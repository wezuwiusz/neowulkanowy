package io.github.wulkanowy.ui.modules.note

import io.github.wulkanowy.data.db.entities.Note
import io.github.wulkanowy.data.repositories.note.NoteRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import timber.log.Timber
import javax.inject.Inject

class NotePresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val noteRepository: NoteRepository,
    private val semesterRepository: SemesterRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<NoteView>(errorHandler, studentRepository, schedulers) {

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
        disposable.add(studentRepository.getCurrentStudent()
            .flatMap { semesterRepository.getCurrentSemester(it).map { semester -> semester to it } }
            .flatMap { noteRepository.getNotes(it.second, it.first, forceRefresh) }
            .map { items -> items.sortedByDescending { it.date } }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .doFinally {
                view?.run {
                    hideRefresh()
                    showProgress(false)
                    enableSwipe(true)
                }
            }.subscribe({
                Timber.i("Loading note result: Success")
                view?.apply {
                    updateData(it)
                    showEmpty(it.isEmpty())
                    showErrorView(false)
                    showContent(it.isNotEmpty())
                }
                analytics.logEvent(
                    "load_data",
                    "type" to "note",
                    "items" to it.size,
                    "force_refresh" to forceRefresh
                )
            }, {
                Timber.i("Loading note result: An exception occurred")
                errorHandler.dispatch(it)
            })
        )
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
        Timber.i("Attempt to update note ${note.id}")
        disposable.add(noteRepository.updateNote(note)
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .subscribe({ Timber.i("Update note result: Success") })
            { error ->
                Timber.i("Update note result: An exception occurred")
                errorHandler.dispatch(error)
            })
    }
}
