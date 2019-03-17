package io.github.wulkanowy.data.repositories.recipient

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.ApiHelper
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.Recipient
import io.github.wulkanowy.data.db.entities.ReportingUnit
import io.github.wulkanowy.data.db.entities.Student
import io.reactivex.Single
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipientRepository @Inject constructor(
    private val settings: InternetObservingSettings,
    private val local: RecipientLocal,
    private val remote: RecipientRemote,
    private val apiHelper: ApiHelper
) {

    fun getRecipients(student: Student, role: Int, unit: ReportingUnit, forceRefresh: Boolean = false): Single<List<Recipient>> {
        return Single.just(apiHelper.initApi(student))
            .flatMap { _ ->
                local.getRecipients(student, role, unit).filter { !forceRefresh }
                    .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                        .flatMap {
                            if (it) remote.getRecipients(role, unit)
                            else Single.error(UnknownHostException())
                        }.flatMap { new ->
                            local.getRecipients(student, role, unit).toSingle(emptyList())
                                .doOnSuccess { old ->
                                    local.deleteRecipients(old - new)
                                    local.saveRecipients(new - old)
                                }
                        }.flatMap {
                            local.getRecipients(student, role, unit).toSingle(emptyList())
                        }
                    )
            }
    }

    fun getMessageRecipients(student: Student, message: Message): Single<List<Recipient>> {
        return Single.just(apiHelper.initApi(student))
            .flatMap { ReactiveNetwork.checkInternetConnectivity(settings) }
            .flatMap {
                if (it) remote.getMessageRecipients(message)
                else Single.error(UnknownHostException())
            }
    }
}
