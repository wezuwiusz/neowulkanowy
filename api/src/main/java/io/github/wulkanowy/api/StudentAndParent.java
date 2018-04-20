package io.github.wulkanowy.api;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentAndParent implements SnP {

    private static final String START_PAGE_URL = "{schema}://uonetplus.{host}/{symbol}/Start.mvc/Index";

    private static final String BASE_URL = "{schema}://uonetplus-opiekun.{host}/{symbol}/{ID}/";

    private static final String GRADES_PAGE_URL = "Oceny/Wszystkie";

    private Client client;

    private String schoolID;

    private String studentID;

    private String diaryID;

    StudentAndParent(Client client, String schoolID, String studentID, String diaryID) {
        this.client = client;
        this.schoolID = schoolID;
        this.studentID = studentID;
        this.diaryID = diaryID;
    }

    public StudentAndParent setUp() throws IOException, VulcanException {
        if (null == getStudentID() || "".equals(getStudentID())) {
            Document doc = client.getPageByUrl(getSnpHomePageUrl());

            Student student = getCurrent(getStudents(doc));
            studentID = student.getId();

            Diary diary = getCurrent(getDiaries(doc));
            diaryID = diary.getId();
        }

        return this;
    }

    public String getSchoolID() {
        return schoolID;
    }

    public String getStudentID() {
        return studentID;
    }

    private String getBaseUrl() {
        return BASE_URL.replace("{ID}", getSchoolID());
    }

    String getSnpHomePageUrl() throws IOException, VulcanException {
        if (null != getSchoolID()) {
            return getBaseUrl();
        }

        // get url to uonetplus-opiekun.vulcan.net.pl
        Document startPage = client.getPageByUrl(START_PAGE_URL);
        Element studentTileLink = startPage.select(".panel.linkownia.pracownik.klient > a").first();

        if (null == studentTileLink) {
            throw new NotLoggedInErrorException("You are probably not logged in. Force login");
        }

        String snpPageUrl = studentTileLink.attr("href");

        this.schoolID = getExtractedIdFromUrl(snpPageUrl);

        return snpPageUrl;
    }

    String getExtractedIdFromUrl(String snpPageUrl) throws NotLoggedInErrorException {
        String[] path = snpPageUrl.split(client.getHost())[1].split("/");

        if (5 != path.length) {
            throw new NotLoggedInErrorException("You are probably not logged in");
        }

        return path[2];
    }

    public String getRowDataChildValue(Element e, int index) {
        return e.select(".daneWiersz .wartosc").get(index - 1).text();
    }

    public void setDiaryID(String id) {
        this.diaryID = id;
    }

    public Document getSnPPageDocument(String url) throws IOException, VulcanException {
        Map<String, String> cookies = new HashMap<>();
        cookies.put("idBiezacyDziennik", diaryID);
        cookies.put("idBiezacyUczen", studentID);
        client.addCookies(cookies);

        Document doc = client.getPageByUrl(getBaseUrl() + url, true, cookies);

        if ("Witryna ucznia i rodzica – Strona główna".equals(doc.select("title").first().text())) {
            throw new VulcanException("Sesja została nieprawidłowo zainicjowana");
        }

        return doc;
    }

    public List<Diary> getDiaries() throws IOException, VulcanException {
        return getDiaries(client.getPageByUrl(getBaseUrl()));
    }

    private List<Diary> getDiaries(Document doc) throws IOException, VulcanException {
        return getList(doc.select("#dziennikDropDownList option"), Diary.class);
    }

    public List<Student> getStudents() throws IOException, VulcanException {
        return getStudents(client.getPageByUrl(getBaseUrl()));
    }

    private List<Student> getStudents(Document doc) throws IOException, VulcanException {
        return getList(doc.select("#uczenDropDownList option"), Student.class);
    }

    public List<Semester> getSemesters() throws IOException, VulcanException {
        return getSemesters(getSnPPageDocument(GRADES_PAGE_URL));
    }

    public List<Semester> getSemesters(Document gradesPage) {
        Elements semesterOptions = gradesPage.select("#okresyKlasyfikacyjneDropDownList option");

        List<Semester> semesters = new ArrayList<>();

        for (Element e : semesterOptions) {
            Semester semester = new Semester()
                    .setId(e.text())
                    .setName(e.attr("value"));

            if (isCurrent(e)) {
                semester.setCurrent(true);
            }

            semesters.add(semester);
        }

        return semesters;
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> getList(Elements options, Class<? extends ParamItem> type) throws IOException, VulcanException {
        List<T> list = new ArrayList<>();

        for (Element e : options) {
            URL url = new URL(e.val());
            try {
                ParamItem item = type.newInstance()
                        .setId(url.getQuery().split("=")[1])
                        .setName(e.text());

                if (isCurrent(e)) {
                    item.setCurrent(true);
                }
                if (item instanceof Diary) {
                    item.setStudentId(getStudentID());
                }

                list.add((T) item);
            } catch (Exception ex) {
                throw new VulcanException("Error while trying to parse params list", ex);
            }
        }

        return list;
    }

    @SuppressWarnings("unchecked")
    public <T> T getCurrent(List<? extends ParamItem> list) {
        ParamItem current = null;
        for (ParamItem s : list) {
            if (s.isCurrent()) {
                current = s;
                break;
            }
        }

        return (T) current;
    }

    private boolean isCurrent(Element e) {
        return "selected".equals(e.attr("selected"));
    }
}
