package io.github.wulkanowy.data.repositories.local

import io.github.wulkanowy.data.db.dao.LuckyNumberDao
import io.github.wulkanowy.data.db.entities.LuckyNumber
import io.github.wulkanowy.data.db.entities.Semester
import io.reactivex.Completable
import io.reactivex.Maybe
import org.threeten.bp.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LuckyNumberLocal @Inject constructor(private val luckyNumberDb: LuckyNumberDao) {

    fun getLuckyNumber(semester: Semester, date: LocalDate): Maybe<LuckyNumber> {
        return luckyNumberDb.loadFromDate(semester.studentId, date)
    }

    fun saveLuckyNumber(luckyNumber: LuckyNumber) {
        luckyNumberDb.insert(luckyNumber)
    }

    fun updateLuckyNumber(luckyNumber: LuckyNumber): Completable {
        return Completable.fromCallable { luckyNumberDb.update(luckyNumber) }
    }

    fun deleteLuckyNumber(luckyNumber: LuckyNumber) {
        luckyNumberDb.delete(luckyNumber)
    }
}
