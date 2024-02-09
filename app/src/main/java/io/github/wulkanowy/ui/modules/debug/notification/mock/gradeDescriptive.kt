package io.github.wulkanowy.ui.modules.debug.notification.mock

import io.github.wulkanowy.data.db.entities.GradeDescriptive

val debugGradeDescriptiveItems = listOf(
    generateGradeDescriptive(
        "Matematyka",
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
    ),
    generateGradeDescriptive("Fizyka", "Lorem ipsum dolor sit amet, consectetur adipiscing elit."),
    generateGradeDescriptive(
        "Geografia",
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
    ),
    generateGradeDescriptive(
        "Sieci komputerowe",
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
    ),
    generateGradeDescriptive(
        "Systemy operacyjne",
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
    ),
    generateGradeDescriptive(
        "Język polski",
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
    ),
    generateGradeDescriptive(
        "Język angielski",
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
    ),
    generateGradeDescriptive("Religia", "Lorem ipsum dolor sit amet, consectetur adipiscing elit."),
    generateGradeDescriptive(
        "Język niemiecki",
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
    ),
    generateGradeDescriptive(
        "Wychowanie fizyczne",
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
    ),
)

private fun generateGradeDescriptive(subject: String, description: String) =
    GradeDescriptive(
        semesterId = 0,
        studentId = 0,
        subject = subject,
        description = description
    )
