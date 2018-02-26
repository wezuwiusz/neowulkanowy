package io.github.wulkanowy.api;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.api.login.NotLoggedInErrorException;

public class StudentAndParent implements SnP {

    private static final String START_PAGE_URL = "{schema}://uonetplus.{host}/{symbol}/Start.mvc/Index";

    private static final String BASE_URL = "{schema}://uonetplus-opiekun.{host}/{symbol}/{ID}/";

    private static final String GRADES_PAGE_URL = "Oceny/Wszystkie";

    private Client client;

    private String id;

    StudentAndParent(Client client) {
        this.client = client;
    }

    StudentAndParent(Client client, String id) {
        this(client);
        this.id = id;
    }

    private String getBaseUrl() {
        return BASE_URL.replace("{ID}", getId());
    }

    public String getId() {
        return id;
    }

    public void storeContextCookies() throws IOException, NotLoggedInErrorException {
        client.getPageByUrl(getSnpHomePageUrl());
    }

    String getSnpHomePageUrl() throws IOException, NotLoggedInErrorException {
        if (null != getId()) {
            return getBaseUrl();
        }

        // get url to uonetplus-opiekun.vulcan.net.pl
        Document startPage = client.getPageByUrl(START_PAGE_URL);
        Element studentTileLink = startPage.select(".panel.linkownia.pracownik.klient > a").first();

        if (null == studentTileLink) {
            throw new NotLoggedInErrorException();
        }

        String snpPageUrl = studentTileLink.attr("href");

        this.id = getExtractedIdFromUrl(snpPageUrl);

        return snpPageUrl;
    }

    String getExtractedIdFromUrl(String snpPageUrl) throws NotLoggedInErrorException {
        String[] path = snpPageUrl.split(client.getHost())[1].split("/");

        if (5 != path.length) {
            throw new NotLoggedInErrorException();
        }

        return path[2];
    }

    public String getRowDataChildValue(Element e, int index) {
        return e.select(".daneWiersz .wartosc").get(index - 1).text();
    }

    public Document getSnPPageDocument(String url) throws IOException {
        return client.getPageByUrl(getBaseUrl() + url);
    }

    public List<Semester> getSemesters() throws IOException {
        return getSemesters(getSnPPageDocument(GRADES_PAGE_URL));
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
