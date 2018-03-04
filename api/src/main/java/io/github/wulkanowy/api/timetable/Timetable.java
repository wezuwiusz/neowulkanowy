package io.github.wulkanowy.api.timetable;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.github.wulkanowy.api.SnP;
import io.github.wulkanowy.api.generic.Day;
import io.github.wulkanowy.api.generic.Lesson;
import io.github.wulkanowy.api.generic.Week;

public class Timetable {

    private static final String TIMETABLE_PAGE_URL = "Lekcja.mvc/PlanLekcji?data=";

    private SnP snp;

    public Timetable(SnP snp) {
        this.snp = snp;
    }

    public Week<Day> getWeekTable() throws IOException, ParseException {
        return getWeekTable("");
    }

    public Week<Day> getWeekTable(final String tick) throws IOException, ParseException {
        Element table = snp.getSnPPageDocument(TIMETABLE_PAGE_URL + tick)
                .select(".mainContainer .presentData").first();

        List<Day> days = getDays(table.select("thead th"));

        setLessonToDays(table, days);

        return new Week<Day>()
                .setStartDayDate(days.get(0).getDate())
                .setDays(days);
    }

    private List<Day> getDays(Elements tableHeaderCells) throws ParseException {
        List<Day> days = new ArrayList<>();

        for (int i = 2; i < 7; i++) {
            String[] dayHeaderCell = tableHeaderCells.get(i).html().split("<br>");

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.ROOT);
            Date d = sdf.parse(dayHeaderCell[1].trim());
            sdf.applyPattern("yyyy-MM-dd");

            Day day = new Day();
            day.setDayName(dayHeaderCell[0]);
            day.setDate(sdf.format(d));

            if (tableHeaderCells.get(i).hasClass("free-day")) {
                day.setFreeDay(true);
                day.setFreeDayName(dayHeaderCell[2]);
            }

            days.add(day);
        }

        return days;
    }

    private void setLessonToDays(Element table, List<Day> days) {
        for (Element row : table.select("tbody tr")) {
            Elements hours = row.select("td");

            // fill hours in day
            for (int i = 2; i < hours.size(); i++) {
                Lesson lesson = new Lesson();

                String[] startEndEnd = hours.get(1).text().split(" ");
                lesson.setStartTime(startEndEnd[0]);
                lesson.setEndTime(startEndEnd[1]);
                lesson.setDate(days.get(i - 2).getDate());
                lesson.setNumber(hours.get(0).text());

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
                addLessonInfoFromElement(lesson, e.last());
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
            e.select(".x-treelabel-rlz").last().text("(" + warn.text() + ")");
            e.remove(1);
        }
    }

    private void addLessonInfoFromElement(Lesson lesson, Element e) {
        Elements spans = e.select("span");

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
            lesson.setDescription(StringUtils.substringBetween(spans.last().text(), "(", ")")
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
            lesson.setDescription(StringUtils.substringBetween(spans.last().text(), "(", ")")
                    + " (poprzednio: " + getLessonAndGroupInfoFromSpan(spans.get(0))[0] + ")");
        } else if (4 <= spans.size()) {
            lesson.setSubject(spans.get(0).text());
            lesson.setTeacher(spans.get(1).text());
            lesson.setRoom(spans.get(2).text());
            lesson.setDescription(StringUtils.substringBetween(spans.last().text(), "(", ")"));
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
        String[] subjectNameArray = span.text().split(" ");
        String groupName = subjectNameArray[subjectNameArray.length - 1];

        return new String[]{
                span.text().replace(" " + groupName, ""),
                StringUtils.substringBetween(groupName, "[", "]")
        };
    }
}
