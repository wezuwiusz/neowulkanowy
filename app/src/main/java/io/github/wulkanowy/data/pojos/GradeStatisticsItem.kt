package io.github.wulkanowy.data.pojos

import io.github.wulkanowy.data.db.entities.GradePointsStatistics
import io.github.wulkanowy.data.db.entities.GradeStatistics
import io.github.wulkanowy.ui.modules.grade.statistics.ViewType

data class GradeStatisticsItem(

    val type: ViewType,

    val partial: List<GradeStatistics>,

    val points: GradePointsStatistics?
)
