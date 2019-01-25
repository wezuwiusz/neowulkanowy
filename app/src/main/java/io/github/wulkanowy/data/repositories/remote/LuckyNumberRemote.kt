package io.github.wulkanowy.data.repositories.remote

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.data.db.entities.LuckyNumber
import io.github.wulkanowy.data.db.entities.Semester
import io.reactivex.Maybe
import io.reactivex.Single
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LuckyNumberRemote @Inject constructor(private val api: Api) {

    fun getLuckyNumber(semester: Semester): Maybe<LuckyNumber> {
        return Single.just(api.apply { diaryId = semester.diaryId })
            .flatMapMaybe { it.getLuckyNumber() }
            .map {
                LuckyNumber(
                    studentId = semester.studentId,
                    date = LocalDate.now(),
                    luckyNumber = it
                )
            }
    }
}
