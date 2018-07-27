package io.github.wulkanowy.api.exams;

import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.api.generic.Day;

@Deprecated
public class ExamDay extends Day {

    private List<Exam> examList = new ArrayList<>();

    public List<Exam> getExamList() {
        return examList;
    }

    public void addExam(Exam exam) {
        this.examList.add(exam);
    }
}
