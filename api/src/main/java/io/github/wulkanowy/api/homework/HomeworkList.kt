package io.github.wulkanowy.api.homework

import io.github.wulkanowy.api.SnP
import io.github.wulkanowy.api.getDateAsTick
import io.github.wulkanowy.api.getFormattedDate
import org.jsoup.nodes.Element

class HomeworkList(private val snp: SnP) {

    fun getHomework(date: String = ""): List<Homework> {
        return snp.getSnPPageDocument("ZadaniaDomowe.mvc?data=${getDateAsTick(date)}&rodzajWidoku=Dzien")
                .select(".mainContainer article").map {
                    getItem(it, date)
                }
    }

    private fun getItem(e: Element, date: String): Homework {
        val teacherAndDate = snp.getRowDataChildValue(e, 3).split(", ")
        return Homework(
                date = date,
                subject = snp.getRowDataChildValue(e, 1),
                teacher = teacherAndDate.first().split(" [").first(),
                teacherSymbol = teacherAndDate.first().split(" [").last().replace("]", ""),
                content = snp.getRowDataChildValue(e, 2),
                entryDate = getFormattedDate(teacherAndDate.last())
        )
    }
}
