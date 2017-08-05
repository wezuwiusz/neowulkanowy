package io.github.wulkanowy.api.timetable;

import java.util.ArrayList;
import java.util.List;

public class Week {

    private List<Day> days = new ArrayList<>();

    private String startDayDate = "";

    public Day getDay(int index) {
        return days.get(index);
    }

    public List<Day> getDays() {
        return days;
    }

    public Week setDays(List<Day> days) {
        this.days = days;
        return this;
    }

    public String getStartDayDate() {
        return startDayDate;
    }

    public Week setStartDayDate(String startDayDate) {
        this.startDayDate = startDayDate;
        return this;
    }
}
