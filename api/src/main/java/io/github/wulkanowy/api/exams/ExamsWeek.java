package io.github.wulkanowy.api.exams;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.api.SnP;

public class ExamsWeek {

    private static final String EXAMS_PAGE_URL = "Sprawdziany.mvc/Terminarz?rodzajWidoku=2&data=";

    private final SnP snp;

    public ExamsWeek(SnP snp) {
        this.snp = snp;
    }

    public Week getCurrent() throws IOException {
        return getWeek("", true);
    }

    public Week getWeek(String tick, final boolean onlyNotEmpty) throws IOException {
        Document examsPage = snp.getSnPPageDocument(EXAMS_PAGE_URL + tick);
        Elements examsDays = examsPage.select(".mainContainer > div:not(.navigation)");

        List<Day> days = new ArrayList<>();

        for (Element item : examsDays) {
            Day day = new Day();
            Element dayHeading = item.select("h2").first();

            if (null == dayHeading && onlyNotEmpty) {
                continue;
            }

            if (null != dayHeading) {
                day = new Day().setDate(dayHeading.text().split(", ")[1]);
            }

            Elements exams = item.select("article");
            for (Element e : exams) {
                day.addExam(new Exam()
                        .setSubjectAndGroup(snp.getRowDataChildValue(e, 1))
                        .setType(snp.getRowDataChildValue(e, 2))
                        .setDescription(snp.getRowDataChildValue(e, 3))
                        .setTeacher(snp.getRowDataChildValue(e, 4).split(", ")[0])
                        .setEntryDate(snp.getRowDataChildValue(e, 4).split(", ")[1])
                );
            }

            days.add(day);
        }

        Week week = new Week();
        week.setStartDate(examsDays.select("h2").first().text().split(" ")[1]);
        week.setDayList(days);

        return week;
    }
}
