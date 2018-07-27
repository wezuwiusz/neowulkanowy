package io.github.wulkanowy.api.grades

import io.github.wulkanowy.api.SnP

class GradesSummary(private val snp: SnP) {

    fun getSummary() = getSummary("")

    fun getSummary(semester: String): List<Summary> {
        val subjects = snp.getSnPPageDocument("Oceny/Wszystkie?details=2&okres=$semester")
                .select(".ocenyZwykle-table > tbody > tr")

        return subjects.map {
            val tds = it.select("td")
            Summary(
                    name = tds[0].text(),
                    predicted = tds[tds.size - 2].text(),
                    final = tds[tds.size - 1].text()
            )
        }
    }
}
