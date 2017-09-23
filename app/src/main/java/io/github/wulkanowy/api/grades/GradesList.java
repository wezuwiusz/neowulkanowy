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
import io.github.wulkanowy.api.StudentAndParent;
import io.github.wulkanowy.api.login.LoginErrorException;

public class GradesList {

    private StudentAndParent snp = null;

    private String gradesPageUrl = "Oceny/Wszystkie?details=2&okres=";

    private List<Grade> grades = new ArrayList<>();

    public GradesList(StudentAndParent snp) {
        this.snp = snp;
    }

    public String getGradesPageUrl() {
        return gradesPageUrl;
    }

    public List<Grade> getAll() throws IOException, LoginErrorException, ParseException {
        return getAll("");
    }

    public List<Grade> getAll(String semester) throws IOException, LoginErrorException, ParseException {
        Document gradesPage = snp.getSnPPageDocument(getGradesPageUrl() + semester);
        Elements gradesRows = gradesPage.select(".ocenySzczegoly-table > tbody > tr");
        Semester currentSemester = snp.getCurrentSemester(snp.getSemesters(gradesPage));

        for (Element row : gradesRows) {
            if ("Brak ocen".equals(row.select("td:nth-child(2)").text())) {
                continue;
            }

            String descriptions = row.select("td:nth-child(3)").text();
            String symbol = descriptions.split(", ")[0];
            String description = descriptions.replaceFirst(symbol, "").replaceFirst(", ", "");

            Pattern pattern = Pattern.compile("#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})");
            Matcher matcher = pattern.matcher(row.select("td:nth-child(2) span.ocenaCzastkowa")
                    .attr("style"));

            String color = "";
            while (matcher.find()) {
                color = matcher.group(1);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.ROOT);
            Date d = sdf.parse(row.select("td:nth-child(5)").text());
            sdf.applyPattern("yyyy-MM-dd");

            grades.add(new Grade()
                    .setSubject(row.select("td:nth-child(1)").text())
                    .setValue(row.select("td:nth-child(2)").text())
                    .setColor(color)
                    .setSymbol(symbol)
                    .setDescription(description)
                    .setWeight(row.select("td:nth-child(4)").text())
                    .setDate(sdf.format(d))
                    .setTeacher(row.select("td:nth-child(6)").text())
                    .setSemester(currentSemester.getNumber())
            );
        }

        return grades;
    }
}
