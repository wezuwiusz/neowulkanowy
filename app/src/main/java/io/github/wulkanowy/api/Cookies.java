package io.github.wulkanowy.api;

import java.util.Map;

public class Cookies {

    private Map<String, String> cookies;

    public void setItems(Map<String, String> items) {
        cookies = items;
    }

    public void addItems(Map<String, String> items) {
        cookies.putAll(items);
    }

    public Map<String, String> getAll() {
        return cookies;
    }
}
