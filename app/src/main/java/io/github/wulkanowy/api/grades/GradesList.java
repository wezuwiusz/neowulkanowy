package io.github.wulkanowy.api.grades;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.wulkanowy.api.Cookies;
import io.github.wulkanowy.api.StudentAndParent;
import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.login.LoginErrorException;

public class GradesList extends Vulcan {

    private StudentAndParent snp = null;

    private String gradesPageUrl =
            "https://uonetplus-opiekun.vulcan.net.pl/{locationID}/{ID}/Oceny/Wszystkie?details=2";

    private List<Grade> grades = new ArrayList<>();

    public GradesList(Cookies cookies, StudentAndParent snp) {
        this.cookies = cookies;
        this.snp = snp;
    }

    public List<Grade> getAll() throws IOException, LoginErrorException {
        gradesPageUrl = gradesPageUrl.replace("{locationID}", snp.getLocationID());
        gradesPageUrl = gradesPageUrl.replace("{ID}", snp.getID());

        Document marksPage = Jsoup.connect(gradesPageUrl)
                .cookies(getCookies())
                .get();

        Elements marksRows = marksPage.select(".ocenySzczegoly-table > tbody > tr");

        for (Element row : marksRows) {
            Pattern pattern = Pattern.compile("#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})");
            Matcher matcher = pattern.matcher(
                    row.select("td:nth-child(2) span.ocenaCzastkowa").attr("style"));
            String color;
            if (!matcher.find()) {
                color = "000000";
            } else {
                color = matcher.group(1);
            }

            grades.add(new Grade()
                    .setSubject(row.select("td:nth-child(1)").text())
                    .setValue(row.select("td:nth-child(2)").text())
                    .setColor(color)
                    .setDescription(row.select("td:nth-child(3)").text())
                    .setWeight(row.select("td:nth-child(4)").text())
                    .setDate(row.select("td:nth-child(5)").text())
                    .setTeacher(row.select("td:nth-child(6)").text())
            );
        }

        return grades;
    }
}
