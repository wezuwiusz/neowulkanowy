package io.github.wulkanowy.api;

import java.util.Map;

public abstract class Vulcan {

    private Cookies cookies;

    public Vulcan(Cookies cookies) {
        this.cookies = cookies;
    }

    public Map<String, String> getJar() {
        return cookies.getAll();
    }

    public void setCookies(Map<String, String> items) {
        cookies.setItems(items);
    }
}
