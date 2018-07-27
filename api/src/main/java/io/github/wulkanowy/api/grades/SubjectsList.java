package io.github.wulkanowy.api.grades;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.api.SnP;
import io.github.wulkanowy.api.VulcanException;

@Deprecated
public class SubjectsList {

    private static final String SUBJECTS_PAGE_URL = "Oceny/Wszystkie?details=1&okres=";

    private SnP snp;

    public SubjectsList(SnP snp) {
        this.snp = snp;
    }


    public List<Subject> getAll() throws IOException, VulcanException {
        return getAll("");
    }

    public List<Subject> getAll(String semester) throws IOException, VulcanException {
        Document subjectPage = snp.getSnPPageDocument(SUBJECTS_PAGE_URL + semester);

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
