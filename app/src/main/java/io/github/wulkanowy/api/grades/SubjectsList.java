package io.github.wulkanowy.api.grades;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.api.Cookies;
import io.github.wulkanowy.api.StudentAndParent;
import io.github.wulkanowy.api.login.LoginErrorException;

public class SubjectsList extends StudentAndParent {

    private String subjectsPageUrl =
            "https://uonetplus-opiekun.vulcan.net.pl/{locationID}/{ID}/Oceny/Wszystkie?details=1";

    private List<Subject> subjects = new ArrayList<>();

    public SubjectsList(Cookies cookies, String locationID) throws IOException {
        super(cookies, locationID);
    }

    public List<Subject> getAll() throws IOException, LoginErrorException {
        subjectsPageUrl = subjectsPageUrl.replace("{locationID}", getLocationID());
        subjectsPageUrl = subjectsPageUrl.replace("{ID}", getID());

        Document subjectPage = Jsoup.connect(subjectsPageUrl)
                .cookies(getJar())
                .get();

        Elements rows = subjectPage.select(".ocenyZwykle-table > tbody > tr");

        for (Element subjectRow : rows) {
            subjects.add(new Subject()
                    .setName(subjectRow.select("td:nth-child(1)").text())
                    .setPredictedRating(subjectRow.select("td:nth-child(3)").text())
                    .setFinalRating(subjectRow.select("td:nth-child(4)").text())
            );
        }

        return subjects;
    }
}
