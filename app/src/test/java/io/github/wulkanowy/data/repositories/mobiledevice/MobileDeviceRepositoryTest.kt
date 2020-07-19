package io.github.wulkanowy.data.repositories.mobiledevice

import io.github.wulkanowy.data.db.entities.MobileDevice
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.getStudentEntity
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.just
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDateTime.of

class MobileDeviceRepositoryTest {

    @MockK
    private lateinit var semester: Semester

    @MockK
    private lateinit var mobileDeviceRemote: MobileDeviceRemote

    @MockK
    private lateinit var mobileDeviceLocal: MobileDeviceLocal

    private val student = getStudentEntity()

    private lateinit var mobileDeviceRepository: MobileDeviceRepository

    @Before
    fun initTest() {
        MockKAnnotations.init(this)
        mobileDeviceRepository = MobileDeviceRepository(mobileDeviceLocal, mobileDeviceRemote)
    }

    @Test
    fun getDevices() {
        val devices = listOf(
            getDeviceEntity(1),
            getDeviceEntity(2)
        )

        coEvery { mobileDeviceLocal.getDevices(semester) } returns flowOf(emptyList())
        coEvery { mobileDeviceLocal.deleteDevices(emptyList()) } just Runs
        coEvery { mobileDeviceLocal.saveDevices(devices) } just Runs
        coEvery { mobileDeviceRemote.getDevices(student, semester) } returns devices

        runBlocking { mobileDeviceRepository.getDevices(student, semester, true).toList() }

        coVerify { mobileDeviceLocal.deleteDevices(emptyList()) }
        coVerify { mobileDeviceLocal.saveDevices(devices) }
    }

    private fun getDeviceEntity(day: Int): MobileDevice {
        return MobileDevice(
            studentId = 1,
            deviceId = 1,
            name = "",
            date = of(2019, 5, day, 0, 0, 0)
        )
    }
}
