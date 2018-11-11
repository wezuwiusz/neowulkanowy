package io.github.wulkanowy.data.repositories.remote

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.data.db.entities.Note
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.utils.toLocalDate
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRemote @Inject constructor(private val api: Api) {

    fun getNotes(semester: Semester): Single<List<Note>> {
        return Single.just(api.run {
            if (diaryId != semester.diaryId) {
                diaryId = semester.diaryId
                notifyDataChanged()
            }
        }).flatMap { api.getNotes() }
            .map { notes ->
                notes.map {
                    Note(
                        semesterId = semester.semesterId,
                        studentId = semester.studentId,
                        date = it.date.toLocalDate(),
                        teacher = it.teacher,
                        category = it.category,
                        content = it.content
                    )
                }
            }
    }
}
