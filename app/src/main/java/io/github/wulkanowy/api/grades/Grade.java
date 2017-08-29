package io.github.wulkanowy.api.grades;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Grade {
    protected int id;

    private int userID;

    private int subjectID;

    private String subject = "";

    protected String value = "";

    private String color = "";

    private String symbol = "";

    private String description = "";

    private String weight = "";

    private String date = "";

    private String teacher = "";

    private String semester = "";

    private boolean isNew;

    public int getId() {
        return id;
    }

    public Grade setId(int id) {
        this.id = id;

        return this;
    }

    public int getUserID() {
        return userID;
    }

    public Grade setUserID(int userID) {
        this.userID = userID;

        return this;
    }

    public int getSubjectID() {
        return subjectID;
    }

    public Grade setSubjectID(int subjectID) {
        this.subjectID = subjectID;

        return this;
    }

    public String getSubject() {
        return subject;
    }

    public Grade setSubject(String subject) {
        this.subject = subject;

        return this;
    }

    public String getValue() {
        return value;
    }

    public Grade setValue(String value) {
        this.value = value;

        return this;
    }

    public String getColor() {
        return color;
    }

    public Grade setColor(String color) {
        this.color = color;

        return this;
    }

    public String getSymbol() {
        return symbol;
    }

    public Grade setSymbol(String symbol) {
        this.symbol = symbol;

        return this;
    }

    public String getDescription() {
        return description;
    }

    public Grade setDescription(String description) {
        this.description = description;

        return this;
    }

    public String getWeight() {
        return weight;
    }

    public Grade setWeight(String weight) {
        this.weight = weight;

        return this;
    }

    public String getDate() {
        return date;
    }

    public Grade setDate(String date) {
        this.date = date;

        return this;
    }

    public String getTeacher() {
        return teacher;
    }

    public Grade setTeacher(String teacher) {
        this.teacher = teacher;

        return this;
    }

    public String getSemester() {
        return semester;
    }

    public Grade setSemester(String semester) {
        this.semester = semester;

        return this;
    }

    public boolean isNew() {
        return isNew;
    }

    public Grade setIsNew(boolean isNew) {
        this.isNew = isNew;

        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Grade grade = (Grade) o;

        return new EqualsBuilder()
                .append(subject, grade.subject)
                .append(value, grade.value)
                .append(color, grade.color)
                .append(symbol, grade.symbol)
                .append(description, grade.description)
                .append(weight, grade.weight)
                .append(date, grade.date)
                .append(teacher, grade.teacher)
                .append(semester, grade.semester)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(subject)
                .append(value)
                .append(color)
                .append(symbol)
                .append(description)
                .append(weight)
                .append(date)
                .append(teacher)
                .append(semester)
                .toHashCode();
    }
}
