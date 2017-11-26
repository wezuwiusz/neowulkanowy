package io.github.wulkanowy.api;

import java.util.HashMap;
import java.util.Map;

public class Cookies {

    private Map<String, String> jar = new HashMap<>();

    public Map<String, String> getItems() {
        return jar;
    }

    public Cookies setItems(Map<String, String> items) {
        this.jar = items;
        return this;
    }

    public Cookies addItems(Map<String, String> items) {
        this.jar.putAll(items);
        return this;
    }
}
