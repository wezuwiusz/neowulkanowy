package io.github.wulkanowy.data.repositories.note

import io.github.wulkanowy.data.db.entities.Note
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.utils.networkBoundResource
import io.github.wulkanowy.utils.uniqueSubtract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository @Inject constructor(
    private val local: NoteLocal,
    private val remote: NoteRemote
) {

    fun getNotes(student: Student, semester: Semester, forceRefresh: Boolean, notify: Boolean = false) = networkBoundResource(
        shouldFetch = { it.isEmpty() || forceRefresh },
        query = { local.getNotes(student) },
        fetch = { remote.getNotes(student, semester) },
        saveFetchResult = { old, new ->
            local.deleteNotes(old uniqueSubtract new)
            local.saveNotes((new uniqueSubtract old).onEach {
                if (it.date >= student.registrationDate.toLocalDate()) it.apply {
                    isRead = false
                    if (notify) isNotified = false
                }
            })
        }
    )

    fun getNotNotifiedNotes(student: Student): Flow<List<Note>> {
        return local.getNotes(student).map { it.filter { note -> !note.isNotified } }
    }

    suspend fun updateNote(note: Note) {
        local.updateNotes(listOf(note))
    }

    suspend fun updateNotes(notes: List<Note>) {
        return local.updateNotes(notes)
    }
}
