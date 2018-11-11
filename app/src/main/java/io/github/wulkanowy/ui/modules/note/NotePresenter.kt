package io.github.wulkanowy.ui.modules.note

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.data.db.entities.Note
import io.github.wulkanowy.data.repositories.NoteRepository
import io.github.wulkanowy.data.repositories.SessionRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.logEvent
import timber.log.Timber
import javax.inject.Inject

class NotePresenter @Inject constructor(
    private val errorHandler: ErrorHandler,
    private val schedulers: SchedulersProvider,
    private val sessionRepository: SessionRepository,
    private val noteRepository: NoteRepository
) : BasePresenter<NoteView>(errorHandler) {

    override fun onAttachView(view: NoteView) {
        super.onAttachView(view)
        view.initView()
        loadData()
    }

    fun onSwipeRefresh() {
        loadData(true)
    }

    private fun loadData(forceRefresh: Boolean = false) {
        disposable.add(sessionRepository.getSemesters()
            .map { it.single { semester -> semester.current } }
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
                view?.apply {
                    updateData(it)
                    showEmpty(it.isEmpty())
                    showContent(it.isNotEmpty())
                }
                logEvent("Note load", mapOf("items" to it.size, "forceRefresh" to forceRefresh))
            }, {
                view?.run { showEmpty(isViewEmpty) }
                errorHandler.proceed(it)
            })
        )
    }

    fun onNoteItemSelected(item: AbstractFlexibleItem<*>?) {
        if (item is NoteItem) {
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
        disposable.add(noteRepository.updateNote(note)
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .subscribe({
                Timber.d("Note ${note.id} updated")
            }) { error -> errorHandler.proceed(error) }
        )
    }
}
