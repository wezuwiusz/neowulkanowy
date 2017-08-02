package io.github.wulkanowy.api;

import java.util.Map;

public abstract class Vulcan {

    protected Cookies cookies;

    public Cookies getCookiesObject() {
        return cookies;
    }

    public Map<String, String> getCookies() {
        return cookies.getItems();
    }

    public Cookies setCookies(Map<String, String> cookies) {
        this.cookies.setItems(cookies);
        return this.cookies;
    }

    public Cookies addCookies(Map<String, String> cookies) {
        this.cookies.addItems(cookies);
        return this.cookies;
    }
}
