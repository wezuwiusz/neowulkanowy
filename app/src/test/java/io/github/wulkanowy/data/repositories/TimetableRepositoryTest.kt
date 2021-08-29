package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.db.dao.TimetableAdditionalDao
import io.github.wulkanowy.data.db.dao.TimetableDao
import io.github.wulkanowy.data.db.dao.TimetableHeaderDao
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.getSemesterEntity
import io.github.wulkanowy.getStudentEntity
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.sdk.pojo.TimetableFull
import io.github.wulkanowy.services.alarm.TimetableNotificationSchedulerHelper
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
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalDateTime.of
import io.github.wulkanowy.sdk.pojo.Timetable as SdkTimetable

class TimetableRepositoryTest {

    @MockK(relaxed = true)
    private lateinit var timetableNotificationSchedulerHelper: TimetableNotificationSchedulerHelper

    @SpyK
    private var sdk = Sdk()

    @MockK
    private lateinit var timetableDb: TimetableDao

    @MockK
    private lateinit var timetableAdditionalDao: TimetableAdditionalDao

    @MockK
    private lateinit var timetableHeaderDao: TimetableHeaderDao

    @MockK(relaxUnitFun = true)
    private lateinit var refreshHelper: AutoRefreshHelper

    private val student = getStudentEntity()

    private val semester = getSemesterEntity()

    private lateinit var timetableRepository: TimetableRepository

    private val startDate = LocalDate.of(2021, 1, 4)

    private val endDate = LocalDate.of(2021, 1, 10)

    @Before
    fun initApi() {
        MockKAnnotations.init(this)
        every { refreshHelper.isShouldBeRefreshed(any()) } returns false

        timetableRepository = TimetableRepository(timetableDb, timetableAdditionalDao, timetableHeaderDao, sdk, timetableNotificationSchedulerHelper, refreshHelper)
    }

    @Test
    fun copyRoomToCompletedFromPrevious() {
        // prepare
        val remoteList = listOf(
            createTimetableRemote(of(2021, 1, 4, 8, 0), 1, "", "Przyroda"),
            createTimetableRemote(of(2021, 1, 4, 8, 50), 2, "", "Religia"),
            createTimetableRemote(of(2021, 1, 4, 9, 40), 3, "", "W-F"),
            createTimetableRemote(of(2021, 1, 4, 10, 30), 4, "", "W-F")
        )
        coEvery { sdk.getTimetableFull(any(), any()) } returns TimetableFull(emptyList(), remoteList, emptyList())

        val localList = listOf(
            createTimetableRemote(of(2021, 1, 4, 8, 0), 1, "123", "Przyroda"),
            createTimetableRemote(of(2021, 1, 4, 8, 50), 2, "321", "Religia"),
            createTimetableRemote(of(2021, 1, 4, 9, 40), 3, "213", "W-F"),
            createTimetableRemote(of(2021, 1, 4, 10, 30), 3, "213", "W-F", "Jan Kowalski")
        )
        coEvery { timetableDb.loadAll(semester.diaryId, semester.studentId, startDate, endDate) } returns flowOf(localList.mapToEntities(semester))
        coEvery { timetableDb.insertAll(any()) } returns listOf(1, 2, 3)
        coEvery { timetableDb.deleteAll(any()) } just Runs

        coEvery { timetableAdditionalDao.loadAll(1, 1, startDate, endDate) } returns flowOf(listOf())
        coEvery { timetableAdditionalDao.insertAll(emptyList()) } returns listOf(1, 2, 3)
        coEvery { timetableAdditionalDao.deleteAll(emptyList()) } just Runs

        coEvery { timetableHeaderDao.loadAll(1, 1, startDate, endDate) } returns flowOf(listOf())
        coEvery { timetableHeaderDao.insertAll(emptyList()) } returns listOf(1, 2, 3)
        coEvery { timetableHeaderDao.deleteAll(emptyList()) } just Runs

        // execute
        val res = runBlocking {
            timetableRepository.getTimetable(student, semester, startDate, endDate, true).toFirstResult()
        }

        // verify
        assertEquals(4, res.data?.lessons.orEmpty().size)
        coVerify {
            timetableDb.insertAll(withArg {
                assertEquals(4, it.size)
                assertEquals("123", it[0].room)
                assertEquals("321", it[1].room)
                assertEquals("213", it[2].room)
            })
        }
        coVerify { timetableDb.deleteAll(match { it.size == 4 }) }
    }

    @Test
    fun copyTeacherToCompletedFromPrevious() {
        // prepare
        val remoteList = listOf(
            createTimetableRemote(of(2021, 1, 4, 8, 0), 1, "123", "Matematyka", "Paweł Poniedziałkowski", false), // skip
            createTimetableRemote(of(2021, 1, 4, 8, 50), 2, "124", "Matematyka", "Jakub Wtorkowski", true),
            createTimetableRemote(of(2021, 1, 4, 9, 40), 3, "125", "Język polski", "Joanna Poniedziałkowska", false),
            createTimetableRemote(of(2021, 1, 4, 10, 40), 4, "126", "Język polski", "Joanna Wtorkowska", true), // skip

            createTimetableRemote(of(2021, 1, 5, 8, 0), 1, "123", "Język polski", "", false),
            createTimetableRemote(of(2021, 1, 5, 8, 50), 2, "124", "Język polski", "", true),
            createTimetableRemote(of(2021, 1, 5, 9, 40), 3, "125", "Język polski", "", false),
            createTimetableRemote(of(2021, 1, 5, 10, 40), 4, "126", "Język polski", "", true),

            createTimetableRemote(of(2021, 1, 6, 8, 0), 1, "123", "Matematyka", "Paweł Środowski", false),
            createTimetableRemote(of(2021, 1, 6, 8, 50), 2, "124", "Matematyka", "Paweł Czwartkowski", true),
            createTimetableRemote(of(2021, 1, 6, 9, 40), 3, "125", "Matematyka", "Paweł Środowski", false),
            createTimetableRemote(of(2021, 1, 6, 10, 40), 4, "126", "Matematyka", "Paweł Czwartkowski", true)
        )
        coEvery { sdk.getTimetableFull(startDate, endDate) } returns TimetableFull(emptyList(), remoteList, emptyList())

        val localList = listOf(
            createTimetableRemote(of(2021, 1, 4, 8, 0), 1, "123", "Matematyka", "Paweł Poniedziałkowski", false),
            createTimetableRemote(of(2021, 1, 4, 8, 50), 2, "124", "Matematyka", "Paweł Poniedziałkowski", false),
            createTimetableRemote(of(2021, 1, 4, 9, 40), 3, "125", "Język polski", "Joanna Wtorkowska", true),
            createTimetableRemote(of(2021, 1, 4, 10, 40), 4, "126", "Język polski", "Joanna Wtorkowska", true),

            createTimetableRemote(of(2021, 1, 5, 8, 0), 1, "123", "Język polski", "Joanna Wtorkowska", false),
            createTimetableRemote(of(2021, 1, 5, 8, 50), 2, "124", "Język polski", "Joanna Wtorkowska", false),
            createTimetableRemote(of(2021, 1, 5, 9, 40), 3, "125", "Język polski", "Joanna Środowska", true),
            createTimetableRemote(of(2021, 1, 5, 10, 40), 4, "126", "Język polski", "Joanna Środowska", true),

            createTimetableRemote(of(2021, 1, 6, 8, 0), 1, "123", "Matematyka", "", false),
            createTimetableRemote(of(2021, 1, 6, 8, 50), 2, "124", "Matematyka", "", false),
            createTimetableRemote(of(2021, 1, 6, 9, 40), 3, "125", "Matematyka", "", true),
            createTimetableRemote(of(2021, 1, 6, 10, 40), 4, "126", "Matematyka", "", true)
        )
        coEvery { timetableDb.loadAll(semester.diaryId, semester.studentId, startDate, endDate) } returns flowOf(localList.mapToEntities(semester))
        coEvery { timetableDb.insertAll(any()) } returns listOf(1, 2, 3)
        coEvery { timetableDb.deleteAll(any()) } just Runs

        coEvery { timetableAdditionalDao.loadAll(1, 1, startDate, endDate) } returns flowOf(listOf())
        coEvery { timetableAdditionalDao.insertAll(emptyList()) } returns listOf(1, 2, 3)
        coEvery { timetableAdditionalDao.deleteAll(emptyList()) } just Runs

        coEvery { timetableHeaderDao.loadAll(1, 1, startDate, endDate) } returns flowOf(listOf())
        coEvery { timetableHeaderDao.insertAll(emptyList()) } returns listOf(1, 2, 3)
        coEvery { timetableHeaderDao.deleteAll(emptyList()) } just Runs

        // execute
        val res = runBlocking { timetableRepository.getTimetable(student, semester, startDate, endDate, true).toFirstResult() }

        // verify
        assertEquals(null, res.error)
        assertEquals(12, res.data!!.lessons.size)

        coVerify {
            timetableDb.insertAll(withArg {
                assertEquals(10, it.size)
//                assertEquals("Paweł Poniedziałkowski", it[0].teacher) // skip
                assertEquals("Jakub Wtorkowski", it[0].teacher)
                assertEquals("Joanna Poniedziałkowska", it[1].teacher)
//                assertEquals("Joanna Wtorkowska", it[3].teacher) // skip

                assertEquals("Joanna Wtorkowska", it[2].teacher)
                assertEquals("", it[3].teacher)
                assertEquals("", it[4].teacher)
                assertEquals("", it[5].teacher)

                assertEquals("Paweł Środowski", it[6].teacher)
                assertEquals("Paweł Czwartkowski", it[7].teacher)
                assertEquals("Paweł Środowski", it[8].teacher)
                assertEquals("Paweł Czwartkowski", it[9].teacher)
            })
        }
        coVerify { timetableDb.deleteAll(match { it.size == 10 }) }
    }

    @Test
    fun `force refresh without difference`() {
        val remoteList = listOf(
            createTimetableRemote(of(2021, 1, 4, 8, 0), 1, "123", "Język polski", "", false),
            createTimetableRemote(of(2021, 1, 4, 8, 50), 2, "124", "Język polski", "", true)
        )

        // prepare
        coEvery { sdk.getTimetableFull(startDate, endDate) } returns TimetableFull(emptyList(), remoteList, emptyList())
        coEvery { timetableDb.loadAll(1, 1, startDate, endDate) } returnsMany listOf(
            flowOf(remoteList.mapToEntities(semester)),
            flowOf(remoteList.mapToEntities(semester))
        )
        coEvery { timetableDb.insertAll(any()) } returns listOf(1, 2, 3)
        coEvery { timetableDb.deleteAll(any()) } just Runs

        coEvery { timetableAdditionalDao.loadAll(1, 1, startDate, endDate) } returns flowOf(listOf())
        coEvery { timetableAdditionalDao.deleteAll(emptyList()) } just Runs
        coEvery { timetableAdditionalDao.insertAll(emptyList()) } returns listOf(1, 2, 3)

        coEvery { timetableHeaderDao.loadAll(1, 1, startDate, endDate) } returns flowOf(listOf())
        coEvery { timetableHeaderDao.insertAll(emptyList()) } returns listOf(1, 2, 3)
        coEvery { timetableHeaderDao.deleteAll(emptyList()) } just Runs

        // execute
        val res = runBlocking { timetableRepository.getTimetable(student, semester, startDate, endDate, true).toFirstResult() }

        // verify
        assertEquals(null, res.error)
        assertEquals(2, res.data?.lessons?.size)
        coVerify { sdk.getTimetableFull(startDate, endDate) }
        coVerify { timetableDb.loadAll(1, 1, startDate, endDate) }
        coVerify { timetableDb.insertAll(match { it.isEmpty() }) }
        coVerify { timetableDb.deleteAll(match { it.isEmpty() }) }
    }

    private fun createTimetableRemote(start: LocalDateTime, number: Int = 1, room: String = "", subject: String = "", teacher: String = "", changes: Boolean = false) = SdkTimetable(
        number = number,
        start = start,
        end = start.plusMinutes(45),
        date = start.toLocalDate(),
        subject = subject,
        group = "",
        room = room,
        teacher = teacher,
        info = "",
        changes = changes,
        canceled = false,
        roomOld = "",
        subjectOld = "",
        teacherOld = "",
        studentPlan = true
    )
}
