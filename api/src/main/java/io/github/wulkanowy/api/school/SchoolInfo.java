package io.github.wulkanowy.api.school;

import org.jsoup.nodes.Element;

import java.io.IOException;

import io.github.wulkanowy.api.StudentAndParent;

public class SchoolInfo {

    private StudentAndParent snp = null;

    private static final String SCHOOL_PAGE_URL = "Szkola.mvc/Nauczyciele";

    public SchoolInfo(StudentAndParent snp) {
        this.snp = snp;
    }

    public SchoolData getSchoolData() throws IOException {
        Element e = snp.getSnPPageDocument(SCHOOL_PAGE_URL)
                .select(".mainContainer > article").get(0);

        return new SchoolData()
                .setName(snp.getRowDataChildValue(e, 1))
                .setAddress(snp.getRowDataChildValue(e, 2))
                .setPhoneNumber(snp.getRowDataChildValue(e, 3))
                .setHeadmaster(snp.getRowDataChildValue(e, 4))
                .setPedagogue(snp.getRowDataChildValue(e, 5).split(", "));
    }
}
