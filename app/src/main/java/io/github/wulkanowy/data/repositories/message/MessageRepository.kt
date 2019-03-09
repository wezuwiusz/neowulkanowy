package io.github.wulkanowy.data.repositories.message

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.api.messages.SentMessage
import io.github.wulkanowy.data.ApiHelper
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.Recipient
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.message.MessageFolder.RECEIVED
import io.reactivex.Completable
import io.reactivex.Single
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepository @Inject constructor(
    private val settings: InternetObservingSettings,
    private val local: MessageLocal,
    private val remote: MessageRemote,
    private val apiHelper: ApiHelper
) {

    fun getMessages(student: Student, folder: MessageFolder, forceRefresh: Boolean = false, notify: Boolean = false): Single<List<Message>> {
        return Single.just(apiHelper.initApi(student))
            .flatMap { _ ->
                local.getMessages(student, folder).filter { !forceRefresh }
                    .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                        .flatMap {
                            if (it) remote.getMessages(student.studentId, folder)
                            else Single.error(UnknownHostException())
                        }.flatMap { new ->
                            local.getMessages(student, folder).toSingle(emptyList())
                                .doOnSuccess { old ->
                                    local.deleteMessages(old - new)
                                    local.saveMessages((new - old)
                                        .onEach {
                                            it.isNotified = !notify
                                        })
                                }
                        }.flatMap { local.getMessages(student, folder).toSingle(emptyList()) }
                    )
            }
    }

    fun getMessage(student: Student, messageId: Int, markAsRead: Boolean = false): Single<Message> {
        return Single.just(apiHelper.initApi(student))
            .flatMap { _ ->
                local.getMessage(student, messageId)
                    .filter { !it.content.isNullOrEmpty() }
                    .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                        .flatMap {
                            if (it) local.getMessage(student, messageId).toSingle()
                            else Single.error(UnknownHostException())
                        }
                        .flatMap { dbMessage ->
                            remote.getMessagesContent(dbMessage, markAsRead).doOnSuccess {
                                local.updateMessages(listOf(dbMessage.copy(unread = false).apply {
                                    id = dbMessage.id
                                    content = it
                                }))
                            }
                        }.flatMap {
                            local.getMessage(student, messageId).toSingle()
                        }
                    )
            }
    }

    fun getNotNotifiedMessages(student: Student): Single<List<Message>> {
        return local.getMessages(student, RECEIVED)
            .map { it.filter { message -> !message.isNotified && message.unread } }
            .toSingle(emptyList())
    }

    fun updateMessage(message: Message): Completable {
        return Completable.fromCallable { local.updateMessages(listOf(message)) }
    }

    fun updateMessages(messages: List<Message>): Completable {
        return Completable.fromCallable { local.updateMessages(messages) }
    }

    fun sendMessage(subject: String, content: String, recipients: List<Recipient>): Single<SentMessage> {
        return remote.sendMessage(subject, content, recipients)
    }
}
