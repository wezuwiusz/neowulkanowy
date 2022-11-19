package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.dataOrNull
import io.github.wulkanowy.data.db.dao.AttendanceDao
import io.github.wulkanowy.data.db.dao.TimetableDao
import io.github.wulkanowy.data.errorOrNull
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.data.toFirstResult
import io.github.wulkanowy.getSemesterEntity
import io.github.wulkanowy.getStudentEntity
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.AutoRefreshHelper
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDate.of
import io.github.wulkanowy.sdk.pojo.Attendance as SdkAttendance

class AttendanceRepositoryTest {

    @SpyK
    private var sdk = Sdk()

    @MockK
    private lateinit var attendanceDb: AttendanceDao

    @MockK
    private lateinit var timetableDb: TimetableDao

    @MockK(relaxUnitFun = true)
    private lateinit var refreshHelper: AutoRefreshHelper

    private val semester = getSemesterEntity()

    private val student = getStudentEntity()

    private lateinit var attendanceRepository: AttendanceRepository

    private val remoteList = listOf(
        getAttendanceRemote(of(2021, 1, 4)),
        getAttendanceRemote(of(2021, 1, 7))
    )

    private val startDate = of(2021, 1, 4)

    private val endDate = of(2021, 1, 10)

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        every { refreshHelper.shouldBeRefreshed(any()) } returns false
        coEvery { timetableDb.load(any(), any(), any(), any()) } returns emptyList()

        attendanceRepository = AttendanceRepository(attendanceDb, timetableDb, sdk, refreshHelper)
    }

    @Test
    fun `force refresh without difference`() {
        // prepare
        coEvery { sdk.getAttendance(startDate, endDate, 1) } returns remoteList
        coEvery { attendanceDb.loadAll(1, 1, startDate, endDate) } returnsMany listOf(
            flowOf(remoteList.mapToEntities(semester, emptyList())),
            flowOf(remoteList.mapToEntities(semester, emptyList()))
        )
        coEvery { attendanceDb.insertAll(any()) } returns listOf(1, 2, 3)
        coEvery { attendanceDb.deleteAll(any()) } just Runs

        // execute
        val res = runBlocking { attendanceRepository.getAttendance(student, semester, startDate, endDate, true).toFirstResult() }

        // verify
        assertEquals(null, res.errorOrNull)
        assertEquals(2, res.dataOrNull?.size)
        coVerify { sdk.getAttendance(startDate, endDate, 1) }
        coVerify { attendanceDb.loadAll(1, 1, startDate, endDate) }
        coVerify { attendanceDb.insertAll(match { it.isEmpty() }) }
        coVerify { attendanceDb.deleteAll(match { it.isEmpty() }) }
    }

    @Test
    fun `force refresh with more items in remote`() {
        // prepare
        coEvery { sdk.getAttendance(startDate, endDate, 1) } returns remoteList
        coEvery { attendanceDb.loadAll(1, 1, startDate, endDate) } returnsMany listOf(
            flowOf(remoteList.dropLast(1).mapToEntities(semester, emptyList())),
            flowOf(remoteList.dropLast(1).mapToEntities(semester, emptyList())), // after fetch end before save result
            flowOf(remoteList.mapToEntities(semester, emptyList()))
        )
        coEvery { attendanceDb.insertAll(any()) } returns listOf(1, 2, 3)
        coEvery { attendanceDb.deleteAll(any()) } just Runs

        // execute
        val res = runBlocking { attendanceRepository.getAttendance(student, semester, startDate, endDate, true).toFirstResult() }

        // verify
        assertEquals(null, res.errorOrNull)
        assertEquals(2, res.dataOrNull?.size)
        coVerify { sdk.getAttendance(startDate, endDate, 1) }
        coVerify { attendanceDb.loadAll(1, 1, startDate, endDate) }
        coVerify {
            attendanceDb.insertAll(match {
                it.size == 1 && it[0] == remoteList.mapToEntities(semester, emptyList())[1]
            })
        }
        coVerify { attendanceDb.deleteAll(match { it.isEmpty() }) }
    }

    @Test
    fun `force refresh with more items in local`() {
        // prepare
        coEvery { sdk.getAttendance(startDate, endDate, 1) } returns remoteList.dropLast(1)
        coEvery { attendanceDb.loadAll(1, 1, startDate, endDate) } returnsMany listOf(
            flowOf(remoteList.mapToEntities(semester, emptyList())),
            flowOf(remoteList.mapToEntities(semester, emptyList())), // after fetch end before save result
            flowOf(remoteList.dropLast(1).mapToEntities(semester, emptyList()))
        )
        coEvery { attendanceDb.insertAll(any()) } returns listOf(1, 2, 3)
        coEvery { attendanceDb.deleteAll(any()) } just Runs

        // execute
        val res = runBlocking { attendanceRepository.getAttendance(student, semester, startDate, endDate, true).toFirstResult() }

        // verify
        assertEquals(null, res.errorOrNull)
        assertEquals(1, res.dataOrNull?.size)
        coVerify { sdk.getAttendance(startDate, endDate, 1) }
        coVerify { attendanceDb.loadAll(1, 1, startDate, endDate) }
        coVerify { attendanceDb.insertAll(match { it.isEmpty() }) }
        coVerify {
            attendanceDb.deleteAll(match {
                it.size == 1 && it[0] == remoteList.mapToEntities(semester, emptyList())[1]
            })
        }
    }

    private fun getAttendanceRemote(date: LocalDate) = SdkAttendance(
        subject = "Fizyka",
        name = "Obecność",
        date = date,
        timeId = 0,
        number = 0,
        deleted = false,
        excusable = false,
        excused = false,
        exemption = false,
        lateness = false,
        presence = false,
        categoryId = 1,
        absence = false,
        excuseStatus = null
    )
}
