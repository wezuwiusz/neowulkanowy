package io.github.wulkanowy.data

import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerifyOrder
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ResourceTest {

    private val testScope = TestScope(UnconfinedTestDispatcher())

    @Test
    fun `fetch from two places with same remote data`() {
        val repo = mockk<TestRepo>()
        coEvery { repo.query() } returnsMany listOf(
            // initial data
            flowOf(listOf(1, 2, 3)),
            flowOf(listOf(1, 2, 3)),

            // for first
            flowOf(listOf(1, 2, 3)), // before save
            flowOf(listOf(2, 3, 4)), // after save

            // for second
            flowOf(listOf(2, 3, 4)), // before save
            flowOf(listOf(2, 3, 4)), // after save
        )
        coEvery { repo.fetch() } returnsMany listOf(
            listOf(2, 3, 4),
            listOf(2, 3, 4),
        )
        coEvery { repo.save(any(), any()) } just Runs

        // first
        networkBoundResource(
            isResultEmpty = { false },
            query = { repo.query() },
            fetch = {
                val data = repo.fetch()
                delay(2_000)
                data
            },
            saveFetchResult = { old, new -> repo.save(old, new) }
        ).launchIn(testScope)

        testScope.advanceTimeBy(1_000)

        // second
        networkBoundResource(
            isResultEmpty = { false },
            query = { repo.query() },
            fetch = {
                val data = repo.fetch()
                delay(2_000)
                data
            },
            saveFetchResult = { old, new -> repo.save(old, new) }
        ).launchIn(testScope)

        testScope.advanceTimeBy(3_000)

        coVerifyOrder {
            // from first
            repo.query()
            repo.fetch() // hang for 2 sec

            // wait 1 sec

            // from second
            repo.query()
            repo.fetch() // hang for 2 sec

            // from first
            repo.query()
            repo.save(withArg {
                assertEquals(listOf(1, 2, 3), it)
            }, any())
            repo.query()

            // from second
            repo.query()
            repo.save(withArg {
                assertEquals(listOf(2, 3, 4), it)
            }, any())
            repo.query()
        }
    }

    @Test
    fun `fetch from two places with same remote data and save at the same moment`() {
        val repo = mockk<TestRepo>()
        coEvery { repo.query() } returnsMany listOf(
            // initial data
            flowOf(listOf(1, 2, 3)),
            flowOf(listOf(1, 2, 3)),

            // for first
            flowOf(listOf(1, 2, 3)), // before save
            flowOf(listOf(2, 3, 4)), // after save

            // for second
            flowOf(listOf(2, 3, 4)), // before save
            flowOf(listOf(2, 3, 4)), // after save
        )
        coEvery { repo.fetch() } returnsMany listOf(
            listOf(2, 3, 4),
            listOf(2, 3, 4),
        )
        coEvery { repo.save(any(), any()) } just Runs

        val saveResultMutex = Mutex()

        // first
        networkBoundResource(
            isResultEmpty = { false },
            mutex = saveResultMutex,
            query = { repo.query() },
            fetch = {
                val data = repo.fetch()
                delay(2_000)
                data
            },
            saveFetchResult = { old, new ->
                delay(1_500)
                repo.save(old, new)
            }
        ).launchIn(testScope)

        testScope.advanceTimeBy(1_000)

        // second
        networkBoundResource(
            isResultEmpty = { false },
            mutex = saveResultMutex,
            query = { repo.query() },
            fetch = {
                val data = repo.fetch()
                delay(2_000)
                data
            },
            saveFetchResult = { old, new ->
                repo.save(old, new)
            }
        ).launchIn(testScope)

        testScope.advanceTimeBy(3_000)

        coVerifyOrder {
            // from first
            repo.query()
            repo.fetch() // hang for 2 sec

            // wait 1 sec

            // from second
            repo.query()
            repo.fetch() // hang for 2 sec

            // from first
            repo.query()
            repo.save(withArg {
                assertEquals(listOf(1, 2, 3), it)
            }, any())

            // from second
            repo.query()
            repo.save(withArg {
                assertEquals(listOf(2, 3, 4), it)
            }, any())

            repo.query()
            repo.query()
        }
    }

    @Suppress("UNUSED_PARAMETER", "RedundantSuspendModifier")
    private class TestRepo {
        fun query() = flowOf<List<Int>>()
        suspend fun fetch() = listOf<Int>()
        suspend fun save(old: List<Int>, new: List<Int>) {}
    }
}
