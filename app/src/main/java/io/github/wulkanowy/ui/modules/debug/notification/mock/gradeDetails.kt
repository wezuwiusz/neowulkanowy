package io.github.wulkanowy.ui.modules.debug.notification.mock

import io.github.wulkanowy.data.db.entities.Grade
import java.time.LocalDate

val debugGradeDetailsItems = listOf(
    generateGrade("Matematyka", "+"),
    generateGrade("Matematyka", "2="),
    generateGrade("Fizyka", "-"),
    generateGrade("Geografia", "4+"),
    generateGrade("Sieci komputerowe", "1"),
    generateGrade("Systemy operacyjne", "3+"),
    generateGrade("Język polski", "2-"),
    generateGrade("Język angielski", "4+"),
    generateGrade("Religia", "6"),
    generateGrade("Język niemiecki", "1!"),
    generateGrade("Wychowanie fizyczne", "5"),
)

private fun generateGrade(subject: String, entry: String) = Grade(
    subject = subject,
    entry = entry,
    semesterId = 0,
    studentId = 0,
    value = 0.0,
    modifier = 0.0,
    comment = "",
    color = "",
    gradeSymbol = "",
    description = "",
    weight = "",
    weightValue = 0.0,
    date = LocalDate.now(),
    teacher = ""
)
