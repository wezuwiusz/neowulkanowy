package io.github.wulkanowy.api;

import java.util.HashMap;
import java.util.Map;

class Cookies {

    private Map<String, String> jar = new HashMap<>();

    Map<String, String> getItems() {
        return jar;
    }

    void addItems(Map<String, String> items) {
        jar.putAll(items);
    }
}
