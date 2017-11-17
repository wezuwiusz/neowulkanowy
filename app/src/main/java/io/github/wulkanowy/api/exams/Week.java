package io.github.wulkanowy.api.exams;

import java.util.ArrayList;
import java.util.List;

public class Week {

    private List<Day> dayList = new ArrayList<>();

    private String startDate = "";

    public List<Day> getDayList() {
        return dayList;
    }

    public Week setDayList(List<Day> dayList) {
        this.dayList = dayList;
        return this;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}
