package io.github.wulkanowy.data.repositories.note

import io.github.wulkanowy.data.db.entities.Note
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.sdk.Sdk
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRemote @Inject constructor(private val sdk: Sdk) {

    fun getNotes(semester: Semester): Single<List<Note>> {
        return sdk.switchDiary(semester.diaryId, semester.schoolYear).getNotes(semester.semesterId)
            .map { notes ->
                notes.map {
                    Note(
                        studentId = semester.studentId,
                        date = it.date,
                        teacher = it.teacher,
                        category = it.category,
                        content = it.content
                    )
                }
            }
    }
}
