package io.github.wulkanowy.api.generic;

public class Month {

    private String name = "";

    private int value = 0;

    public String getName() {
        return name;
    }

    public Month setName(String name) {
        this.name = name;
        return this;
    }

    public int getValue() {
        return value;
    }

    public Month setValue(int value) {
        this.value = value;
        return this;
    }
}
