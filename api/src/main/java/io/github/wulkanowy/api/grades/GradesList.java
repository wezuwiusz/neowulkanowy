package io.github.wulkanowy.api.grades;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.wulkanowy.api.Semester;
import io.github.wulkanowy.api.SnP;
import io.github.wulkanowy.api.VulcanException;

public class GradesList {

    private static final String GRADES_PAGE_URL = "Oceny/Wszystkie?details=2&okres=";

    private SnP snp;

    private List<Grade> grades = new ArrayList<>();

    public GradesList(SnP snp) {
        this.snp = snp;
    }

    private String getGradesPageUrl() {
        return GRADES_PAGE_URL;
    }

    public List<Grade> getAll() throws IOException, ParseException, VulcanException {
        return getAll("");
    }

    public List<Grade> getAll(String semester) throws IOException, ParseException, VulcanException {
        Document gradesPage = snp.getSnPPageDocument(getGradesPageUrl() + semester);
        Elements gradesRows = gradesPage.select(".ocenySzczegoly-table > tbody > tr");

        if ("".equals(semester)) {
            List<Semester> semesterList = snp.getSemesters(gradesPage);
            Semester currentSemester = snp.getCurrent(semesterList);
            semester = currentSemester.getName();
        }

        for (Element row : gradesRows) {
            if ("Brak ocen".equals(row.select("td:nth-child(2)").text())) {
                continue;
            }

            grades.add(getGrade(row, semester));
        }

        return grades;
    }

    private Grade getGrade(Element row, String semester) throws ParseException {
        String descriptions = row.select("td:nth-child(3)").text();

        String symbol = descriptions.split(", ")[0];
        String description = descriptions.replaceFirst(symbol, "").replaceFirst(", ", "");
        String color = getColor(row.select("td:nth-child(2) span.ocenaCzastkowa").attr("style"));
        String date = formatDate(row.select("td:nth-child(5)").text());

        return new Grade()
                .setSubject(row.select("td:nth-child(1)").text())
                .setValue(row.select("td:nth-child(2)").text())
                .setColor(color)
                .setSymbol(symbol)
                .setDescription(description)
                .setWeight(row.select("td:nth-child(4)").text())
                .setDate(date)
                .setTeacher(row.select("td:nth-child(6)").text())
                .setSemester(semester);
    }

    private String getColor(String styleAttr) {
        Pattern pattern = Pattern.compile("#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})");
        Matcher matcher = pattern.matcher(styleAttr);

        String color = "";
        while (matcher.find()) {
            color = matcher.group(1);
        }

        return color;
    }

    private String formatDate(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.ROOT);
        Date d = sdf.parse(date);
        sdf.applyPattern("yyyy-MM-dd");

        return sdf.format(d);
    }
}
