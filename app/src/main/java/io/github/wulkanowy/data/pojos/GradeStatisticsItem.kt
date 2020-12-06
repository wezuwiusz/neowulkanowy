package io.github.wulkanowy.data.pojos

import io.github.wulkanowy.data.db.entities.GradePartialStatistics
import io.github.wulkanowy.data.db.entities.GradePointsStatistics
import io.github.wulkanowy.data.db.entities.GradeSemesterStatistics
import io.github.wulkanowy.ui.modules.grade.statistics.ViewType

data class GradeStatisticsItem(

    val type: ViewType,

    val average: String,

    val partial: GradePartialStatistics?,

    val semester: GradeSemesterStatistics?,

    val points: GradePointsStatistics?
)
