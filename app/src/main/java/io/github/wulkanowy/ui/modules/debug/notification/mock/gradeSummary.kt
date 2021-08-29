package io.github.wulkanowy.ui.modules.debug.notification.mock

import io.github.wulkanowy.data.db.entities.GradeSummary

val debugGradeSummaryItems = listOf(
    generateSummary("Matematyka", "2-", "2"),
    generateSummary("Fizyka", "1", "2"),
    generateSummary("Geografia", "4+", "5"),
    generateSummary("Sieci komputerowe", "2", "5"),
    generateSummary("Systemy operacyjne", "3", "4"),
    generateSummary("Język polski", "1", "3"),
    generateSummary("Język angielski", "4", "3"),
    generateSummary("Religia", "5", "6"),
    generateSummary("Język niemiecki", "2", "2"),
    generateSummary("Wychowanie fizyczne", "5", "5"),
    generateSummary("Biologia", "4", "4"),
)

private fun generateSummary(subject: String, predicted: String, final: String) = GradeSummary(
    semesterId = 0,
    studentId = 0,
    position = 0,
    subject = subject,
    predictedGrade = predicted,
    finalGrade = final,
    proposedPoints = "",
    finalPoints = "",
    pointsSum = "",
    average = .0
)
