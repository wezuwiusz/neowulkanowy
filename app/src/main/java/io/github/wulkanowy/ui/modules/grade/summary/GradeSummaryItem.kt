package io.github.wulkanowy.ui.modules.grade.summary

import io.github.wulkanowy.data.db.entities.GradeDescriptive
import io.github.wulkanowy.data.db.entities.GradeSummary

data class GradeSummaryItem(
    val gradeSummary: GradeSummary,
    val gradeDescriptive: GradeDescriptive?
)
