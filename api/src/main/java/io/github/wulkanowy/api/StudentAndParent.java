package io.github.wulkanowy.api;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.wulkanowy.api.generic.Diary;
import io.github.wulkanowy.api.generic.ParamItem;
import io.github.wulkanowy.api.generic.Semester;
import io.github.wulkanowy.api.generic.Student;

public class StudentAndParent implements SnP {

    private static final String BASE_URL = "{schema}://uonetplus-opiekun.{host}/{symbol}/{ID}/";

    private static final String GRADES_PAGE_URL = "Oceny/Wszystkie";

    private Client client;

    private String studentID;

    private String diaryID;

    private static final Logger logger = LoggerFactory.getLogger(StudentAndParent.class);

    StudentAndParent(Client client, String studentID, String diaryID) {
        this.client = client;
        this.studentID = studentID;
        this.diaryID = diaryID;
    }

    public StudentAndParent setUp() throws IOException, VulcanException {
        if (null == getStudentID() || "".equals(getStudentID())) {
            Document doc = client.getPageByUrl(BASE_URL);

            if (doc.select("#idSection").isEmpty()) {
                logger.error("Expected SnP page, got page with title: {} {}", doc.title(), doc.selectFirst("body"));
                throw new VulcanException("Nieznany błąd podczas pobierania danych. Strona: " + doc.title());
            }

            Student student = getCurrent(getStudents(doc));
            studentID = student.getId();

            Diary diary = getCurrent(getDiaries(doc));
            diaryID = diary.getId();
        }

        return this;
    }

    public String getStudentID() {
        return studentID;
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

        Document doc = client.getPageByUrl(BASE_URL + url, true, cookies);

        if (!doc.title().startsWith("Witryna ucznia i rodzica")) {
            logger.error("Expected SnP page, got page with title: {} {}", doc.title(), doc.selectFirst("body"));
            throw new VulcanException("Nieznany błąd podczas pobierania danych. Strona: " + doc.title());
        }

        if (doc.title().endsWith("Strona główna")) {
            throw new VulcanException("Sesja została nieprawidłowo zainicjowana");
        }

        return doc;
    }

    public List<Diary> getDiaries() throws IOException, VulcanException {
        return getDiaries(client.getPageByUrl(BASE_URL));
    }

    private List<Diary> getDiaries(Document doc) throws IOException, VulcanException {
        return getList(doc.select("#dziennikDropDownList option"), Diary.class);
    }

    public List<Student> getStudents() throws IOException, VulcanException {
        return getStudents(client.getPageByUrl(BASE_URL));
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
                    .setId(e.attr("value"))
                    .setName(e.text());

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
