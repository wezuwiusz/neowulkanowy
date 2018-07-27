package io.github.wulkanowy.api.exams;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.api.SnP;
import io.github.wulkanowy.api.VulcanException;
import io.github.wulkanowy.api.generic.Week;

import static io.github.wulkanowy.api.DateTimeUtilsKt.getDateAsTick;
import static io.github.wulkanowy.api.DateTimeUtilsKt.getFormattedDate;

@Deprecated
public class ExamsWeek {

    private static final String EXAMS_PAGE_URL = "Sprawdziany.mvc/Terminarz?rodzajWidoku=2&data=";

    private final SnP snp;

    public ExamsWeek(SnP snp) {
        this.snp = snp;
    }

    public Week<ExamDay> getCurrent() throws IOException, VulcanException {
        return getWeek("", true);
    }

    public Week<ExamDay> getWeek(String date, final boolean onlyNotEmpty) throws IOException, VulcanException {
        Document examsPage = snp.getSnPPageDocument(EXAMS_PAGE_URL + getDateAsTick(date));
        Elements examsDays = examsPage.select(".mainContainer > div:not(.navigation)");

        List<ExamDay> days = new ArrayList<>();

        for (Element item : examsDays) {
            ExamDay day = new ExamDay();
            Element dayHeading = item.select("h2").first();

            if (null == dayHeading && onlyNotEmpty) {
                continue;
            }

            if (null != dayHeading) {
                String[] dateHeader = dayHeading.text().split(", ");
                day.setDayName(StringUtils.capitalize(dateHeader[0]));
                day.setDate(getFormattedDate(dateHeader[1]));
            }

            Elements exams = item.select("article");
            for (Element e : exams) {
                day.addExam(new Exam()
                        .setSubjectAndGroup(snp.getRowDataChildValue(e, 1))
                        .setType(snp.getRowDataChildValue(e, 2))
                        .setDescription(snp.getRowDataChildValue(e, 3))
                        .setTeacher(snp.getRowDataChildValue(e, 4).split(", ")[0])
                        .setEntryDate(getFormattedDate(snp.getRowDataChildValue(e, 4).split(", ")[1]))
                );
            }

            days.add(day);
        }


        return new Week<ExamDay>()
                .setStartDayDate(getFormattedDate(examsPage.select(".mainContainer > h2")
                        .first().text().split(" ")[1]))
                .setDays(days);
    }
}
