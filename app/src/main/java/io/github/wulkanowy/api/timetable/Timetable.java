package io.github.wulkanowy.api.timetable;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.api.StudentAndParent;
import io.github.wulkanowy.api.login.LoginErrorException;

public class Timetable {

    private StudentAndParent snp;

    private String timetablePageUrl = "Lekcja.mvc/PlanLekcji?data=";

    public Timetable(StudentAndParent snp) {
        this.snp = snp;
    }

    public Week getWeekTable() throws IOException, LoginErrorException {
        return getWeekTable("");
    }

    public Week getWeekTable(String tick) throws IOException, LoginErrorException {
        Element table = snp.getSnPPageDocument(timetablePageUrl + tick)
                .select(".mainContainer .presentData").first();

        Elements tableHeaderCells = table.select("thead th");
        List<Day> days = new ArrayList<>();

        for (int i = 2; i < 7; i++) {
            String[] dayHeaderCell = tableHeaderCells.get(i).html().split("<br>");
            boolean isFreeDay = tableHeaderCells.get(i).hasClass("free-day");

            Day day = new Day();
            day.setDate(dayHeaderCell[1]);

            if (isFreeDay) {
                day.setFreeDay(isFreeDay);
                day.setFreeDayName(dayHeaderCell[2]);
            }

            days.add(day);
        }

        Elements hoursInDays = table.select("tbody tr");

        // fill days in week with lessons
        for (Element row : hoursInDays) {
            Elements hours = row.select("td");

            // fill hours in day
            for (int i = 2; i < hours.size(); i++) {
                Lesson lesson = new Lesson();

                Elements e = hours.get(i).select("div");
                switch (e.size()) {
                    case 1:
                        lesson = getLessonFromElement(e.first());
                        break;
                    case 3:
                        lesson = getLessonFromElement(e.get(1));
                        break;
                    default:
                        lesson.setEmpty(true);
                        break;
                }

                String[] startEndEnd = hours.get(1).text().split(" ");
                lesson.setStartTime(startEndEnd[0]);
                lesson.setEndTime(startEndEnd[1]);

                days.get(i - 2).setLesson(lesson);
            }
        }

        Element startDayCellHeader = tableHeaderCells.get(2);
        String[] dayDescription = startDayCellHeader.html().split("<br>");

        return new Week()
                .setStartDayDate(dayDescription[1])
                .setDays(days);
    }

    private Lesson getLessonFromElement(Element e) {
        Lesson lesson = new Lesson();
        Elements spans = e.select("span");

        lesson.setSubject(spans.get(0).text());
        lesson.setTeacher(spans.get(1).text());
        lesson.setRoom(spans.get(2).text());

        // okienko dla uczniów
        if (5 == spans.size()) {
            lesson.setTeacher(spans.get(2).text());
            lesson.setRoom(spans.get(3).text());
        }

        lesson = getLessonGroupDivisionInfo(lesson, spans);
        lesson = getLessonTypeInfo(lesson, spans);
        lesson = getLessonDescriptionInfo(lesson, spans);

        return lesson;
    }

    private Lesson getLessonGroupDivisionInfo(Lesson lesson, Elements e) {
        if ((4 == e.size() && (e.first().attr("class").equals("")) ||
                (5 == e.size() && e.first().hasClass(Lesson.CLASS_NEW_MOVED_IN_OR_CHANGED)))) {
            lesson.setDivisionIntoGroups(true);
            String[] subjectNameArray = lesson.getSubject().split(" ");
            String groupName = subjectNameArray[subjectNameArray.length - 1];
            lesson.setSubject(lesson.getSubject().replace(" " + groupName, ""));
            lesson.setGroupName(StringUtils.substringBetween(groupName, "[", "]"));
            lesson.setTeacher(e.get(2).text());
            lesson.setRoom(e.get(3).text());
        }

        return lesson;
    }

    private Lesson getLessonTypeInfo(Lesson lesson, Elements e) {
        if (e.first().hasClass(Lesson.CLASS_MOVED_OR_CANCELED)) {
            lesson.setMovedOrCanceled(true);
        } else if (e.first().hasClass(Lesson.CLASS_NEW_MOVED_IN_OR_CHANGED)) {
            lesson.setNewMovedInOrChanged(true);
        } else if (e.first().hasClass(Lesson.CLASS_PLANNING)) {
            lesson.setPlanning(true);
        }

        if (e.last().hasClass(Lesson.CLASS_REALIZED)
                || e.first().attr("class").equals("")) {
            lesson.setRealized(true);
        }

        return lesson;
    }

    private Lesson getLessonDescriptionInfo(Lesson lesson, Elements e) {
        if ((4 == e.size() || 5 == e.size())
                && (e.first().hasClass(Lesson.CLASS_MOVED_OR_CANCELED)
                || e.first().hasClass(Lesson.CLASS_NEW_MOVED_IN_OR_CHANGED))) {
            lesson.setDescription(StringUtils.substringBetween(e.last().text(), "(", ")"));
        }

        return lesson;
    }
}
