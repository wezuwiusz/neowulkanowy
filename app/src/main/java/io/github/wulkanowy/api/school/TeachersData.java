package io.github.wulkanowy.api.school;

import java.util.List;

public class TeachersData {

    private String className = "";

    private String[] classTeacher;

    private List<Subject> subjects;

    public String getClassName() {
        return className;
    }

    public TeachersData setClassName(String className) {
        this.className = className;
        return this;
    }

    public String[] getClassTeacher() {
        return classTeacher;
    }

    public TeachersData setClassTeacher(String[] classTeacher) {
        this.classTeacher = classTeacher;
        return this;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public TeachersData setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
        return this;
    }
}
