package io.github.wulkanowy.ui.modules.message.tab

import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.enums.MessageFolder
import io.github.wulkanowy.data.repositories.MessageRepository
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.afterLoading
import io.github.wulkanowy.utils.flowWithResourceIn
import io.github.wulkanowy.utils.toFormattedString
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import me.xdrop.fuzzywuzzy.FuzzySearch
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.pow

class MessageTabPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val messageRepository: MessageRepository,
    private val semesterRepository: SemesterRepository,
    private val analytics: AnalyticsHelper
) : BasePresenter<MessageTabView>(errorHandler, studentRepository) {

    lateinit var folder: MessageFolder

    private lateinit var lastError: Throwable

    private var lastSearchQuery = ""

    private var messages = emptyList<Message>()

    private val searchChannel = Channel<String>()

    fun onAttachView(view: MessageTabView, folder: MessageFolder) {
        super.onAttachView(view)
        view.initView()
        initializeSearchStream()
        errorHandler.showErrorMessage = ::showErrorViewOnError
        this.folder = folder
    }

    fun onSwipeRefresh() {
        Timber.i("Force refreshing the $folder message")
        view?.run { onParentViewLoadData(true, onlyUnread, onlyWithAttachments) }
    }

    fun onRetry() {
        view?.run {
            showErrorView(false)
            showProgress(true)
            loadData(true, onlyUnread == true, onlyWithAttachments)
        }
    }

    fun onDetailsClick() {
        view?.showErrorDetailsDialog(lastError)
    }

    fun onDeleteMessage() {
        view?.run { loadData(true, onlyUnread == true, onlyWithAttachments) }
    }

    fun onParentViewLoadData(
        forceRefresh: Boolean,
        onlyUnread: Boolean? = view?.onlyUnread,
        onlyWithAttachments: Boolean = view?.onlyWithAttachments == true
    ) {
        loadData(forceRefresh, onlyUnread == true, onlyWithAttachments)
    }

    fun onMessageItemSelected(message: Message, position: Int) {
        Timber.i("Select message ${message.id} item (position: $position)")
        view?.openMessage(message)
    }

    fun onUnreadFilterSelected(isChecked: Boolean) {
        view?.run {
            onlyUnread = isChecked
            onParentViewLoadData(false, onlyUnread, onlyWithAttachments)
        }
    }

    fun onAttachmentsFilterSelected(isChecked: Boolean) {
        view?.run {
            onlyWithAttachments = isChecked
            onParentViewLoadData(false, onlyUnread, onlyWithAttachments)
        }
    }

    private fun loadData(
        forceRefresh: Boolean,
        onlyUnread: Boolean,
        onlyWithAttachments: Boolean
    ) {
        Timber.i("Loading $folder message data started")

        flowWithResourceIn {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)
            messageRepository.getMessages(student, semester, folder, forceRefresh)
        }.onEach {
            when (it.status) {
                Status.LOADING -> {
                    if (!it.data.isNullOrEmpty()) {
                        view?.run {
                            enableSwipe(true)
                            showErrorView(false)
                            showRefresh(true)
                            showProgress(false)
                            showContent(true)
                            messages = it.data
                            val filteredData = getFilteredData(
                                lastSearchQuery,
                                onlyUnread,
                                onlyWithAttachments
                            )
                            val newItems = listOf(MessageTabDataItem.Header) + filteredData.map {
                                MessageTabDataItem.MessageItem(it)
                            }
                            updateData(newItems, folder.id == MessageFolder.SENT.id)
                            notifyParentDataLoaded()
                        }
                    }
                }
                Status.SUCCESS -> {
                    Timber.i("Loading $folder message result: Success")
                    messages = it.data!!
                    updateData(getFilteredData(lastSearchQuery, onlyUnread, onlyWithAttachments))
                    analytics.logEvent(
                        "load_data",
                        "type" to "messages",
                        "items" to it.data.size,
                        "folder" to folder.name
                    )
                }
                Status.ERROR -> {
                    Timber.i("Loading $folder message result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                }
            }
        }.afterLoading {
            view?.run {
                showRefresh(false)
                showProgress(false)
                enableSwipe(true)
                notifyParentDataLoaded()
            }
        }.launch()
    }

    private fun showErrorViewOnError(message: String, error: Throwable) {
        view?.run {
            if (isViewEmpty) {
                lastError = error
                setErrorDetails(message)
                showErrorView(true)
                showEmpty(false)
            } else showError(message, error)
        }
    }

    fun onSearchQueryTextChange(query: String) {
        presenterScope.launch {
            searchChannel.send(query)
        }
    }

    @OptIn(FlowPreview::class)
    private fun initializeSearchStream() {
        presenterScope.launch {
            searchChannel.consumeAsFlow()
                .debounce(250)
                .map { query ->
                    lastSearchQuery = query
                    val isOnlyUnread = view?.onlyUnread == true
                    val isOnlyWithAttachments = view?.onlyWithAttachments == true
                    getFilteredData(query, isOnlyUnread, isOnlyWithAttachments)
                }
                .catch { Timber.e(it) }
                .collect {
                    Timber.d("Applying filter. Full list: ${messages.size}, filtered: ${it.size}")
                    updateData(it)
                    view?.resetListPosition()
                }
        }
    }

    private fun getFilteredData(
        query: String,
        onlyUnread: Boolean = false,
        onlyWithAttachments: Boolean = false
    ): List<Message> {
        if (query.trim().isEmpty()) {
            val sortedMessages = messages.sortedByDescending { it.date }
            return when {
                onlyUnread && onlyWithAttachments -> sortedMessages.filter { it.unread == onlyUnread && it.hasAttachments == onlyWithAttachments }
                onlyUnread -> sortedMessages.filter { it.unread == onlyUnread }
                onlyWithAttachments -> sortedMessages.filter { it.hasAttachments == onlyWithAttachments }
                else -> sortedMessages
            }
        } else {
            val sortedMessages = messages
                .map { it to calculateMatchRatio(it, query) }
                .sortedWith(compareBy<Pair<Message, Int>> { -it.second }.thenByDescending { it.first.date })
                .filter { it.second > 6000 }
                .map { it.first }
            return when {
                onlyUnread && onlyWithAttachments -> sortedMessages.filter { it.unread == onlyUnread && it.hasAttachments == onlyWithAttachments }
                onlyUnread -> sortedMessages.filter { it.unread == onlyUnread }
                onlyWithAttachments -> sortedMessages.filter { it.hasAttachments == onlyWithAttachments }
                else -> sortedMessages
            }
        }
    }

    private fun updateData(data: List<Message>) {
        view?.run {
            showEmpty(data.isEmpty())
            showContent(true)
            showErrorView(false)
            val newItems =
                listOf(MessageTabDataItem.Header) + data.map { MessageTabDataItem.MessageItem(it) }
            updateData(newItems, folder.id == MessageFolder.SENT.id)
        }
    }

    private fun calculateMatchRatio(message: Message, query: String): Int {
        val subjectRatio = FuzzySearch.tokenSortPartialRatio(query.lowercase(), message.subject)

        val senderOrRecipientRatio = FuzzySearch.tokenSortPartialRatio(
            query.lowercase(),
            if (message.sender.isNotEmpty()) message.sender.lowercase()
            else message.recipient.lowercase()
        )

        val dateRatio = listOf(
            FuzzySearch.ratio(
                query.lowercase(),
                message.date.toFormattedString("dd.MM").lowercase()
            ),
            FuzzySearch.ratio(
                query.lowercase(),
                message.date.toFormattedString("dd.MM.yyyy").lowercase()
            )
        ).maxOrNull() ?: 0


        return (subjectRatio.toDouble().pow(2)
            + senderOrRecipientRatio.toDouble().pow(2)
            + dateRatio.toDouble().pow(2) * 2
            ).toInt()
    }
}
