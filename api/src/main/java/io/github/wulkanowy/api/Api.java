package io.github.wulkanowy.api;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Map;

public abstract class Api {

    protected Cookies cookies = new Cookies();

    public Cookies getCookiesObject() {
        return cookies;
    }

    public Map<String, String> getCookies() {
        return cookies.getItems();
    }

    public Document getPageByUrl(String url) throws IOException {
        Connection.Response response = Jsoup.connect(url)
                .followRedirects(true)
                .cookies(getCookies())
                .execute();

        this.cookies.addItems(response.cookies());

        return response.parse();
    }

    public Document postPageByUrl(String url, String[][] params) throws IOException {
        Connection connection = Jsoup.connect(url);

        for (String[] data : params) {
            connection.data(data[0], data[1]);
        }

        Connection.Response response = connection.cookies(getCookies())
                .followRedirects(true)
                .method(Connection.Method.POST)
                .execute();

        this.cookies.addItems(response.cookies());

        return response.parse();
    }
}
