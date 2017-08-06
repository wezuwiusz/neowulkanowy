package io.github.wulkanowy.api.grades;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.wulkanowy.api.Semester;
import io.github.wulkanowy.api.StudentAndParent;
import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.login.LoginErrorException;

public class GradesList extends Vulcan {

    private Grades grades = null;
    private StudentAndParent snp = null;

    private String gradesPageUrl = "https://uonetplus-opiekun.vulcan.net.pl/{locationID}/{ID}"
            + "/Oceny/Wszystkie?details=2&okres=";

    private List<Grade> gradesList = new ArrayList<>();

    public GradesList(Grades grades, StudentAndParent snp) {
        this.grades = grades;
        this.snp = snp;
    }

    public String getGradesPageUrl() {
        return gradesPageUrl;
    }

    public List<Grade> getAll() throws IOException, LoginErrorException {
        return getAll("");
    }

    public List<Grade> getAll(String semester) throws IOException, LoginErrorException {
        Document gradesPage = grades.getGradesPageDocument(getGradesPageUrl() + semester);
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
            String color = matcher.find() ? matcher.group(1) : "";

            gradesList.add(new Grade()
                    .setSubject(row.select("td:nth-child(1)").text())
                    .setValue(row.select("td:nth-child(2)").text())
                    .setColor(color)
                    .setSymbol(symbol)
                    .setDescription(description)
                    .setWeight(row.select("td:nth-child(4)").text())
                    .setDate(row.select("td:nth-child(5)").text())
                    .setTeacher(row.select("td:nth-child(6)").text())
                    .setSemester(currentSemester.getNumber())
            );
        }

        return gradesList;
    }
}
