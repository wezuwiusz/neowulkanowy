package io.github.wulkanowy.api;

public class Semester {

    private String number = "";
    private String id = "";
    private boolean isCurrent = false;

    public String getNumber() {
        return number;
    }

    public Semester setNumber(String number) {
        this.number = number;
        return this;
    }

    public String getId() {
        return id;
    }

    public Semester setId(String id) {
        this.id = id;
        return this;
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public Semester setCurrent(boolean current) {
        isCurrent = current;
        return this;
    }
}
