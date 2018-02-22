package io.github.wulkanowy.api;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.api.login.NotLoggedInErrorException;

public class StudentAndParent implements SnP {

    private static final String startPageUrl = "{schema}://uonetplus.{host}/{symbol}/Start.mvc/Index";

    private static final String baseUrl = "{schema}://uonetplus-opiekun.{host}/{symbol}/{ID}/";

    private static final String SYMBOL_PLACEHOLDER = "{symbol}";

    private static final String GRADES_PAGE_URL = "Oceny/Wszystkie";

    private Client client;

    private String logHost = "vulcan.net.pl";

    private String protocolSchema = "https";

    private String symbol;

    private String id;

    public StudentAndParent(Client client, String symbol) {
        this.client = client;
        this.symbol = symbol;
    }

    public StudentAndParent(Client client, String symbol, String id) {
        this(client, symbol);
        this.id = id;
    }

    public void setProtocolSchema(String schema) {
        this.protocolSchema = schema;
    }

    public String getLogHost() {
        return logHost;
    }

    public void setLogHost(String hostname) {
        this.logHost = hostname;
    }

    public String getStartPageUrl() {
        return startPageUrl
                .replace("{schema}", protocolSchema)
                .replace("{host}", logHost)
                .replace(SYMBOL_PLACEHOLDER, getSymbol());
    }

    public String getBaseUrl() {
        return baseUrl
                .replace("{schema}", protocolSchema)
                .replace("{host}", logHost)
                .replace(SYMBOL_PLACEHOLDER, getSymbol())
                .replace("{ID}", getId());
    }

    public String getGradesPageUrl() {
        return getBaseUrl() + GRADES_PAGE_URL;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getId() {
        return id;
    }

    public void storeContextCookies() throws IOException, NotLoggedInErrorException {
        client.getPageByUrl(getSnpPageUrl());
    }

    public String getSnpPageUrl() throws IOException, NotLoggedInErrorException {
        if (null != getId()) {
            return getBaseUrl();
        }

        // get url to uonetplus-opiekun.vulcan.net.pl
        Document startPage = client.getPageByUrl(getStartPageUrl());
        Element studentTileLink = startPage.select(".panel.linkownia.pracownik.klient > a").first();

        if (null == studentTileLink) {
            throw new NotLoggedInErrorException();
        }

        String snpPageUrl = studentTileLink.attr("href");

        this.id = getExtractedIdFromUrl(snpPageUrl);

        return snpPageUrl;
    }

    public String getExtractedIdFromUrl(String snpPageUrl) throws NotLoggedInErrorException {
        String[] path = snpPageUrl.split(getLogHost() + "/")[1].split("/");

        if (4 != path.length) {
            throw new NotLoggedInErrorException();
        }

        return path[1];
    }

    public String getRowDataChildValue(Element e, int index) {
        return e.select(".daneWiersz .wartosc").get(index - 1).text();
    }

    public Document getSnPPageDocument(String url) throws IOException {
        return client.getPageByUrl(getBaseUrl()
                .replace(SYMBOL_PLACEHOLDER, getSymbol())
                .replace("{ID}", getId()) + url);
    }

    public List<Semester> getSemesters() throws IOException {
        return getSemesters(getSnPPageDocument(getGradesPageUrl()));
    }

    public List<Semester> getSemesters(Document gradesPage) {
        Elements semesterOptions = gradesPage.select("#okresyKlasyfikacyjneDropDownList option");

        List<Semester> semesters = new ArrayList<>();

        for (Element e : semesterOptions) {
            Semester semester = new Semester()
                    .setId(e.text())
                    .setNumber(e.attr("value"));

            if ("selected".equals(e.attr("selected"))) {
                semester.setCurrent(true);
            }

            semesters.add(semester);
        }

        return semesters;
    }

    public Semester getCurrentSemester(List<Semester> semesterList) {
        Semester current = null;
        for (Semester s : semesterList) {
            if (s.isCurrent()) {
                current = s;
                break;
            }
        }

        return current;
    }
}
