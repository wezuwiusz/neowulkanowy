package io.github.wulkanowy.api;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Map;

public class Client {

    private String protocol;

    private String host;

    private String symbol;

    private Cookies cookies = new Cookies();

    Client(String protocol, String host, String symbol) {
        this.protocol = protocol;
        this.host = host;
        this.symbol = symbol;
    }

    String getHost() {
        return host;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    Map<String, String> getCookies() {
        return cookies.getItems();
    }

    private String getFilledUrl(String url) {
        return url
                .replace("{schema}", protocol)
                .replace("{host}", host.replace(":", "%253A"))
                .replace("{symbol}", symbol == null ? "Default" : symbol);
    }

    Document getPageByUrl(String url) throws IOException {
        Connection.Response response = Jsoup.connect(getFilledUrl(url))
                .followRedirects(true)
                .cookies(getCookies())
                .execute();

        this.cookies.addItems(response.cookies());

        return response.parse();
    }

    public Document postPageByUrl(String url, String[][] params) throws IOException {
        Connection connection = Jsoup.connect(getFilledUrl(url));

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
        Connection.Response response = Jsoup.connect(getFilledUrl(url))
                .followRedirects(true)
                .ignoreContentType(true)
                .cookies(getCookies())
                .execute();

        this.cookies.addItems(response.cookies());

        return response.body();
    }

    public String postJsonStringByUrl(String url, String[][] params) throws IOException {
        Connection connection = Jsoup.connect(getFilledUrl(url));

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
