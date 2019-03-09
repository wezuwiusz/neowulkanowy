package io.github.wulkanowy.data.repositories.note

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.Note
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.reactivex.Completable
import io.reactivex.Single
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository @Inject constructor(
    private val settings: InternetObservingSettings,
    private val local: NoteLocal,
    private val remote: NoteRemote
) {

    fun getNotes(student: Student, semester: Semester, forceRefresh: Boolean = false, notify: Boolean = false): Single<List<Note>> {
        return local.getNotes(student).filter { !forceRefresh }
            .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                .flatMap {
                    if (it) remote.getNotes(semester)
                    else Single.error(UnknownHostException())
                }.flatMap { new ->
                    local.getNotes(student).toSingle(emptyList())
                        .doOnSuccess { old ->
                            local.deleteNotes(old - new)
                            local.saveNotes((new - old)
                                .onEach {
                                    if (it.date >= student.registrationDate.toLocalDate()) it.apply {
                                        isRead = false
                                        if (notify) isNotified = false
                                    }
                                })
                        }
                }.flatMap { local.getNotes(student).toSingle(emptyList()) })
    }

    fun getNotNotifiedNotes(student: Student): Single<List<Note>> {
        return local.getNotes(student).map { it.filter { note -> !note.isNotified } }.toSingle(emptyList())
    }

    fun updateNote(note: Note): Completable {
        return Completable.fromCallable { local.updateNotes(listOf(note)) }
    }

    fun updateNotes(notes: List<Note>): Completable {
        return Completable.fromCallable { local.updateNotes(notes) }
    }
}
