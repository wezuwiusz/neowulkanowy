package io.github.wulkanowy.ui.modules.debug.notification.mock

import io.github.wulkanowy.data.db.entities.Timetable
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import kotlin.random.Random

val debugTimetableItems = listOf(
    generateTimetable("Matematyka", "12", "01"),
    generateTimetable("Język angielski", "23", "12"),
    generateTimetable("Geografia", "34", "23"),
    generateTimetable("Sieci komputerowe", "45", "34"),
    generateTimetable("Systemy operacyjne", "56", "45"),
    generateTimetable("Język niemiecki", "67", "56"),
    generateTimetable("Biologia", "78", "67"),
    generateTimetable("Chemia", "89", "78"),
    generateTimetable("Fizyka", "90", "89"),
    generateTimetable("Matematyka", "01", "90"),
)

private fun generateTimetable(subject: String, room: String, roomOld: String) = Timetable(
    subject = subject,
    studentId = 0,
    diaryId = 0,
    date = LocalDate.now().minusDays(Random.nextLong(0, 8)),
    number = 1,
    start = Instant.now().plus(Duration.ofHours(1)),
    end = Instant.now(),
    subjectOld = "",
    group = "",
    room = room,
    roomOld = roomOld,
    teacher = "Wtorkowska Renata",
    teacherOld = "",
    info = "",
    isStudentPlan = true,
    changes = true,
    canceled = true
)
