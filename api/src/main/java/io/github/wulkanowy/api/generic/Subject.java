package io.github.wulkanowy.api.generic;

public class Subject {

    private int id = -1;

    private String name = "";

    public int getId() {
        return id;
    }

    public Subject setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Subject setName(String name) {
        this.name = name;
        return this;
    }
}
