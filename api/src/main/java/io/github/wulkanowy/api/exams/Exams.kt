package io.github.wulkanowy.api.exams

import io.github.wulkanowy.api.SnP
import io.github.wulkanowy.api.getDateAsTick
import io.github.wulkanowy.api.getFormattedDate
import org.jsoup.nodes.Element

class Exams(private val snp: SnP) {

    fun getExams() = getExams("")

    fun getExams(start: String): List<ExamEntry> {
        return snp.getSnPPageDocument("Sprawdziany.mvc/Terminarz?rodzajWidoku=2&data=" + getDateAsTick(start))
                .select(".mainContainer > div:not(.navigation)").map {
                    val date = getFormattedDate(it.selectFirst("h2")?.text()?.split(", ")?.last()?.trim())

                    it.select("article").map { getExam(it, date) }
                }.flatten()
    }

    private fun getExam(e: Element, date: String): ExamEntry {
        val subjectAndGroup = snp.getRowDataChildValue(e, 1)
        val groupAndClass = subjectAndGroup.split(" ").last()
        val group = if (groupAndClass.contains("|")) groupAndClass.split("|").last() else ""
        val teacherAndDate = snp.getRowDataChildValue(e, 4).split(", ")
        val teacherSymbol = teacherAndDate.first().split(" ").last().removeSurrounding("[", "]")
        val teacher = teacherAndDate.first().replace(" [$teacherSymbol]", "")

        return ExamEntry(
                date = date,
                entryDate = getFormattedDate(teacherAndDate.last()),
                subject = subjectAndGroup.replace(groupAndClass, "").trim(),
                group = group,
                type = snp.getRowDataChildValue(e, 2),
                description = snp.getRowDataChildValue(e, 3),
                teacher = teacher,
                teacherSymbol = teacherSymbol
        )
    }
}
