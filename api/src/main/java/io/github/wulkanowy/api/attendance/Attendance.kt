package io.github.wulkanowy.api.attendance

import io.github.wulkanowy.api.SnP
import io.github.wulkanowy.api.getDateAsTick
import io.github.wulkanowy.api.getFormattedDate
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class Attendance(private val snp: SnP) {

    fun getAttendance() = getAttendance("")

    fun getAttendance(start: String): List<AttendanceLesson> {
        val table = snp.getSnPPageDocument("Frekwencja.mvc?data=" + getDateAsTick(start))
                .selectFirst(".mainContainer .presentData")

        val days = getDays(table.select("thead th"))

        return table.select("tbody tr").map {
            val hours = it.select("td")
            hours.drop(1).mapIndexed { i, item ->
                getLesson(item, days[i], hours[0].text().toInt())
            }
        }.flatten().sortedBy {
            it.date
        }.filter {
            it.subject.isNotBlank()
        }
    }

    private fun getDays(el: Elements): List<String> {
        return el.drop(1).map {
            getFormattedDate(it.html().split("<br>")[1])
        }
    }

    private fun getLesson(cell: Element, date: String, number: Int): AttendanceLesson {
        val lesson = AttendanceLesson(number = number, date = date, subject = cell.select("span").text())

        if (Types.CLASS_NOT_EXIST == cell.attr("class")) {
            lesson.notExist = true

            return lesson
        }

        when (cell.select("div").attr("class")) {
            Types.CLASS_PRESENCE -> lesson.presence = true
            Types.CLASS_ABSENCE_UNEXCUSED -> lesson.absenceUnexcused = true
            Types.CLASS_ABSENCE_EXCUSED -> lesson.absenceExcused = true
            Types.CLASS_ABSENCE_FOR_SCHOOL_REASONS -> lesson.absenceForSchoolReasons = true
            Types.CLASS_UNEXCUSED_LATENESS -> lesson.unexcusedLateness = true
            Types.CLASS_EXCUSED_LATENESS -> lesson.excusedLateness = true
            Types.CLASS_EXEMPTION -> lesson.exemption = true
        }

        return lesson
    }

    private object Types {
        const val CLASS_NOT_EXIST = "x-sp-nieobecny-w-oddziale"
        const val CLASS_PRESENCE = "x-obecnosc"
        const val CLASS_ABSENCE_UNEXCUSED = "x-nieobecnosc-nieuspr"
        const val CLASS_ABSENCE_EXCUSED = "x-nieobecnosc-uspr"
        const val CLASS_ABSENCE_FOR_SCHOOL_REASONS = "x-nieobecnosc-przycz-szkol"
        const val CLASS_UNEXCUSED_LATENESS = "x-sp-nieusprawiedliwione"
        const val CLASS_EXCUSED_LATENESS = "x-sp-spr"
        const val CLASS_EXEMPTION = "x-sp-zwolnienie"
    }
}
