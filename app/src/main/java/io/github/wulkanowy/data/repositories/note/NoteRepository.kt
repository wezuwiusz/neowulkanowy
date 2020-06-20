package io.github.wulkanowy.data.repositories.note

import io.github.wulkanowy.data.db.entities.Note
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.utils.uniqueSubtract
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository @Inject constructor(
    private val local: NoteLocal,
    private val remote: NoteRemote
) {

    suspend fun getNotes(student: Student, semester: Semester, forceRefresh: Boolean = false, notify: Boolean = false): List<Note> {
        return local.getNotes(student).filter { !forceRefresh }.ifEmpty {
            val new = remote.getNotes(student, semester)
            val old = local.getNotes(student)

            local.deleteNotes(old.uniqueSubtract(new))
            local.saveNotes(new.uniqueSubtract(old).onEach {
                if (it.date >= student.registrationDate.toLocalDate()) it.apply {
                    isRead = false
                    if (notify) isNotified = false
                }
            })

            local.getNotes(student)
        }
    }

    suspend fun getNotNotifiedNotes(student: Student): List<Note> {
        return local.getNotes(student).filter { note -> !note.isNotified }
    }

    suspend fun updateNote(note: Note) {
        return local.updateNotes(listOf(note))
    }

    suspend fun updateNotes(notes: List<Note>) {
        return local.updateNotes(notes)
    }
}
