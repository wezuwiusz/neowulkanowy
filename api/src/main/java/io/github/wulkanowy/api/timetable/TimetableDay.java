package io.github.wulkanowy.api.timetable;

import io.github.wulkanowy.api.generic.Day;

@Deprecated
public class TimetableDay extends Day {

    private boolean isFreeDay = false;

    private String freeDayName = "";

    public boolean isFreeDay() {
        return isFreeDay;
    }

    public void setFreeDay(boolean freeDay) {
        isFreeDay = freeDay;
    }

    public String getFreeDayName() {
        return freeDayName;
    }

    public void setFreeDayName(String freeDayName) {
        this.freeDayName = freeDayName;
    }
}
