package io.github.wulkanowy.api.grades;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.wulkanowy.api.SnP;
import io.github.wulkanowy.api.VulcanException;

import static io.github.wulkanowy.api.DateTimeUtilsKt.getFormattedDate;

@Deprecated
public class GradesList {

    private static final String GRADES_PAGE_URL = "Oceny/Wszystkie?details=2&okres=";

    private SnP snp;

    public GradesList(SnP snp) {
        this.snp = snp;
    }

    public List<Grade> getAll(String semester) throws IOException, VulcanException {
        Document gradesPage = snp.getSnPPageDocument(GRADES_PAGE_URL + semester);
        Elements gradesRows = gradesPage.select(".ocenySzczegoly-table > tbody > tr");

        List<Grade> grades = new ArrayList<>();

        for (Element row : gradesRows) {
            if ("Brak ocen".equals(row.select("td:nth-child(2)").text())) {
                continue;
            }

            grades.add(getGrade(row));
        }

        return grades;
    }

    private Grade getGrade(Element row) {
        String descriptions = row.select("td:nth-child(3)").text();

        String symbol = descriptions.split(", ")[0];
        String description = descriptions.replaceFirst(Pattern.quote(symbol), "").replaceFirst(", ", "");
        String color = getColor(row.select("td:nth-child(2) span.ocenaCzastkowa").attr("style"));
        String date = getFormattedDate(row.select("td:nth-child(5)").text());

        return new Grade()
                .setSubject(row.select("td:nth-child(1)").text())
                .setValue(row.select("td:nth-child(2)").text())
                .setColor(color)
                .setSymbol(symbol)
                .setDescription(description)
                .setWeight(row.select("td:nth-child(4)").text())
                .setDate(date)
                .setTeacher(row.select("td:nth-child(6)").text());
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
}
