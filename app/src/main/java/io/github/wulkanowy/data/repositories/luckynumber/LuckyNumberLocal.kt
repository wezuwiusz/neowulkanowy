package io.github.wulkanowy.data.repositories.luckynumber

import io.github.wulkanowy.data.db.dao.LuckyNumberDao
import io.github.wulkanowy.data.db.entities.LuckyNumber
import io.github.wulkanowy.data.db.entities.Semester
import io.reactivex.Maybe
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LuckyNumberLocal @Inject constructor(private val luckyNumberDb: LuckyNumberDao) {

    fun saveLuckyNumber(luckyNumber: LuckyNumber) {
        luckyNumberDb.insert(luckyNumber)
    }

    fun updateLuckyNumber(luckyNumber: LuckyNumber) {
        luckyNumberDb.update(luckyNumber)
    }

    fun deleteLuckyNumber(luckyNumber: LuckyNumber) {
        luckyNumberDb.delete(luckyNumber)
    }

    fun getLuckyNumber(semester: Semester, date: LocalDate): Maybe<LuckyNumber> {
        return luckyNumberDb.load(semester.studentId, date)
    }
}
