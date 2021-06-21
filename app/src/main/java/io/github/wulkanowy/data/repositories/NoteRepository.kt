package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.db.dao.NoteDao
import io.github.wulkanowy.data.db.entities.Note
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.AutoRefreshHelper
import io.github.wulkanowy.utils.getRefreshKey
import io.github.wulkanowy.utils.init
import io.github.wulkanowy.utils.networkBoundResource
import io.github.wulkanowy.utils.uniqueSubtract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository @Inject constructor(
    private val noteDb: NoteDao,
    private val sdk: Sdk,
    private val refreshHelper: AutoRefreshHelper,
) {

    private val saveFetchResultMutex = Mutex()

    private val cacheKey = "note"

    fun getNotes(student: Student, semester: Semester, forceRefresh: Boolean, notify: Boolean = false) = networkBoundResource(
        mutex = saveFetchResultMutex,
        shouldFetch = { it.isEmpty() || forceRefresh || refreshHelper.isShouldBeRefreshed(getRefreshKey(cacheKey, semester)) },
        query = { noteDb.loadAll(student.studentId) },
        fetch = {
            sdk.init(student).switchDiary(semester.diaryId, semester.schoolYear)
                .getNotes(semester.semesterId)
                .mapToEntities(semester)
        },
        saveFetchResult = { old, new ->
            noteDb.deleteAll(old uniqueSubtract new)
            noteDb.insertAll((new uniqueSubtract old).onEach {
                if (it.date >= student.registrationDate.toLocalDate()) it.apply {
                    isRead = false
                    if (notify) isNotified = false
                }
            })

            refreshHelper.updateLastRefreshTimestamp(getRefreshKey(cacheKey, semester))
        }
    )

    fun getNotesFromDatabase(student: Student): Flow<List<Note>> {
        return noteDb.loadAll(student.studentId)
    }

    suspend fun updateNote(note: Note) {
        noteDb.updateAll(listOf(note))
    }

    suspend fun updateNotes(notes: List<Note>) {
        noteDb.updateAll(notes)
    }
}
