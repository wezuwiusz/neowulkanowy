package io.github.wulkanowy.api.attendance;

import java.util.ArrayList;
import java.util.List;

public class Day {

    private List<Lesson> lessons = new ArrayList<>();

    private String date = "";

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
}
