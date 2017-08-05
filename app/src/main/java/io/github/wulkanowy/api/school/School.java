package io.github.wulkanowy.api.school;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import io.github.wulkanowy.api.Cookies;
import io.github.wulkanowy.api.StudentAndParent;
import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.login.LoginErrorException;

public class School extends Vulcan {
    private StudentAndParent snp = null;

    private String schoolPageUrl = "https://uonetplus-opiekun.vulcan.net.pl/{locationID}/{ID}/"
            + "Szkola.mvc/Nauczyciele";

    public School(Cookies cookies, StudentAndParent snp) {
        this.cookies = cookies;
        this.snp = snp;
    }

    public Document getSchoolPageDocument() throws IOException, LoginErrorException {
        schoolPageUrl = schoolPageUrl.replace("{locationID}", snp.getLocationID());
        schoolPageUrl = schoolPageUrl.replace("{ID}", snp.getID());

        return Jsoup.connect(schoolPageUrl)
                .cookies(getCookies())
                .get();
    }
}
