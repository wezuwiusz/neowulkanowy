package io.github.wulkanowy.api.timetable

data class TimetableLesson(

        val number: Int,

        val date: String,

        val freeDayName: String,

        val startTime: String,

        val endTime: String,

        var subject: String = "",

        var teacher: String = "",

        var room: String = "",

        var description: String = "",

        var groupName: String = "",

        var empty: Boolean = false,

        var divisionIntoGroups: Boolean = false,

        var planning: Boolean = false,

        var realized: Boolean = false,

        var movedOrCanceled: Boolean = false,

        var newMovedInOrChanged: Boolean = false
)
