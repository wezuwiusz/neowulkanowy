package io.github.wulkanowy.api.generic;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class Week<T> {

    private List<T> days = new ArrayList<>();

    private String startDayDate = "";

    public T getDay(int index) {
        return days.get(index);
    }

    public List<T> getDays() {
        return days;
    }

    public Week<T> setDays(List<T> days) {
        this.days = days;
        return this;
    }

    public String getStartDayDate() {
        return startDayDate;
    }

    public Week<T> setStartDayDate(String startDayDate) {
        this.startDayDate = startDayDate;
        return this;
    }
}
