package io.github.wulkanowy.api.grades;

public class Grade {

    private String subject;

    private String value;

    private String color;

    private String description;

    private String weight;

    private String date;

    private String teacher;

    public Grade setSubject(String subject) {
        this.subject = subject;

        return this;
    }

    public Grade setValue(String value) {
        this.value = value;

        return this;
    }

    public Grade setColor(String color) {
        this.color = color;

        return this;
    }

    public Grade setDescription(String description) {
        this.description = description;

        return this;
    }

    public Grade setWeight(String weight) {
        this.weight = weight;

        return this;
    }

    public Grade setDate(String date) {
        this.date = date;

        return this;
    }

    public Grade setTeacher(String teacher) {
        this.teacher = teacher;

        return this;
    }

    public String getSubject() {
        return subject;
    }

    public String getValue() {
        return value;
    }

    public String getColor() {
        return color;
    }

    public String getDescription() {
        return description;
    }

    public String getWeight() {
        return weight;
    }

    public String getDate() {
        return date;
    }

    public String getTeacher() {
        return teacher;
    }
}
