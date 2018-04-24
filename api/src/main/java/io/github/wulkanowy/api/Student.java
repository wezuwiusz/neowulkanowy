package io.github.wulkanowy.api;

public class Student implements ParamItem {

    private String id = "";

    private String name = "";

    private boolean current = false;

    public String getId() {
        return id;
    }

    public Student setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Student setName(String name) {
        this.name = name;
        return this;
    }

    public boolean isCurrent() {
        return current;
    }

    public Student setCurrent(boolean current) {
        this.current = current;
        return this;
    }
}
