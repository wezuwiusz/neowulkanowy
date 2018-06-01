package io.github.wulkanowy.api.generic;

public class Diary implements ParamItem {

    private String id = "";

    private String name = "";

    private boolean current = false;

    public String getId() {
        return id;
    }

    public Diary setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Diary setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public boolean isCurrent() {
        return current;
    }

    public Diary setCurrent(boolean current) {
        this.current = current;
        return this;
    }
}
