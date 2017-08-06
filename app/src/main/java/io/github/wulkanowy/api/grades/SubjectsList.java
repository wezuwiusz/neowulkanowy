package io.github.wulkanowy.api.grades;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.api.Cookies;
import io.github.wulkanowy.api.StudentAndParent;
import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.login.LoginErrorException;

public class SubjectsList extends Vulcan {

    private StudentAndParent snp = null;

    private String subjectsPageUrl =
            "https://uonetplus-opiekun.vulcan.net.pl/{locationID}/{ID}/Oceny/Wszystkie?details=1";

    private List<Subject> subjects = new ArrayList<>();

    public SubjectsList(Cookies cookies, StudentAndParent snp) {
        this.cookies = cookies;
        this.snp = snp;
    }

    public List<Subject> getAll() throws IOException, LoginErrorException {
        subjectsPageUrl = subjectsPageUrl.replace("{locationID}", snp.getLocationID());
        subjectsPageUrl = subjectsPageUrl.replace("{ID}", snp.getID());

        Document subjectPage = getPageByUrl(subjectsPageUrl);

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
