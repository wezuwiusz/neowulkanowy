package io.github.wulkanowy.data.repositories.message

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.SdkHelper
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.Recipient
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.message.MessageFolder.RECEIVED
import io.github.wulkanowy.sdk.pojo.SentMessage
import io.github.wulkanowy.utils.uniqueSubtract
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import timber.log.Timber
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepository @Inject constructor(
    private val settings: InternetObservingSettings,
    private val local: MessageLocal,
    private val remote: MessageRemote,
    private val sdkHelper: SdkHelper
) {

    fun getMessages(student: Student, semester: Semester, folder: MessageFolder, forceRefresh: Boolean = false, notify: Boolean = false): Single<List<Message>> {
        return Single.just(sdkHelper.init(student))
            .flatMap { _ ->
                local.getMessages(student, folder).filter { !forceRefresh }
                    .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                        .flatMap {
                            if (it) remote.getMessages(student, semester, folder)
                            else Single.error(UnknownHostException())
                        }.flatMap { new ->
                            local.getMessages(student, folder).toSingle(emptyList())
                                .doOnSuccess { old ->
                                    local.deleteMessages(old.uniqueSubtract(new))
                                    local.saveMessages(new.uniqueSubtract(old)
                                        .onEach {
                                            it.isNotified = !notify
                                        })
                                }
                        }.flatMap { local.getMessages(student, folder).toSingle(emptyList()) }
                    )
            }
    }

    fun getMessage(student: Student, messageDbId: Long, markAsRead: Boolean = false): Single<Message> {
        return Single.just(sdkHelper.init(student))
            .flatMap { _ ->
                local.getMessage(messageDbId)
                    .filter {
                        it.content.isNotEmpty().also { status ->
                            Timber.d("Message content in db empty: ${!status}")
                        } && !it.unread
                    }
                    .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                        .flatMap {
                            if (it) local.getMessage(messageDbId)
                            else Single.error(UnknownHostException())
                        }
                        .flatMap { dbMessage ->
                            remote.getMessagesContent(dbMessage, markAsRead).doOnSuccess {
                                local.updateMessages(listOf(dbMessage.copy(unread = !markAsRead).apply {
                                    id = dbMessage.id
                                    content = content.ifBlank { it }
                                }))
                                Timber.d("Message $messageDbId with blank content: ${dbMessage.content.isBlank()}, marked as read")
                            }
                        }.flatMap {
                            local.getMessage(messageDbId)
                        }
                    )
            }
    }

    fun getNotNotifiedMessages(student: Student): Single<List<Message>> {
        return local.getMessages(student, RECEIVED)
            .map { it.filter { message -> !message.isNotified && message.unread } }
            .toSingle(emptyList())
    }

    fun updateMessages(messages: List<Message>): Completable {
        return Completable.fromCallable { local.updateMessages(messages) }
    }

    fun sendMessage(subject: String, content: String, recipients: List<Recipient>): Single<SentMessage> {
        return ReactiveNetwork.checkInternetConnectivity(settings)
            .flatMap {
                if (it) remote.sendMessage(subject, content, recipients)
                else Single.error(UnknownHostException())
            }
    }

    fun deleteMessage(message: Message): Single<Boolean> {
        return ReactiveNetwork.checkInternetConnectivity(settings)
            .flatMap {
                if (it) remote.deleteMessage(message)
                else Single.error(UnknownHostException())
            }
            .doOnSuccess {
                if (!message.removed) local.updateMessages(listOf(message.copy(removed = true).apply {
                    id = message.id
                    content = message.content
                }))
                else local.deleteMessages(listOf(message))
            }
    }
}
