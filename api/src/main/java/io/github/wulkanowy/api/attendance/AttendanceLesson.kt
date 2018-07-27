package io.github.wulkanowy.api.attendance

data class AttendanceLesson(

        val number: Int,

        val date: String,

        val subject: String,

        var notExist: Boolean = false,

        var presence: Boolean = false,

        var absenceUnexcused: Boolean = false,

        var absenceExcused: Boolean = false,

        var unexcusedLateness: Boolean = false,

        var absenceForSchoolReasons: Boolean = false,

        var excusedLateness: Boolean = false,

        var exemption: Boolean = false
)
