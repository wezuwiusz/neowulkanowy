package io.github.wulkanowy.api.generic;

import java.util.ArrayList;
import java.util.List;

public class Day {

    private List<Lesson> lessons = new ArrayList<>();

    protected String date = "";

    private String dayName = "";

    private boolean isFreeDay = false;

    private String freeDayName = "";

    public Lesson getLesson(int index) {
        return lessons.get(index);
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public Day setLesson(Lesson lesson) {
        this.lessons.add(lesson);
        return this;
    }

    public String getDate() {
        return date;
    }

    public Day setDate(String date) {
        this.date = date;
        return this;
    }

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

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
