package io.github.wulkanowy.api;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Map;

public class Client {

    private Cookies cookies = new Cookies();

    Cookies getCookiesObject() {
        return cookies;
    }

    void setCookies(Cookies cookies) {
        this.cookies = cookies;
    }

     Map<String, String> getCookies() {
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

        Connection.Response response = connection
                .followRedirects(true)
                .method(Connection.Method.POST)
                .cookies(getCookies())
                .execute();

        this.cookies.addItems(response.cookies());

        return response.parse();
    }

    public String getJsonStringByUrl(String url) throws IOException {
        Connection.Response response = Jsoup.connect(url)
                .followRedirects(true)
                .ignoreContentType(true)
                .cookies(getCookies())
                .execute();

        this.cookies.addItems(response.cookies());

        return response.body();
    }

    public String postJsonStringByUrl(String url, String[][] params) throws IOException {
        Connection connection = Jsoup.connect(url);

        for (String[] data : params) {
            connection.data(data[0], data[1]);
        }

        Connection.Response response = connection
                .followRedirects(true)
                .ignoreContentType(true)
                .method(Connection.Method.POST)
                .cookies(getCookies())
                .execute();

        this.cookies.addItems(response.cookies());

        return response.body();
    }
}
