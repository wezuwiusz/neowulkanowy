package io.github.wulkanowy.api.timetable

import io.github.wulkanowy.api.SnP
import io.github.wulkanowy.api.getDateAsTick
import io.github.wulkanowy.api.getFormattedDate
import org.apache.commons.lang3.StringUtils
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class TimetableKt(private val snp: SnP) {

    fun getTimetable() = getTimetable("")

    fun getTimetable(start: String): List<TimetableLesson> {
        val table = snp.getSnPPageDocument("Lekcja.mvc/PlanZajec?data=" + getDateAsTick(start))
                .select(".mainContainer .presentData").first()

        val days = getDays(table.select("thead th"))

        return table.select("tbody tr").map {
            val hours = it.select("td")
            hours.drop(2).mapIndexed { i, item ->
                getLesson(item, days[i], hours[1].text().split(" "), hours[0].text().toInt())
            }
        }.flatten().sortedBy {
            it.date
        }.filter {
            it.subject.isNotBlank()
        }
    }

    private fun getDays(el: Elements): List<Pair<String, String>> {
        return el.drop(2).map {
            val info = it.html().split("<br>")

            Pair(getFormattedDate(info[1].trim()), info.getOrElse(2) { "" }.trim())
        }
    }

    private fun getLesson(cell: Element, day: Pair<String, String>, hours: List<String>, number: Int): TimetableLesson {
        val lesson = TimetableLesson(
                number = number,
                date = day.first,
                freeDayName = day.second,
                startTime = hours[0],
                endTime = hours[1]
        )

        addLessonDetails(lesson, cell.select("div"))

        return lesson
    }

    private fun addLessonDetails(lesson: TimetableLesson, e: Elements) {
        moveWarningToLessonNode(e)

        when (e.size) {
            1 -> addLessonInfoFromElement(lesson, e.first())
            2 -> {
                val span = e.last().selectFirst("span")
                when {
                    null == span -> addLessonInfoFromElement(lesson, e.first())
                    span.hasClass(Types.CLASS_MOVED_OR_CANCELED) -> {
                        lesson.newMovedInOrChanged = true
                        lesson.description = "poprzednio: " + getLessonAndGroupInfoFromSpan(span)[0]
                        addLessonInfoFromElement(lesson, e.first())
                    }
                    else -> addLessonInfoFromElement(lesson, e.last())
                }
            }
            3 -> addLessonInfoFromElement(lesson, e[1])
            else -> lesson.empty = true
        }
    }

    private fun moveWarningToLessonNode(e: Elements) {
        val warn = e.select(".uwaga-panel")

        if (!warn.isEmpty()) {
            e.select("span").last()
                    .addClass("x-treelabel-rlz")
                    .text(warn.text())
            e.removeAt(1)
        }
    }

    private fun addLessonInfoFromElement(lesson: TimetableLesson, e: Element) {
        val spans = e.select("span")

        if (spans.isEmpty()) {
            return
        }

        addTypeInfo(lesson, spans)
        addNormalLessonInfo(lesson, spans)
        addChangesInfo(lesson, spans)
        addGroupLessonInfo(lesson, spans)
    }

    private fun addTypeInfo(lesson: TimetableLesson, spans: Elements) {
        if (spans.first().hasClass(Types.CLASS_PLANNING)) {
            lesson.planning = true
        }

        if (spans.first().hasClass(Types.CLASS_MOVED_OR_CANCELED)) {
            lesson.movedOrCanceled = true
        }

        if (spans.first().hasClass(Types.CLASS_NEW_MOVED_IN_OR_CHANGED)) {
            lesson.newMovedInOrChanged = true
        }

        if (spans.last().hasClass(Types.CLASS_REALIZED) || "" == spans.first().attr("class")) {
            lesson.realized = true
        }
    }

    private fun addNormalLessonInfo(lesson: TimetableLesson, spans: Elements) {
        if (3 == spans.size) {
            lesson.subject = spans[0].text()
            lesson.teacher = spans[1].text()
            lesson.room = spans[2].text()
        }
    }

    private fun addChangesInfo(lesson: TimetableLesson, spans: Elements) {
        if (!spans.last().hasClass(Types.CLASS_REALIZED)) {
            return
        }

        when {
            7 == spans.size -> {
                lesson.subject = spans[3].text()
                lesson.teacher = spans[4].text()
                lesson.room = spans[5].text()
                lesson.movedOrCanceled = false
                lesson.newMovedInOrChanged = true
                lesson.description = (StringUtils.defaultString(StringUtils.substringBetween(
                        spans.last().text(), "(", ")"), spans.last().text())
                        + " (poprzednio: " + spans[0].text() + ")")
            }
            9 == spans.size -> {
                val subjectAndGroupInfo = getLessonAndGroupInfoFromSpan(spans[4])
                lesson.subject = subjectAndGroupInfo[0]
                lesson.groupName = subjectAndGroupInfo[1]
                lesson.teacher = spans[6].text()
                lesson.room = spans[7].text()
                lesson.movedOrCanceled = false
                lesson.newMovedInOrChanged = true
                lesson.divisionIntoGroups = true
                lesson.description = (StringUtils.defaultString(StringUtils.substringBetween(
                        spans.last().text(), "(", ")"), spans.last().text())
                        + " (poprzednio: " + getLessonAndGroupInfoFromSpan(spans[0])[0] + ")")
            }
            4 <= spans.size -> {
                lesson.subject = spans[0].text()
                lesson.teacher = spans[1].text()
                lesson.room = spans[2].text()
                lesson.description = StringUtils.defaultString(StringUtils.substringBetween(
                        spans.last().text(), "(", ")"), spans.last().text())
            }
        }
    }

    private fun addGroupLessonInfo(lesson: TimetableLesson, spans: Elements) {
        if (4 == spans.size && !spans.last().hasClass(Types.CLASS_REALIZED)) {
            lesson.room = spans.last().text()
        }

        if (4 == spans.size && !spans.last().hasClass(Types.CLASS_REALIZED) || 5 == spans.size) {
            val subjectAndGroupInfo = getLessonAndGroupInfoFromSpan(spans[0])
            lesson.subject = subjectAndGroupInfo[0]
            lesson.groupName = subjectAndGroupInfo[1]
            lesson.teacher = spans[2].text()
            lesson.divisionIntoGroups = true
        }

        if (5 == spans.size) {
            lesson.room = spans[3].text()
        }
    }

    private fun getLessonAndGroupInfoFromSpan(span: Element): Array<String> {
        if (!span.text().contains("[")) {
            return arrayOf(span.text(), "")
        }

        val subjectNameArray = span.text().split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val groupName = subjectNameArray[subjectNameArray.size - 1]

        return arrayOf(span.text().replace(" $groupName", ""), StringUtils.defaultString(StringUtils.substringBetween(
                groupName, "[", "]"), groupName))
    }

    private object Types {
        const val CLASS_PLANNING = "x-treelabel-ppl"
        const val CLASS_REALIZED = "x-treelabel-rlz"
        const val CLASS_MOVED_OR_CANCELED = "x-treelabel-inv"
        const val CLASS_NEW_MOVED_IN_OR_CHANGED = "x-treelabel-zas"
    }
}
