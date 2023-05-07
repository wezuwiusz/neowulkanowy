package io.github.wulkanowy.ui.modules.message.tab

import io.github.wulkanowy.data.*
import io.github.wulkanowy.data.db.entities.Mailbox
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.enums.MessageFolder
import io.github.wulkanowy.data.repositories.MessageRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.toFormattedString
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import me.xdrop.fuzzywuzzy.FuzzySearch
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.pow

class MessageTabPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val messageRepository: MessageRepository,
    private val analytics: AnalyticsHelper
) : BasePresenter<MessageTabView>(errorHandler, studentRepository) {

    lateinit var folder: MessageFolder

    private lateinit var lastError: Throwable

    private var lastSearchQuery = ""

    private var mailboxes: List<Mailbox> = emptyList()
    private var selectedMailbox: Mailbox? = null

    private var messages = emptyList<Message>()

    private val searchChannel = Channel<String>()

    private val messagesToDelete = mutableSetOf<Message>()

    private var onlyUnread: Boolean? = false

    private var onlyWithAttachments = false

    private var isActionMode = false

    fun onAttachView(view: MessageTabView, folder: MessageFolder) {
        super.onAttachView(view)
        view.initView()
        initializeSearchStream()
        errorHandler.showErrorMessage = ::showErrorViewOnError
        this.folder = folder
    }

    fun onSwipeRefresh() {
        Timber.i("Force refreshing the $folder message")
        view?.run { loadData(true) }
    }

    fun onRetry() {
        view?.run {
            showErrorView(false)
            showProgress(true)
            loadData(true)
        }
    }

    fun onDetailsClick() {
        view?.showErrorDetailsDialog(lastError)
    }

    fun onParentViewLoadData(forceRefresh: Boolean) {
        loadData(forceRefresh)
    }

    fun onParentFinishActionMode() {
        view?.showActionMode(false)
    }

    fun onParentReselected() {
        view?.run {
            if (!isViewEmpty) {
                resetListPosition()
            }
        }
    }

    fun onDestroyActionMode() {
        isActionMode = false
        messagesToDelete.clear()
        updateDataInView()

        view?.run {
            enableSwipe(true)
            notifyParentShowNewMessage(true)
            notifyParentShowActionMode(false)
            showRecyclerBottomPadding(true)
        }
    }

    fun onPrepareActionMode(): Boolean {
        isActionMode = true
        messagesToDelete.clear()
        updateDataInView()

        view?.apply {
            enableSwipe(false)
            notifyParentShowNewMessage(false)
            notifyParentShowActionMode(true)
            showRecyclerBottomPadding(false)
            hideKeyboard()
        }
        return true
    }

    fun onActionModeSelectDelete() {
        Timber.i("Delete ${messagesToDelete.size} messages)")
        val messageList = messagesToDelete.toList()

        presenterScope.launch {
            view?.run {
                showProgress(true)
                showContent(false)
                showActionMode(false)
            }

            runCatching {
                val student = studentRepository.getCurrentStudent(true)
                messageRepository.deleteMessages(student, selectedMailbox, messageList)
            }
                .onFailure(errorHandler::dispatch)
                .onSuccess { view?.showMessagesDeleted() }
        }
    }

    fun onActionModeSelectCheckAll() {
        val messagesToSelect = getFilteredData()
        val isAllSelected = messagesToDelete.containsAll(messagesToSelect)

        if (isAllSelected) {
            messagesToDelete.clear()
            view?.showActionMode(false)
        } else {
            messagesToDelete.addAll(messagesToSelect)
            updateDataInView()
        }

        view?.run {
            updateSelectAllMenu(!isAllSelected)
            updateActionModeTitle(messagesToDelete.size)
        }
    }

    fun onMessageItemLongSelected(messageItem: MessageTabDataItem.MessageItem) {
        if (!isActionMode) {
            view?.showActionMode(true)

            messagesToDelete.add(messageItem.message)

            view?.updateActionModeTitle(messagesToDelete.size)
            updateDataInView()
        }
    }

    fun onMessageItemSelected(messageItem: MessageTabDataItem.MessageItem, position: Int) {
        Timber.i("Select message ${messageItem.message.messageGlobalKey} item (position: $position)")

        if (!isActionMode) {
            view?.run {
                showActionMode(false)
                openMessage(messageItem.message)
            }
        } else {
            if (!messageItem.isSelected) {
                messagesToDelete.add(messageItem.message)
            } else {
                messagesToDelete.remove(messageItem.message)
            }

            if (messagesToDelete.isEmpty()) {
                view?.showActionMode(false)
            }

            val filteredData = getFilteredData()

            view?.run {
                updateActionModeTitle(messagesToDelete.size)
                updateSelectAllMenu(messagesToDelete.containsAll(filteredData))
            }
            updateDataInView()
        }
    }

    fun onUnreadFilterSelected(isChecked: Boolean) {
        view?.run {
            onlyUnread = isChecked
            loadData(false)
        }
    }

    fun onAttachmentsFilterSelected(isChecked: Boolean) {
        view?.run {
            onlyWithAttachments = isChecked
            loadData(false)
        }
    }

    fun onMailboxFilterSelected() {
        view?.showMailboxChooser(mailboxes)
    }

    fun onMailboxSelected(mailbox: Mailbox?) {
        selectedMailbox = mailbox
        loadData(false)
    }

    private fun loadData(forceRefresh: Boolean) {
        Timber.i("Loading $folder message data started")

        flatResourceFlow {
            val student = studentRepository.getCurrentStudent()

            if (selectedMailbox == null && mailboxes.isEmpty()) {
                selectedMailbox = messageRepository.getMailboxByStudent(student)
                mailboxes = messageRepository.getMailboxes(student, forceRefresh).toFirstResult()
                    .dataOrNull.orEmpty()
            }

            messageRepository.getMessages(student, selectedMailbox, folder, forceRefresh)
        }
            .logResourceStatus("load $folder message")
            .onResourceData {
                messages = it

                val filteredData = getFilteredData()

                view?.run {
                    enableSwipe(true)
                    showErrorView(false)
                    showProgress(false)
                    showContent(true)
                    showEmpty(filteredData.isEmpty())
                }

                updateDataInView()
            }
            .onResourceIntermediate { view?.showRefresh(true) }
            .onResourceSuccess {
                analytics.logEvent(
                    "load_data",
                    "type" to "messages",
                    "items" to it.size,
                    "folder" to folder.name
                )
            }
            .onResourceNotLoading {
                view?.run {
                    showRefresh(false)
                    showProgress(false)
                    enableSwipe(true)
                    notifyParentDataLoaded()
                }
            }
            .onResourceError(errorHandler::dispatch)
            .catch {
                errorHandler.dispatch(it)
                view?.notifyParentDataLoaded()
            }
            .launch()
    }

    private fun showErrorViewOnError(message: String, error: Throwable) {
        view?.run {
            if (isViewEmpty) {
                lastError = error
                setErrorDetails(message)
                showErrorView(true)
                showEmpty(false)
                showProgress(false)
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

                    getFilteredData()
                }
                .catch { Timber.e(it) }
                .collect {
                    Timber.d("Applying filter. Full list: ${messages.size}, filtered: ${it.size}")

                    view?.run {
                        showEmpty(it.isEmpty())
                        showContent(true)
                        showErrorView(false)
                    }

                    updateDataInView()
                    view?.resetListPosition()
                }
        }
    }

    private fun getFilteredData(): List<Message> {
        if (lastSearchQuery.trim().isEmpty()) {
            val sortedMessages = messages.sortedByDescending { it.date }
            return when {
                (onlyUnread == true) && onlyWithAttachments -> sortedMessages.filter { it.unread == onlyUnread && it.hasAttachments == onlyWithAttachments }
                (onlyUnread == true) -> sortedMessages.filter { it.unread == onlyUnread }
                onlyWithAttachments -> sortedMessages.filter { it.hasAttachments == onlyWithAttachments }
                else -> sortedMessages
            }
        } else {
            val sortedMessages = messages
                .map { it to calculateMatchRatio(it, lastSearchQuery) }
                .sortedWith(compareBy<Pair<Message, Int>> { -it.second }.thenByDescending { it.first.date })
                .filter { it.second > 6000 }
                .map { it.first }
            return when {
                (onlyUnread == true) && onlyWithAttachments -> sortedMessages.filter { it.unread == onlyUnread && it.hasAttachments == onlyWithAttachments }
                (onlyUnread == true) -> sortedMessages.filter { it.unread == onlyUnread }
                onlyWithAttachments -> sortedMessages.filter { it.hasAttachments == onlyWithAttachments }
                else -> sortedMessages
            }
        }
    }

    private fun updateDataInView() {
        val data = getFilteredData()

        val list = buildList {
            add(
                MessageTabDataItem.FilterHeader(
                    onlyUnread = onlyUnread.takeIf { folder != MessageFolder.SENT },
                    onlyWithAttachments = onlyWithAttachments,
                    isEnabled = !isActionMode,
                    selectedMailbox = selectedMailbox?.let {
                        buildString {
                            if (it.studentName.isNotBlank() && it.studentName != it.userName) {
                                append(it.studentName)
                                append(" - ")
                            }
                            append(it.userName)
                        }
                    },
                )
            )

            addAll(data.map { message ->
                MessageTabDataItem.MessageItem(
                    message = message,
                    isSelected = messagesToDelete.any { it.messageGlobalKey == message.messageGlobalKey },
                    isActionMode = isActionMode
                )
            })
        }

        view?.updateData(list)
    }

    private fun calculateMatchRatio(message: Message, query: String): Int {
        val subjectRatio = FuzzySearch.tokenSortPartialRatio(query.lowercase(), message.subject)

        val correspondentsRatio = FuzzySearch.tokenSortPartialRatio(
            query.lowercase(),
            message.correspondents
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
            + correspondentsRatio.toDouble().pow(2)
            + dateRatio.toDouble().pow(2) * 2
            ).toInt()
    }
}
