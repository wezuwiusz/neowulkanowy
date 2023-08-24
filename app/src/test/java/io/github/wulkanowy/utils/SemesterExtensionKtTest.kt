package io.github.wulkanowy.utils

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.getSemesterEntity
import org.junit.Test
import java.time.LocalDate
import kotlin.test.assertEquals

class SemesterExtensionKtTest {

    @Test
    fun `check is first semester is current`() {
        val first = getSemesterEntity(
            semesterName = 1,
            start = LocalDate.of(2023, 9, 1),
            end = LocalDate.of(2024, 1, 31),
        )

        // first boundary - school-year start
        assertEquals(false, first.isCurrent(LocalDate.of(2023, 8, 28)))
        assertEquals(true, first.isCurrent(LocalDate.of(2023, 8, 29)))

        // second boundary
        assertEquals(true, first.isCurrent(LocalDate.of(2024, 1, 31)))
        assertEquals(false, first.isCurrent(LocalDate.of(2024, 2, 1)))
    }

    @Test
    fun `check is second semester is current`() {
        val second = getSemesterEntity(
            semesterName = 2,
            start = LocalDate.of(2024, 2, 1),
            end = LocalDate.of(2024, 9, 1),
        )

        // first boundary
        assertEquals(false, second.isCurrent(LocalDate.of(2024, 1, 31)))
        assertEquals(true, second.isCurrent(LocalDate.of(2024, 2, 1)))

        // second boundary - school-year end
        assertEquals(true, second.isCurrent(LocalDate.of(2024, 8, 29)))
        assertEquals(false, second.isCurrent(LocalDate.of(2024, 8, 30)))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `get current semester when current is doubled`() {
        val semesters = listOf(
            getSemesterEntity(1, 1, LocalDate.now(), LocalDate.now()),
            getSemesterEntity(1, 1, LocalDate.now(), LocalDate.now())
        )

        semesters.getCurrentOrLast()
    }

    @Test(expected = RuntimeException::class)
    fun `get current semester when there is empty list`() {
        val semesters = listOf<Semester>()

        semesters.getCurrentOrLast()
    }

    @Test
    fun `get current kindergarten semester when there is no any current`() {
        val semesters = listOf(
            createSemesterEntity(
                kindergartenDiaryId = 281,
                schoolYear = 2020,
                semesterId = 0,
                start = LocalDate.of(2020, 9, 1),
                end = LocalDate.of(2021, 8, 31),
            ),
            createSemesterEntity(
                kindergartenDiaryId = 342,
                schoolYear = 2021,
                semesterId = 0,
                start = LocalDate.of(2021, 9, 1),
                end = LocalDate.of(2022, 8, 31),
            ),
        )

        val res = semesters.getCurrentOrLast()

        assertEquals(2021, res.schoolYear)
    }

    private fun createSemesterEntity(
        diaryId: Int = 0,
        kindergartenDiaryId: Int = 0,
        semesterId: Int = 0,
        schoolYear: Int = 0,
        start: LocalDate = LocalDate.now(),
        end: LocalDate = LocalDate.now().plusMonths(6),
    ) = Semester(
        studentId = 1,
        diaryId = diaryId,
        kindergartenDiaryId = kindergartenDiaryId,
        semesterId = semesterId,
        diaryName = "$semesterId",
        schoolYear = schoolYear,
        classId = 0,
        semesterName = semesterId,
        unitId = 1,
        start = start,
        end = end
    )
}
