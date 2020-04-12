package io.github.wulkanowy.data.repositories.mobiledevice

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.MobileDevice
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.pojos.MobileDeviceToken
import io.github.wulkanowy.utils.uniqueSubtract
import io.reactivex.Single
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MobileDeviceRepository @Inject constructor(
    private val settings: InternetObservingSettings,
    private val local: MobileDeviceLocal,
    private val remote: MobileDeviceRemote
) {

    fun getDevices(student: Student, semester: Semester, forceRefresh: Boolean = false): Single<List<MobileDevice>> {
        return local.getDevices(semester).filter { !forceRefresh }
            .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                .flatMap {
                    if (it) remote.getDevices(student, semester)
                    else Single.error(UnknownHostException())
                }.flatMap { new ->
                    local.getDevices(semester).toSingle(emptyList())
                        .doOnSuccess { old ->
                            local.deleteDevices(old uniqueSubtract new)
                            local.saveDevices(new uniqueSubtract old)
                        }
                }
            ).flatMap { local.getDevices(semester).toSingle(emptyList()) }
    }

    fun unregisterDevice(student: Student, semester: Semester, device: MobileDevice): Single<Boolean> {
        return ReactiveNetwork.checkInternetConnectivity(settings)
            .flatMap {
                if (it) remote.unregisterDevice(student, semester, device)
                else Single.error(UnknownHostException())
            }
    }

    fun getToken(student: Student, semester: Semester): Single<MobileDeviceToken> {
        return ReactiveNetwork.checkInternetConnectivity(settings)
            .flatMap {
                if (it) remote.getToken(student, semester)
                else Single.error(UnknownHostException())
            }
    }
}
