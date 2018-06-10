package io.github.wulkanowy.api;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private String symbol;

    private Date lastSuccessRequest;

    private Cookies cookies = new Cookies();

    private static final Logger logger = LoggerFactory.getLogger(Client.class);

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
            String[] path = url[1].split("/");
            host = path[0];
            if (path.length > 1) {
                symbol = path[1];
            }
            email = creds[2];
        }
    }

    private void login() throws IOException, VulcanException {
        if (isLoggedIn()) {
            return;
        }

        clearCookies();
        this.symbol = new Login(this).login(email, password, symbol);
        logger.info("Login successful on {} at {}", getHost(), new Date());
    }

    private boolean isLoggedIn() {
        logger.debug("Last success request: {}", lastSuccessRequest);
        logger.debug("Cookies: {}", getCookies().size());

        return getCookies().size() > 0 && lastSuccessRequest != null &&
                5 > TimeUnit.MILLISECONDS.toMinutes(new Date().getTime() - lastSuccessRequest.getTime());

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

    public void clearCookies() {
        cookies = new Cookies();
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

    public Document getPageByUrl(String url) throws IOException, VulcanException {
        return getPageByUrl(url, true, null);
    }

    public Document getPageByUrl(String url, boolean loginBefore) throws IOException, VulcanException {
        return getPageByUrl(url, loginBefore, null);
    }

    public synchronized Document getPageByUrl(String url, boolean loginBefore, Map<String, String> cookies) throws IOException, VulcanException {
        if (loginBefore) {
            login();
        }

        if (null != cookies) {
            this.cookies.addItems(cookies);
        }

        url = getFilledUrl(url);

        logger.debug("GET {}", url);

        Connection.Response response = Jsoup.connect(url)
                .followRedirects(true)
                .cookies(getCookies())
                .execute();

        this.cookies.addItems(response.cookies());

        Document doc = checkForErrors(response.parse(), response.statusCode());

        if (loginBefore) {
            lastSuccessRequest = new Date();
        }

        return doc;
    }

    public synchronized Document postPageByUrl(String url, String[][] params) throws IOException, VulcanException {
        url = getFilledUrl(url);

        logger.debug("POST {}", url);

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

        response.bufferUp(); // fixes cert parsing issues #109

        return checkForErrors(response.parse(), response.statusCode());
    }

    public String getJsonStringByUrl(String url) throws IOException, VulcanException {
        login();

        url = getFilledUrl(url);

        logger.debug("GET {}", url);

        Connection.Response response = Jsoup.connect(url)
                .followRedirects(true)
                .ignoreContentType(true)
                .cookies(getCookies())
                .execute();

        this.cookies.addItems(response.cookies());

        return response.body();
    }

    public String postJsonStringByUrl(String url, String[][] params) throws IOException, VulcanException {
        login();

        url = getFilledUrl(url);

        logger.debug("POST {}", url);

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

    Document checkForErrors(Document doc, int code) throws VulcanException {
        lastSuccessRequest = null;

        String title = doc.select("title").text();
        if ("Przerwa techniczna".equals(title)) {
            throw new VulcanOfflineException(title);
        }

        String singIn = doc.select(".loginButton").text();
        if ("Zaloguj się".equals(singIn)) {
            throw new NotLoggedInErrorException(singIn);
        }

        if ("Błąd strony".equals(title)) {
            throw new NotLoggedInErrorException(title + " " + doc.body() + ", status: " + code);
        }

        return doc;
    }
}
