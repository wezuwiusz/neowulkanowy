package io.github.wulkanowy.api.school;

public class Subject {
    private String name = "";
    private String[] teachers;

    public String getName() {
        return name;
    }

    public Subject setName(String name) {
        this.name = name;
        return this;
    }

    public String[] getTeachers() {
        return teachers;
    }

    public Subject setTeachers(String[] teachers) {
        this.teachers = teachers;
        return this;
    }
}
