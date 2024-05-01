package io.github.wulkanowy.domain.attendance

import io.github.wulkanowy.data.*
import io.github.wulkanowy.data.db.entities.AttendanceSummary
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.Subject
import io.github.wulkanowy.data.enums.AttendanceCalculatorSortingMode
import io.github.wulkanowy.data.enums.AttendanceCalculatorSortingMode.*
import io.github.wulkanowy.data.pojos.AttendanceData
import io.github.wulkanowy.data.repositories.AttendanceSummaryRepository
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.SubjectRepository
import io.github.wulkanowy.utils.allAbsences
import io.github.wulkanowy.utils.allPresences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.floor

class GetAttendanceCalculatorDataUseCase @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val attendanceSummaryRepository: AttendanceSummaryRepository,
    private val preferencesRepository: PreferencesRepository,
) {

    operator fun invoke(
        student: Student,
        semester: Semester,
        forceRefresh: Boolean,
    ): Flow<Resource<List<AttendanceData>>> =
        subjectRepository.getSubjects(student, semester, forceRefresh)
            .mapResourceData { subjects -> subjects.sortedBy(Subject::name) }
            .combineWithResourceData(preferencesRepository.targetAttendanceFlow, ::Pair)
            .flatMapResourceData { (subjects, targetFreq) ->
                combineResourceFlows(subjects.map { subject ->
                    attendanceSummaryRepository.getAttendanceSummary(
                        student = student,
                        semester = semester,
                        subjectId = subject.realId,
                        forceRefresh = forceRefresh
                    ).mapResourceData { summaries ->
                        summaries.toAttendanceData(subject.name, targetFreq)
                    }
                })
                    // Every individual combined flow causes separate network requests to update data.
                    // When there is N child flows, they can cause up to N-1 items to be emitted. Since all
                    // requests are usually completed in less than 5s, there is no need to emit multiple
                    // intermediates that will be visible for barely any time.
                    .debounceIntermediates()
            }
            .combineWithResourceData(preferencesRepository.attendanceCalculatorShowEmptySubjects) { attendanceDataList, showEmptySubjects ->
                attendanceDataList.filter { it.total != 0 || showEmptySubjects }
            }
            .combineWithResourceData(preferencesRepository.attendanceCalculatorSortingModeFlow, List<AttendanceData>::sortedBy)
}

private fun List<AttendanceSummary>.toAttendanceData(subjectName: String, targetFreq: Int): AttendanceData {
    val presences = sumOf { it.allPresences }
    val absences = sumOf { it.allAbsences }
    return AttendanceData(
        subjectName = subjectName,
        lessonBalance = calcLessonBalance(
            targetFreq.toDouble() / 100, presences, absences
        ),
        presences = presences,
        absences = absences,
    )
}

private fun calcLessonBalance(targetFreq: Double, presences: Int, absences: Int): Int {
    val total = presences + absences
    // The `+ 1` is to avoid false positives in close cases. Eg.:
    // target frequency 99%, 1 presence. Without the `+ 1` this would be reported shown as
    // a positive balance of +1, however that is not actually true as skipping one class
    // would make it so that the balance would actually be negative (-98). The `+ 1`
    // fixes this and makes sure that in situations like these, it's not reporting incorrect
    // balances
    return when {
        presences / (total + 1f) >= targetFreq -> calcMissingAbsences(
            targetFreq, absences, presences
        )
        presences / (total + 0f) < targetFreq -> -calcMissingPresences(
            targetFreq, absences, presences
        )
        else -> 0
    }
}

private fun calcMissingPresences(targetFreq: Double, absences: Int, presences: Int) =
    calcMinRequiredPresencesFor(targetFreq, absences) - presences

private fun calcMinRequiredPresencesFor(targetFreq: Double, absences: Int) =
    ceil((targetFreq / (1 - targetFreq)) * absences).toInt()

private fun calcMissingAbsences(targetFreq: Double, absences: Int, presences: Int) =
    calcMinRequiredAbsencesFor(targetFreq, presences) - absences

private fun calcMinRequiredAbsencesFor(targetFreq: Double, presences: Int) =
    floor((presences * (1 - targetFreq)) / targetFreq).toInt()

private fun List<AttendanceData>.sortedBy(mode: AttendanceCalculatorSortingMode) = when (mode) {
    ALPHABETIC -> sortedBy(AttendanceData::subjectName)
    ATTENDANCE -> sortedByDescending(AttendanceData::presencePercentage)
    LESSON_BALANCE -> sortedBy(AttendanceData::lessonBalance)
}
