package io.github.wulkanowy.api.grades;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.api.StudentAndParent;
import io.github.wulkanowy.api.login.LoginErrorException;

public class SubjectsList {

    private StudentAndParent snp = null;

    private String subjectsPageUrl = "Oceny/Wszystkie?details=1";

    public SubjectsList(StudentAndParent snp) {
        this.snp = snp;
    }

    public List<Subject> getAll() throws IOException, LoginErrorException {
        Document subjectPage = snp.getSnPPageDocument(subjectsPageUrl);

        Elements rows = subjectPage.select(".ocenyZwykle-table > tbody > tr");

        List<Subject> subjects = new ArrayList<>();

        for (Element subjectRow : rows) {
            subjects.add(new Subject()
                    .setName(subjectRow.select("td:nth-child(1)").text())
                    .setPredictedRating(subjectRow.select("td:nth-last-child(2)").text())
                    .setFinalRating(subjectRow.select("td:nth-last-child(1)").text())
            );
        }

        return subjects;
    }
}
