package io.github.wulkanowy.ui.modules.note

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.db.entities.Note
import io.github.wulkanowy.data.repositories.note.NoteRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.session.BaseSessionPresenter
import io.github.wulkanowy.ui.base.session.SessionErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import timber.log.Timber
import javax.inject.Inject

class NotePresenter @Inject constructor(
    private val errorHandler: SessionErrorHandler,
    private val schedulers: SchedulersProvider,
    private val studentRepository: StudentRepository,
    private val noteRepository: NoteRepository,
    private val semesterRepository: SemesterRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BaseSessionPresenter<NoteView>(errorHandler) {

    override fun onAttachView(view: NoteView) {
        super.onAttachView(view)
        Timber.i("Note view is attached")
        view.initView()
        loadData()
    }

    fun onSwipeRefresh() {
        Timber.i("Force refreshing the note")
        loadData(true)
    }

    private fun loadData(forceRefresh: Boolean = false) {
        Timber.i("Loading note data started")
        disposable.add(studentRepository.getCurrentStudent()
            .flatMap { semesterRepository.getCurrentSemester(it) }
            .flatMap { noteRepository.getNotes(it, forceRefresh) }
            .map { items -> items.map { NoteItem(it) } }
            .map { items -> items.sortedByDescending { it.note.date } }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .doFinally {
                view?.run {
                    hideRefresh()
                    showProgress(false)
                }
            }.subscribe({
                Timber.i("Loading note result: Success")
                view?.apply {
                    updateData(it)
                    showEmpty(it.isEmpty())
                    showContent(it.isNotEmpty())
                }
                analytics.logEvent("load_note", "items" to it.size, "force_refresh" to forceRefresh)
            }, {
                Timber.i("Loading note result: An exception occurred")
                view?.run { showEmpty(isViewEmpty) }
                errorHandler.dispatch(it)
            })
        )
    }

    fun onNoteItemSelected(item: AbstractFlexibleItem<*>?) {
        if (item is NoteItem) {
            Timber.i("Select note item ${item.note.id}")
            view?.run {
                showNoteDialog(item.note)
                if (!item.note.isRead) {
                    item.note.isRead = true
                    updateItem(item)
                    updateNote(item.note)
                }
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
