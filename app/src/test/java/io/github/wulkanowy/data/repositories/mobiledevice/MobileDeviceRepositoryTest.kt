package io.github.wulkanowy.data.repositories.mobiledevice

import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.MobileDevice
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.repositories.UnitTestInternetObservingStrategy
import io.github.wulkanowy.getStudentEntity
import io.reactivex.Maybe
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.threeten.bp.LocalDateTime.of

class MobileDeviceRepositoryTest {

    @Mock
    private lateinit var semester: Semester

    @Mock
    private lateinit var mobileDeviceRemote: MobileDeviceRemote

    @Mock
    private lateinit var mobileDeviceLocal: MobileDeviceLocal

    private val student = getStudentEntity()

    private lateinit var mobileDeviceRepository: MobileDeviceRepository

    private val settings = InternetObservingSettings.builder()
        .strategy(UnitTestInternetObservingStrategy())
        .build()

    @Before
    fun initTest() {
        MockitoAnnotations.initMocks(this)
        mobileDeviceRepository = MobileDeviceRepository(settings, mobileDeviceLocal, mobileDeviceRemote)
    }

    @Test
    fun getDevices() {
        val devices = listOf(
            getDeviceEntity(1),
            getDeviceEntity(2)
        )

        doReturn(Maybe.empty<MobileDevice>()).`when`(mobileDeviceLocal).getDevices(semester)
        doReturn(Single.just(devices)).`when`(mobileDeviceRemote).getDevices(student, semester)

        mobileDeviceRepository.getDevices(student, semester).blockingGet()

        verify(mobileDeviceLocal).deleteDevices(emptyList())
        verify(mobileDeviceLocal).saveDevices(devices)
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
