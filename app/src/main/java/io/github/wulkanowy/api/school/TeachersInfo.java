package io.github.wulkanowy.api.school;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.api.StudentAndParent;
import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.login.LoginErrorException;

public class TeachersInfo extends Vulcan {

    private StudentAndParent snp = null;

    private String schoolPageUrl = "Szkola.mvc/Nauczyciele";

    public TeachersInfo(StudentAndParent snp) {
        this.snp = snp;
    }

    public TeachersData getTeachersData() throws IOException, LoginErrorException {
        Document doc = snp.getSnPPageDocument(schoolPageUrl);
        Elements rows = doc.select(".mainContainer > table tbody tr");
        String description = doc.select(".mainContainer > p").first().text();

        List<Subject> subjects = new ArrayList<>();

        for (Element subject : rows) {
            subjects.add(new Subject()
                    .setName(subject.select("td").get(1).text())
                    .setTeachers(subject.select("td").get(2).text().split(", "))
            );
        }

        return new TeachersData()
                .setClassName(description.split(", ")[0].split(": ")[1].trim())
                .setClassTeacher(description.split("Wychowawcy:")[1].trim().split(", "))
                .setSubjects(subjects);
    }
}
