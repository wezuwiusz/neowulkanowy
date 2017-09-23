package io.github.wulkanowy.api;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Map;

public abstract class Api {

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

    public Document getPageByUrl(String url) throws IOException {
        return Jsoup.connect(url)
                .followRedirects(true)
                .cookies(getCookies())
                .get();
    }
}
