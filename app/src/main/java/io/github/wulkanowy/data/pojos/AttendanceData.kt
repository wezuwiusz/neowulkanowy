package io.github.wulkanowy.data.pojos

data class AttendanceData(
    val subjectName: String,
    val lessonBalance: Int,
    val presences: Int,
    val absences: Int,
) {
    val total: Int
        get() = presences + absences

    val presencePercentage: Double
        get() = if (total == 0) 0.0 else (presences.toDouble() / total) * 100
}
