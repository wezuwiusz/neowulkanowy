package io.github.wulkanowy.api.attendance;

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
import io.github.wulkanowy.api.VulcanException;
import io.github.wulkanowy.api.generic.Day;
import io.github.wulkanowy.api.generic.Lesson;
import io.github.wulkanowy.api.generic.Week;

public class AttendanceTable {

    private final static String ATTENDANCE_PAGE_URL = "Frekwencja.mvc?data=";

    private SnP snp;

    public AttendanceTable(SnP snp) {
        this.snp = snp;
    }

    public Week<Day> getWeekTable() throws IOException, ParseException, VulcanException {
        return getWeekTable("");
    }

    public Week<Day> getWeekTable(String tick) throws IOException, ParseException, VulcanException {
        Element table = snp.getSnPPageDocument(ATTENDANCE_PAGE_URL + tick)

                .select(".mainContainer .presentData").first();

        Elements headerCells = table.select("thead th");
        List<Day> days = new ArrayList<>();

        for (int i = 1; i < headerCells.size(); i++) {
            String[] dayHeaderCell = headerCells.get(i).html().split("<br>");

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.ROOT);
            Date d = sdf.parse(dayHeaderCell[1].trim());
            sdf.applyPattern("yyyy-MM-dd");

            Day day = new Day();
            day.setDayName(dayHeaderCell[0]);
            day.setDate(sdf.format(d));
            days.add(day);
        }

        Elements hoursInDays = table.select("tbody tr");

        // fill days in week with lessons
        for (Element row : hoursInDays) {
            Elements hours = row.select("td");

            // fill hours in day
            int size = hours.size();
            for (int i = 1; i < size; i++) {
                Lesson lesson = new Lesson();
                lesson.setDate(days.get(i - 1).getDate());
                lesson.setNumber(Integer.valueOf(hours.get(0).text()));

                addLessonDetails(lesson, hours.get(i));

                days.get(i - 1).setLesson(lesson);
            }
        }

        return new Week<Day>()
                .setStartDayDate(days.get(0).getDate())
                .setDays(days);
    }

    private void addLessonDetails(Lesson lesson, Element cell) {
        lesson.setSubject(cell.select("span").text());

        if (LessonTypes.CLASS_NOT_EXIST.equals(cell.attr("class"))) {
            lesson.setNotExist(true);
            lesson.setEmpty(true);

            return;
        }

        switch (cell.select("div").attr("class")) {
            case LessonTypes.CLASS_PRESENCE:
                lesson.setPresence(true);
                break;
            case LessonTypes.CLASS_ABSENCE_UNEXCUSED:
                lesson.setAbsenceUnexcused(true);
                break;
            case LessonTypes.CLASS_ABSENCE_EXCUSED:
                lesson.setAbsenceExcused(true);
                break;
            case LessonTypes.CLASS_ABSENCE_FOR_SCHOOL_REASONS:
                lesson.setAbsenceForSchoolReasons(true);
                break;
            case LessonTypes.CLASS_UNEXCUSED_LATENESS:
                lesson.setUnexcusedLateness(true);
                break;
            case LessonTypes.CLASS_EXCUSED_LATENESS:
                lesson.setExcusedLateness(true);
                break;
            case LessonTypes.CLASS_EXEMPTION:
                lesson.setExemption(true);
                break;

            default:
                lesson.setEmpty(true);
                break;
        }
    }
}
