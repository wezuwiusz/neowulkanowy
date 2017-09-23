package io.github.wulkanowy.api;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.api.login.NotLoggedInErrorException;

public class StudentAndParent extends Api {

    private String startPageUrl = "https://uonetplus.vulcan.net.pl/{symbol}/Start.mvc/Index";

    private String baseUrl = "https://uonetplus-opiekun.vulcan.net.pl/{symbol}/{ID}/";

    private String gradesPageUrl = baseUrl + "Oceny/Wszystkie";

    private String symbol;

    private String id;

    public StudentAndParent(Cookies cookies, String symbol) {
        this.cookies = cookies;
        this.symbol = symbol;
    }

    public StudentAndParent(Cookies cookies, String symbol, String id) {
        this(cookies, symbol);
        this.id = id;
    }

    public String getGradesPageUrl() {
        return gradesPageUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getStartPageUrl() {
        return startPageUrl;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getId() {
        return id;
    }

    public void storeContextCookies() throws IOException, NotLoggedInErrorException {
        //get context cookie
        Connection.Response res = Jsoup.connect(getSnpPageUrl())
                .followRedirects(true)
                .cookies(getCookies())
                .execute();

        cookies.addItems(res.cookies());
    }

    public String getSnpPageUrl() throws IOException, NotLoggedInErrorException {
        if (null != getId()) {
            return getBaseUrl().replace("{symbol}", getSymbol()).replace("{ID}", getId());
        }

        // get url to uonetplus-opiekun.vulcan.net.pl
        Document startPage = getPageByUrl(getStartPageUrl().replace("{symbol}", getSymbol()));
        Element studentTileLink = startPage.select(".panel.linkownia.pracownik.klient > a").first();

        if (null == studentTileLink) {
            throw new NotLoggedInErrorException();
        }

        String snpPageUrl = studentTileLink.attr("href");

        this.id = getExtractedIdFromUrl(snpPageUrl);

        return snpPageUrl;
    }

    public String getExtractedIdFromUrl(String snpPageUrl) throws NotLoggedInErrorException {
        String[] path = snpPageUrl.split("vulcan.net.pl/")[1].split("/");

        if (4 != path.length) {
            throw new NotLoggedInErrorException();
        }

        return path[1];
    }

    public String getRowDataChildValue(Element e, int index) {
        return e.select(".daneWiersz .wartosc").get(index - 1).text();
    }

    public Document getSnPPageDocument(String url) throws IOException {
        return getPageByUrl(getBaseUrl()
                .replace("{symbol}", getSymbol())
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
