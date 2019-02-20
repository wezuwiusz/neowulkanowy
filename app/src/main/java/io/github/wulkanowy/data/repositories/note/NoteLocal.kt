package io.github.wulkanowy.data.repositories.note

import io.github.wulkanowy.data.db.dao.NoteDao
import io.github.wulkanowy.data.db.entities.Note
import io.github.wulkanowy.data.db.entities.Student
import io.reactivex.Completable
import io.reactivex.Maybe
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteLocal @Inject constructor(private val noteDb: NoteDao) {

    fun getNotes(student: Student): Maybe<List<Note>> {
        return noteDb.loadAll(student.studentId).filter { !it.isEmpty() }
    }

    fun getNewNotes(student: Student): Maybe<List<Note>> {
        return noteDb.loadNew(student.studentId)
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
