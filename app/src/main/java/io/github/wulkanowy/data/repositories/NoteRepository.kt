package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.WulkanowySdkFactory
import io.github.wulkanowy.data.db.dao.NoteDao
import io.github.wulkanowy.data.db.entities.Note
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.data.networkBoundResource
import io.github.wulkanowy.utils.AutoRefreshHelper
import io.github.wulkanowy.utils.getRefreshKey
import io.github.wulkanowy.utils.toLocalDate
import io.github.wulkanowy.utils.uniqueSubtract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository @Inject constructor(
    private val noteDb: NoteDao,
    private val wulkanowySdkFactory: WulkanowySdkFactory,
    private val refreshHelper: AutoRefreshHelper,
) {

    private val saveFetchResultMutex = Mutex()

    private val cacheKey = "note"

    fun getNotes(
        student: Student,
        semester: Semester,
        forceRefresh: Boolean,
        notify: Boolean = false,
    ) = networkBoundResource(
        mutex = saveFetchResultMutex,
        isResultEmpty = { it.isEmpty() },
        shouldFetch = {
            val isExpired = refreshHelper.shouldBeRefreshed(
                getRefreshKey(cacheKey, semester)
            )
            it.isEmpty() || forceRefresh || isExpired
        },
        query = { noteDb.loadAll(student.studentId) },
        fetch = {
            wulkanowySdkFactory.create(student, semester)
                .getNotes()
                .mapToEntities(semester)
        },
        saveFetchResult = { old, new ->
            val notesToAdd = (new uniqueSubtract old).onEach {
                if (it.date >= student.registrationDate.toLocalDate()) it.apply {
                    isRead = false
                    if (notify) isNotified = false
                }
            }
            noteDb.removeOldAndSaveNew(
                oldItems = old uniqueSubtract new,
                newItems = notesToAdd,
            )
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
