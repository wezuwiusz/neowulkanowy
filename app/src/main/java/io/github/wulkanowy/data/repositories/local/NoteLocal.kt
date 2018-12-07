package io.github.wulkanowy.data.repositories.local

import io.github.wulkanowy.data.db.dao.NoteDao
import io.github.wulkanowy.data.db.entities.Note
import io.github.wulkanowy.data.db.entities.Semester
import io.reactivex.Completable
import io.reactivex.Maybe
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteLocal @Inject constructor(private val noteDb: NoteDao) {

    fun getNotes(semester: Semester): Maybe<List<Note>> {
        return noteDb.loadAll(semester.semesterId, semester.studentId).filter { !it.isEmpty() }
    }

    fun getNewNotes(semester: Semester): Maybe<List<Note>> {
        return noteDb.loadNew(semester.semesterId, semester.studentId)
    }

    fun saveNotes(notes: List<Note>) {
        noteDb.insertAll(notes)
    }

    fun updateNote(note: Note): Completable {
        return Completable.fromCallable { noteDb.update(note) }
    }

    fun updateNotes(notes: List<Note>): Completable {
        return Completable.fromCallable { noteDb.updateAll(notes) }
    }

    fun deleteNotes(notes: List<Note>) {
        noteDb.deleteAll(notes)
    }
}
