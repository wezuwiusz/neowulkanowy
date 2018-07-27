package io.github.wulkanowy.api.grades;

@Deprecated
public class Grade {

    protected String value = "";

    private String subject = "";

    private String color = "";

    private String symbol = "";

    private String description = "";

    private String weight = "";

    private String date = "";

    private String teacher = "";

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
}
