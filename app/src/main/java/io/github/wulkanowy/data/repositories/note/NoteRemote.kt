package io.github.wulkanowy.data.repositories.note

import io.github.wulkanowy.data.db.entities.Note
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.init
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRemote @Inject constructor(private val sdk: Sdk) {

    suspend fun getNotes(student: Student, semester: Semester): List<Note> {
        return sdk.init(student).switchDiary(semester.diaryId, semester.schoolYear)
            .getNotes(semester.semesterId)
            .map {
                Note(
                    studentId = semester.studentId,
                    date = it.date,
                    teacher = it.teacher,
                    teacherSymbol = it.teacherSymbol,
                    category = it.category,
                    categoryType = it.categoryType.id,
                    isPointsShow = it.showPoints,
                    points = it.points,
                    content = it.content
                )
            }
    }
}
