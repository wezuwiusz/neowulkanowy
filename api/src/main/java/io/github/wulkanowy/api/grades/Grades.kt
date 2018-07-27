package io.github.wulkanowy.api.grades

import io.github.wulkanowy.api.SnP
import io.github.wulkanowy.api.getFormattedDate
import org.jsoup.nodes.Element
import java.util.regex.Pattern

class Grades(private val snp: SnP) {

    private val colorPattern by lazy { Pattern.compile("#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})") }

    fun getGrades() = getGrades("")

    fun getGrades(semester: String): List<GradeKt> {
        return snp.getSnPPageDocument("Oceny/Wszystkie?details=2&okres=$semester")
                .select(".ocenySzczegoly-table > tbody > tr").map { getGrade(it) }.filter {
                    it.value != "Brak ocen"
                }
    }

    private fun getGrade(e: Element): GradeKt {
        val tds = e.select("td")
        val symbol = tds[2].text().split(", ").first()

        return GradeKt(
                subject = tds[0].text(),
                value = tds[1].text(),
                color = getColor(tds[1].select(".ocenaCzastkowa").attr("style")),
                symbol = symbol,
                description = tds[2].text().replaceFirst(symbol, "").replaceFirst(", ", ""),
                weight = tds[3].text(),
                date = getFormattedDate(tds[4].text()),
                teacher = tds[5].text()
        )
    }

    private fun getColor(styleAttr: String): String {
        val matcher = colorPattern.matcher(styleAttr)
        while (matcher.find()) {
            return matcher.group(1)
        }

        return ""
    }
}
