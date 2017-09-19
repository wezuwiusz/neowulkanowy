package io.github.wulkanowy.api.attendance;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.api.StudentAndParent;

public class Table {

    private StudentAndParent snp;

    private String attendancePageUrl = "Frekwencja.mvc?data=";

    public Table(StudentAndParent snp) {
        this.snp = snp;
    }

    public Week getWeekTable() throws IOException {
        return getWeekTable("");
    }

    public Week getWeekTable(String tick) throws IOException {
        Element table = snp.getSnPPageDocument(attendancePageUrl + tick)
                .select(".mainContainer .presentData").first();

        Elements headerCells = table.select("thead th");
        List<Day> days = new ArrayList<>();

        for (int i = 1; i < headerCells.size(); i++) {
            days.add(new Day().setDate(headerCells.get(i).html().split("<br>")[1]));
        }

        Elements hoursInDays = table.select("tbody tr");

        // fill days in week with lessons
        for (Element row : hoursInDays) {
            Elements hours = row.select("td");

            // fill hours in day
            int size = hours.size();
            for (int i = 1; i < size; i++) {
                days.get(i - 1).setLesson(getNewLesson(hours.get(i)));
            }
        }

        String[] dayDescription = headerCells.get(1).html().split("<br>");

        return new Week()
                .setStartDayDate(dayDescription[1])
                .setDays(days);
    }

    private Lesson getNewLesson(Element cell) {
        Lesson lesson = new Lesson();
        lesson.setSubject(cell.select("span").text());

        if (Lesson.CLASS_NOT_EXIST.equals(cell.attr("class"))) {
            lesson.setNotExist(true);
            lesson.setEmpty(true);

            return lesson;
        }

        switch (cell.select("div").attr("class")) {
            case Lesson.CLASS_PRESENCE:
                lesson.setPresence(true);
                break;
            case Lesson.CLASS_ABSENCE_UNEXCUSED:
                lesson.setAbsenceUnexcused(true);
                break;
            case Lesson.CLASS_ABSENCE_EXCUSED:
                lesson.setAbsenceExcused(true);
                break;
            case Lesson.CLASS_ABSENCE_FOR_SCHOOL_REASONS:
                lesson.setAbsenceForSchoolReasons(true);
                break;
            case Lesson.CLASS_UNEXCUSED_LATENESS:
                lesson.setUnexcusedLateness(true);
                break;
            case Lesson.CLASS_EXCUSED_LATENESS:
                lesson.setExcusedLateness(true);
                break;
            case Lesson.CLASS_EXEMPTION:
                lesson.setExemption(true);
                break;

            default:
                lesson.setEmpty(true);
                break;
        }

        return lesson;
    }
}
