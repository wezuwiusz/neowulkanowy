package io.github.wulkanowy.api.exams

data class ExamEntry(

        val date: String,

        val entryDate: String,

        val subject: String,

        val group: String,

        val type: String,

        val description: String,

        val teacher: String,

        val teacherSymbol: String
)
