package io.github.wulkanowy.data.repositories

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.ApiHelper
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.local.MessagesLocal
import io.github.wulkanowy.data.repositories.remote.MessagesRemote
import io.reactivex.Completable
import io.reactivex.Single
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagesRepository @Inject constructor(
    private val settings: InternetObservingSettings,
    private val local: MessagesLocal,
    private val remote: MessagesRemote,
    private val apiHelper: ApiHelper
) {

    enum class MessageFolder(val id: Int = 1) {
        RECEIVED(1),
        SENT(2),
        TRASHED(3)
    }

    fun getMessages(student: Student, folder: MessageFolder, forceRefresh: Boolean = false, notify: Boolean = false): Single<List<Message>> {
        return Single.just(apiHelper.initApi(student))
            .flatMap { _ ->
                local.getMessages(student.studentId, folder).filter { !forceRefresh }
                    .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                        .flatMap {
                            if (it) remote.getMessages(student.studentId, folder)
                            else Single.error(UnknownHostException())
                        }.flatMap { new ->
                            local.getMessages(student.studentId, folder).toSingle(emptyList())
                                .doOnSuccess { old ->
                                    local.deleteMessages(old - new)
                                    local.saveMessages((new - old)
                                        .onEach {
                                            it.isNotified = !notify
                                        })
                                }
                        }.flatMap { local.getMessages(student.studentId, folder).toSingle(emptyList()) }
                    )
            }
    }

    fun getMessage(student: Student, messageId: Int, markAsRead: Boolean = false): Single<Message> {
        return Single.just(apiHelper.initApi(student))
            .flatMap { _ ->
                local.getMessage(student.studentId, messageId)
                    .filter { !it.content.isNullOrEmpty() }
                    .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                        .flatMap {
                            if (it) local.getMessage(student.studentId, messageId).toSingle()
                            else Single.error(UnknownHostException())
                        }
                        .flatMap { dbMessage ->
                            remote.getMessagesContent(dbMessage, markAsRead).doOnSuccess {
                                local.updateMessage(dbMessage.copy(unread = false).apply {
                                    id = dbMessage.id
                                    content = it
                                })
                            }
                        }.flatMap {
                            local.getMessage(student.studentId, messageId).toSingle()
                        }
                    )
            }
    }

    fun getNewMessages(student: Student): Single<List<Message>> {
        return local.getNewMessages(student).toSingle(emptyList())
    }

    fun updateMessage(message: Message): Completable {
        return Completable.fromCallable { local.updateMessage(message) }
    }

    fun updateMessages(messages: List<Message>): Completable {
        return Completable.fromCallable { local.updateMessages(messages) }
    }
}
