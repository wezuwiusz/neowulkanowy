package io.github.wulkanowy.data.repositories.luckynumber

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.SdkHelper
import io.github.wulkanowy.data.db.entities.LuckyNumber
import io.github.wulkanowy.data.db.entities.Student
import io.reactivex.Completable
import io.reactivex.Maybe
import org.threeten.bp.LocalDate
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LuckyNumberRepository @Inject constructor(
    private val settings: InternetObservingSettings,
    private val local: LuckyNumberLocal,
    private val remote: LuckyNumberRemote,
    private val sdkHelper: SdkHelper
) {

    fun getLuckyNumber(student: Student, forceRefresh: Boolean = false, notify: Boolean = false): Maybe<LuckyNumber> {
        return Maybe.just(sdkHelper.init(student)).flatMap {
            local.getLuckyNumber(student, LocalDate.now()).filter { !forceRefresh }
                .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                    .flatMapMaybe {
                        if (it) remote.getLuckyNumber(student)
                        else Maybe.error(UnknownHostException())
                    }.flatMap { new ->
                        local.getLuckyNumber(student, LocalDate.now())
                            .doOnSuccess { old ->
                                if (new != old) {
                                    local.deleteLuckyNumber(old)
                                    local.saveLuckyNumber(new.apply {
                                        if (notify) isNotified = false
                                    })
                                }
                            }
                            .doOnComplete {
                                local.saveLuckyNumber(new.apply {
                                    if (notify) isNotified = false
                                })
                            }
                    }.flatMap({ local.getLuckyNumber(student, LocalDate.now()) }, { Maybe.error(it) },
                        { local.getLuckyNumber(student, LocalDate.now()) })
                )
        }
    }

    fun getNotNotifiedLuckyNumber(student: Student): Maybe<LuckyNumber> {
        return local.getLuckyNumber(student, LocalDate.now()).filter { !it.isNotified }
    }

    fun updateLuckyNumber(luckyNumber: LuckyNumber): Completable {
        return Completable.fromCallable { local.updateLuckyNumber(luckyNumber) }
    }
}
