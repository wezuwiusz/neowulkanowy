package io.github.wulkanowy.api.school;

import org.jsoup.nodes.Element;

import java.io.IOException;

import io.github.wulkanowy.api.StudentAndParent;
import io.github.wulkanowy.api.login.LoginErrorException;

public class SchoolInfo {

    private StudentAndParent snp = null;

    private String schoolPageUrl = "Szkola.mvc/Nauczyciele";

    public SchoolInfo(StudentAndParent snp) {
        this.snp = snp;
    }

    public SchoolData getSchoolData() throws IOException, LoginErrorException {
        Element e = snp.getSnPPageDocument(schoolPageUrl)
                .select(".mainContainer > article").get(0);

        return new SchoolData()
                .setName(snp.getRowDataChildValue(e, 1))
                .setAddress(snp.getRowDataChildValue(e, 2))
                .setPhoneNumber(snp.getRowDataChildValue(e, 3))
                .setHeadmaster(snp.getRowDataChildValue(e, 4))
                .setPedagogue(snp.getRowDataChildValue(e, 5).split(", "));
    }
}
