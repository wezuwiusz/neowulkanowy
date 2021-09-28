package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.db.dao.MobileDeviceDao
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.getSemesterEntity
import io.github.wulkanowy.getStudentEntity
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.sdk.pojo.Device
import io.github.wulkanowy.utils.AutoRefreshHelper
import io.github.wulkanowy.utils.toFirstResult
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.just
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime.of

class MobileDeviceRepositoryTest {

    @SpyK
    private var sdk = Sdk()

    @MockK
    private lateinit var mobileDeviceDb: MobileDeviceDao

    @MockK(relaxUnitFun = true)
    private lateinit var refreshHelper: AutoRefreshHelper

    private val semester = getSemesterEntity()

    private val student = getStudentEntity()

    private lateinit var mobileDeviceRepository: MobileDeviceRepository

    private val remoteList = listOf(
        getDevicePojo(1),
        getDevicePojo(2)
    )

    @Before
    fun initTest() {
        MockKAnnotations.init(this)
        every { refreshHelper.shouldBeRefreshed(any()) } returns false

        mobileDeviceRepository = MobileDeviceRepository(mobileDeviceDb, sdk, refreshHelper)
    }

    @Test
    fun `force refresh without difference`() {
        // prepare
        coEvery { sdk.getRegisteredDevices() } returns remoteList
        coEvery { mobileDeviceDb.loadAll(student.studentId) } returnsMany listOf(
            flowOf(remoteList.mapToEntities(semester)),
            flowOf(remoteList.mapToEntities(semester))
        )
        coEvery { mobileDeviceDb.insertAll(any()) } returns listOf(1, 2, 3)
        coEvery { mobileDeviceDb.deleteAll(any()) } just Runs

        // execute
        val res = runBlocking { mobileDeviceRepository.getDevices(student, semester, true).toFirstResult() }

        // verify
        Assert.assertEquals(null, res.error)
        Assert.assertEquals(2, res.data?.size)
        coVerify { sdk.getRegisteredDevices() }
        coVerify { mobileDeviceDb.loadAll(1) }
        coVerify { mobileDeviceDb.insertAll(match { it.isEmpty() }) }
        coVerify { mobileDeviceDb.deleteAll(match { it.isEmpty() }) }
    }

    @Test
    fun `force refresh with more items in remote`() {
        // prepare
        coEvery { sdk.getRegisteredDevices() } returns remoteList
        coEvery { mobileDeviceDb.loadAll(1) } returnsMany listOf(
            flowOf(remoteList.dropLast(1).mapToEntities(semester)),
            flowOf(remoteList.dropLast(1).mapToEntities(semester)), // after fetch end before save result
            flowOf(remoteList.mapToEntities(semester))
        )
        coEvery { mobileDeviceDb.insertAll(any()) } returns listOf(1, 2, 3)
        coEvery { mobileDeviceDb.deleteAll(any()) } just Runs

        // execute
        val res = runBlocking { mobileDeviceRepository.getDevices(student, semester, true).toFirstResult() }

        // verify
        Assert.assertEquals(null, res.error)
        Assert.assertEquals(2, res.data?.size)
        coVerify { sdk.getRegisteredDevices() }
        coVerify { mobileDeviceDb.loadAll(1) }
        coVerify {
            mobileDeviceDb.insertAll(match {
                it.size == 1 && it[0] == remoteList.mapToEntities(semester)[1]
            })
        }
        coVerify { mobileDeviceDb.deleteAll(match { it.isEmpty() }) }
    }

    @Test
    fun `force refresh with more items in local`() {
        // prepare
        coEvery { sdk.getRegisteredDevices() } returns remoteList.dropLast(1)
        coEvery { mobileDeviceDb.loadAll(1) } returnsMany listOf(
            flowOf(remoteList.mapToEntities(semester)),
            flowOf(remoteList.mapToEntities(semester)), // after fetch end before save result
            flowOf(remoteList.dropLast(1).mapToEntities(semester))
        )
        coEvery { mobileDeviceDb.insertAll(any()) } returns listOf(1, 2, 3)
        coEvery { mobileDeviceDb.deleteAll(any()) } just Runs

        // execute
        val res = runBlocking { mobileDeviceRepository.getDevices(student, semester, true).toFirstResult() }

        // verify
        Assert.assertEquals(null, res.error)
        Assert.assertEquals(1, res.data?.size)
        coVerify { sdk.getRegisteredDevices() }
        coVerify { mobileDeviceDb.loadAll(1) }
        coVerify { mobileDeviceDb.insertAll(match { it.isEmpty() }) }
        coVerify {
            mobileDeviceDb.deleteAll(match {
                it.size == 1 && it[0] == remoteList.mapToEntities(semester)[1]
            })
        }
    }

    private fun getDevicePojo(day: Int) = Device(
        id = 0,
        name = "",
        deviceId = "",
        createDate = of(2019, 5, day, 0, 0, 0),
        modificationDate = of(2019, 5, day, 0, 0, 0)
    )
}
