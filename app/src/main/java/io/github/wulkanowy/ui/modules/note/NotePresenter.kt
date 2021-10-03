package io.github.wulkanowy.ui.modules.note

import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.db.entities.Note
import io.github.wulkanowy.data.repositories.NoteRepository
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.afterLoading
import io.github.wulkanowy.utils.flowWithResource
import io.github.wulkanowy.utils.flowWithResourceIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class NotePresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val noteRepository: NoteRepository,
    private val semesterRepository: SemesterRepository,
    private val analytics: AnalyticsHelper
) : BasePresenter<NoteView>(errorHandler, studentRepository) {

    private lateinit var lastError: Throwable

    override fun onAttachView(view: NoteView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Note view was initialized")
        errorHandler.showErrorMessage = ::showErrorViewOnError
        loadData()
    }

    fun onSwipeRefresh() {
        Timber.i("Force refreshing the note")
        loadData(true)
    }

    fun onRetry() {
        view?.run {
            showErrorView(false)
            showProgress(true)
        }
        loadData(true)
    }

    fun onDetailsClick() {
        view?.showErrorDetailsDialog(lastError)
    }

    private fun loadData(forceRefresh: Boolean = false) {
        Timber.i("Loading note data started")

        flowWithResourceIn {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)
            noteRepository.getNotes(student, semester, forceRefresh)
        }.onEach {
            when (it.status) {
                Status.LOADING -> {
                    if (!it.data.isNullOrEmpty()) {
                        view?.run {
                            enableSwipe(true)
                            showRefresh(true)
                            showProgress(false)
                            showContent(true)
                            updateData(it.data.sortedByDescending { item -> item.date })
                        }
                    }
                }
                Status.SUCCESS -> {
                    Timber.i("Loading note result: Success")
                    view?.apply {
                        updateData(it.data!!.sortedByDescending { item -> item.date })
                        showEmpty(it.data.isEmpty())
                        showErrorView(false)
                        showContent(it.data.isNotEmpty())
                    }
                    analytics.logEvent(
                        "load_data",
                        "type" to "note",
                        "items" to it.data!!.size
                    )
                }
                Status.ERROR -> {
                    Timber.i("Loading note result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                }
            }
        }.afterLoading {
            view?.run {
                showRefresh(false)
                showProgress(false)
                enableSwipe(true)
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

    fun onNoteItemSelected(note: Note, position: Int) {
        Timber.i("Select note item ${note.id}")
        view?.run {
            showNoteDialog(note)
            if (!note.isRead) {
                note.isRead = true
                updateItem(note, position)
                updateNote(note)
            }
        }
    }

    private fun updateNote(note: Note) {
        flowWithResource { noteRepository.updateNote(note) }
            .onEach {
                when (it.status) {
                    Status.LOADING -> Timber.i("Attempt to update note ${note.id}")
                    Status.SUCCESS -> Timber.i("Update note result: Success")
                    Status.ERROR -> {
                        Timber.i("Update note result: An exception occurred")
                        errorHandler.dispatch(it.error!!)
                    }
                }
            }
            .launch("update_note")
    }
}
