package io.github.wulkanowy.api.exams;

import java.util.ArrayList;
import java.util.List;

public class Day {

    private List<Exam> examList = new ArrayList<>();

    private String date = "";

    public String getDate() {
        return date;
    }

    public Day setDate(String date) {
        this.date = date;
        return this;
    }

    public List<Exam> getExamList() {
        return examList;
    }

    public Day addExam(Exam exam) {
        this.examList.add(exam);
        return this;
    }
}
