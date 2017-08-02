package io.github.wulkanowy.api;

import java.util.Map;

public class Cookies {

    private Map<String, String> cookies;

    public Map<String, String> getItems() {
        return cookies;
    }

    public Cookies setItems(Map<String, String> items) {
        this.cookies = items;
        return this;
    }

    public Cookies addItems(Map<String, String> items) {
        this.cookies.putAll(items);
        return this;
    }
}
