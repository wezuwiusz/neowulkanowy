package io.github.wulkanowy.api;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.github.wulkanowy.api.login.Login;

public class Client {

    private String protocol = "https";

    private String host = "vulcan.net.pl";

    private String email;

    private String password;

    private String symbol = "Default";

    private Login login;

    private Date lastSuccessRequest = new Date();

    private Cookies cookies = new Cookies();

    Client(String email, String password, String symbol) {
        this.email = email;
        this.password = password;
        this.symbol = symbol;

        setFullEndpointInfo(email);
    }

    private void setFullEndpointInfo(String info) {
        String[] creds = info.split("\\\\");

        email = info;

        if (creds.length > 2) {
            String[] url = creds[0].split("://");

            protocol = url[0];
            host = url[1];
            email = creds[2];
        }
    }

    private void login() throws IOException, VulcanException {
        if (isLoggedIn()) {
            return;
        }

        this.symbol = getLogin().login(email, password, symbol);
    }

    private boolean isLoggedIn() {
        return getCookies().size() > 0 &&
                29 > TimeUnit.MILLISECONDS.toMinutes(new Date().getTime() - lastSuccessRequest.getTime());

    }

    Login getLogin() {
        if (null != login) {
            return login;
        }

        login = new Login(this);

        return login;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    private Map<String, String> getCookies() {
        return cookies.getItems();
    }

    String getHost() {
        return host;
    }

    String getFilledUrl(String url) {
        return url
                .replace("{schema}", protocol)
                .replace("{host}", host.replace(":", "%253A"))
                .replace("{symbol}", symbol);
    }

    Document getPageByUrl(String url) throws IOException, VulcanException {
        login();

        Connection.Response response = Jsoup.connect(getFilledUrl(url))
                .followRedirects(true)
                .cookies(getCookies())
                .execute();

        this.cookies.addItems(response.cookies());

        Document doc = checkForErrors(response.parse());

        lastSuccessRequest = new Date();

        return doc;
    }

    public Document postPageByUrl(String url, String[][] params) throws IOException, VulcanException {
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

        return checkForErrors(response.parse());
    }

    public String getJsonStringByUrl(String url) throws IOException, VulcanException {
        login();

        Connection.Response response = Jsoup.connect(getFilledUrl(url))
                .followRedirects(true)
                .ignoreContentType(true)
                .cookies(getCookies())
                .execute();

        this.cookies.addItems(response.cookies());

        return response.body();
    }

    public String postJsonStringByUrl(String url, String[][] params) throws IOException, VulcanException {
        login();

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

    Document checkForErrors(Document doc) throws VulcanException {
        String title = doc.select("title").text();
        if ("Przerwa techniczna".equals(title)) {
            throw new VulcanOfflineException(title);
        }

        String singIn = doc.select(".loginButton").text();
        if ("Zaloguj się".equals(singIn)) {
            throw new NotLoggedInErrorException(singIn);
        }

        return doc;
    }
}
