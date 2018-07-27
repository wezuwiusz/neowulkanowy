package io.github.wulkanowy.api.timetable;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.api.SnP;
import io.github.wulkanowy.api.VulcanException;
import io.github.wulkanowy.api.generic.Lesson;
import io.github.wulkanowy.api.generic.Week;

import static io.github.wulkanowy.api.DateTimeUtilsKt.getDateAsTick;
import static io.github.wulkanowy.api.DateTimeUtilsKt.getFormattedDate;

@Deprecated
public class Timetable {

    private static final String TIMETABLE_PAGE_URL = "Lekcja.mvc/PlanZajec?data=";

    private SnP snp;

    private static final Logger logger = LoggerFactory.getLogger(Timetable.class);

    public Timetable(SnP snp) {
        this.snp = snp;
    }

    public Week<TimetableDay> getWeekTable() throws IOException, VulcanException {
        return getWeekTable("");
    }

    public Week<TimetableDay> getWeekTable(final String date) throws IOException, VulcanException {
        Element table = snp.getSnPPageDocument(TIMETABLE_PAGE_URL + getDateAsTick(date))
                .select(".mainContainer .presentData").first();

        List<TimetableDay> days = getDays(table.select("thead th"));

        setLessonToDays(table, days);

        return new Week<TimetableDay>()
                .setStartDayDate(days.get(0).getDate())
                .setDays(days);
    }

    private List<TimetableDay> getDays(Elements tableHeaderCells) {
        List<TimetableDay> days = new ArrayList<>();
        int numberOfDays = tableHeaderCells.size();

        if (numberOfDays > 7) {
            logger.info("Number of days: {}", numberOfDays);
        }

        for (int i = 2; i < numberOfDays; i++) {
            String[] dayHeaderCell = tableHeaderCells.get(i).html().split("<br>");

            TimetableDay day = new TimetableDay();
            day.setDayName(dayHeaderCell[0]);
            day.setDate(getFormattedDate(dayHeaderCell[1].trim()));

            if (tableHeaderCells.get(i).hasClass("free-day")) {
                day.setFreeDay(true);
                day.setFreeDayName(dayHeaderCell[2]);
            }

            days.add(day);
        }

        return days;
    }

    private void setLessonToDays(Element table, List<TimetableDay> days) {
        for (Element row : table.select("tbody tr")) {
            Elements hours = row.select("td");

            // fill hours in day
            for (int i = 2; i < hours.size(); i++) {
                Lesson lesson = new Lesson();

                String[] startEndEnd = hours.get(1).text().split(" ");
                lesson.setStartTime(startEndEnd[0]);
                lesson.setEndTime(startEndEnd[1]);
                lesson.setDate(days.get(i - 2).getDate());
                lesson.setNumber(Integer.valueOf(hours.get(0).text()));

                addLessonDetails(lesson, hours.get(i).select("div"));

                days.get(i - 2).setLesson(lesson);
            }
        }
    }

    private void addLessonDetails(Lesson lesson, Elements e) {
        moveWarningToLessonNode(e);

        switch (e.size()) {
            case 1:
                addLessonInfoFromElement(lesson, e.first());
                break;
            case 2:
                Element span = e.last().selectFirst("span");
                if (null == span) {
                    addLessonInfoFromElement(lesson, e.first());
                } else if (span.hasClass(LessonTypes.CLASS_MOVED_OR_CANCELED)) {
                    lesson.setNewMovedInOrChanged(true);
                    lesson.setDescription("poprzednio: " + getLessonAndGroupInfoFromSpan(span)[0]);
                    addLessonInfoFromElement(lesson, e.first());
                } else {
                    addLessonInfoFromElement(lesson, e.last());
                }
                break;
            case 3:
                addLessonInfoFromElement(lesson, e.get(1));
                break;
            default:
                lesson.setEmpty(true);
                break;
        }
    }

    private void moveWarningToLessonNode(Elements e) {
        Elements warn = e.select(".uwaga-panel");

        if (!warn.isEmpty()) {
            e.select("span").last()
                    .addClass("x-treelabel-rlz")
                    .text(warn.text());
            e.remove(1);
        }
    }

    private void addLessonInfoFromElement(Lesson lesson, Element e) {
        Elements spans = e.select("span");

        if (spans.isEmpty()) {
            logger.warn("Lesson span is empty");
            return;
        }

        addTypeInfo(lesson, spans);
        addNormalLessonInfo(lesson, spans);
        addChangesInfo(lesson, spans);
        addGroupLessonInfo(lesson, spans);
    }

    private void addTypeInfo(Lesson lesson, Elements spans) {
        if (spans.first().hasClass(LessonTypes.CLASS_PLANNING)) {
            lesson.setPlanning(true);
        }

        if (spans.first().hasClass(LessonTypes.CLASS_MOVED_OR_CANCELED)) {
            lesson.setMovedOrCanceled(true);
        }

        if (spans.first().hasClass(LessonTypes.CLASS_NEW_MOVED_IN_OR_CHANGED)) {
            lesson.setNewMovedInOrChanged(true);
        }

        if (spans.last().hasClass(LessonTypes.CLASS_REALIZED) || "".equals(spans.first().attr("class"))) {
            lesson.setRealized(true);
        }
    }

    private void addNormalLessonInfo(Lesson lesson, Elements spans) {
        if (3 == spans.size()) {
            lesson.setSubject(spans.get(0).text());
            lesson.setTeacher(spans.get(1).text());
            lesson.setRoom(spans.get(2).text());
        }
    }

    private void addChangesInfo(Lesson lesson, Elements spans) {
        if (!spans.last().hasClass(LessonTypes.CLASS_REALIZED)) {
            return;
        }

        if (7 == spans.size()) {
            lesson.setSubject(spans.get(3).text());
            lesson.setTeacher(spans.get(4).text());
            lesson.setRoom(spans.get(5).text());
            lesson.setMovedOrCanceled(false);
            lesson.setNewMovedInOrChanged(true);
            lesson.setDescription(StringUtils.defaultString(StringUtils.substringBetween(
                    spans.last().text(), "(", ")"), spans.last().text())
                    + " (poprzednio: " + spans.get(0).text() + ")");
        } else if (9 == spans.size()) {
            String[] subjectAndGroupInfo = getLessonAndGroupInfoFromSpan(spans.get(4));
            lesson.setSubject(subjectAndGroupInfo[0]);
            lesson.setGroupName(subjectAndGroupInfo[1]);
            lesson.setTeacher(spans.get(6).text());
            lesson.setRoom(spans.get(7).text());
            lesson.setMovedOrCanceled(false);
            lesson.setNewMovedInOrChanged(true);
            lesson.setDivisionIntoGroups(true);
            lesson.setDescription(StringUtils.defaultString(StringUtils.substringBetween(
                    spans.last().text(), "(", ")"), spans.last().text())
                    + " (poprzednio: " + getLessonAndGroupInfoFromSpan(spans.get(0))[0] + ")");
        } else if (4 <= spans.size()) {
            lesson.setSubject(spans.get(0).text());
            lesson.setTeacher(spans.get(1).text());
            lesson.setRoom(spans.get(2).text());
            lesson.setDescription(StringUtils.defaultString(StringUtils.substringBetween(
                    spans.last().text(), "(", ")"), spans.last().text()));
        }
    }

    private void addGroupLessonInfo(Lesson lesson, Elements spans) {
        if (4 == spans.size() && !spans.last().hasClass(LessonTypes.CLASS_REALIZED)) {
            lesson.setRoom(spans.last().text());
        }

        if ((4 == spans.size() && !spans.last().hasClass(LessonTypes.CLASS_REALIZED) || 5 == spans.size())) {
            String[] subjectAndGroupInfo = getLessonAndGroupInfoFromSpan(spans.get(0));
            lesson.setSubject(subjectAndGroupInfo[0]);
            lesson.setGroupName(subjectAndGroupInfo[1]);
            lesson.setTeacher(spans.get(2).text());
            lesson.setDivisionIntoGroups(true);
        }

        if (5 == spans.size()) {
            lesson.setRoom(spans.get(3).text());
        }
    }

    private String[] getLessonAndGroupInfoFromSpan(Element span) {
        if (!span.text().contains("[")) {
            return new String[] {span.text(), ""};
        }

        String[] subjectNameArray = span.text().split(" ");
        String groupName = subjectNameArray[subjectNameArray.length - 1];

        return new String[]{
                span.text().replace(" " + groupName, ""),
                StringUtils.defaultString(StringUtils.substringBetween(
                        groupName, "[", "]"), groupName)
        };
    }
}
