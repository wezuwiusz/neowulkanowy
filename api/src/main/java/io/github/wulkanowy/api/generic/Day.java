package io.github.wulkanowy.api.generic;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class Day {

    private List<Lesson> lessons = new ArrayList<>();

    protected String date = "";

    private String dayName = "";

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

    public Day setDayName(String dayName) {
        this.dayName = dayName;
        return this;
    }
}
