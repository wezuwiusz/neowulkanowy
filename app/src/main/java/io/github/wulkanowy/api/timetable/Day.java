package io.github.wulkanowy.api.timetable;

import java.util.ArrayList;
import java.util.List;

public class Day {

    private List<Lesson> lessons = new ArrayList<>();

    private String date = "";

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

    public boolean isFreeDay() {
        return isFreeDay;
    }

    public Day setFreeDay(boolean freeDay) {
        isFreeDay = freeDay;
        return this;
    }

    public String getFreeDayName() {
        return freeDayName;
    }

    public Day setFreeDayName(String freeDayName) {
        this.freeDayName = freeDayName;
        return this;
    }
}
