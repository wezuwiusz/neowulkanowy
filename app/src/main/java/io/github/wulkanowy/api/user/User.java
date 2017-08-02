package io.github.wulkanowy.api.user;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import io.github.wulkanowy.api.Cookies;
import io.github.wulkanowy.api.StudentAndParent;
import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.login.LoginErrorException;

public class User extends Vulcan {

    private StudentAndParent snp = null;

    private String studentDataPageUrl =
            "https://uonetplus-opiekun.vulcan.net.pl/{locationID}/{ID}/Uczen.mvc/DanePodstawowe";

    public User(Cookies cookies, StudentAndParent snp) {
        this.cookies = cookies;
        this.snp = snp;
    }

    public Document getPage() throws IOException, LoginErrorException {
        studentDataPageUrl = studentDataPageUrl.replace("{locationID}", snp.getLocationID());
        studentDataPageUrl = studentDataPageUrl.replace("{ID}", snp.getID());

        return Jsoup.connect(studentDataPageUrl)
                .cookies(getCookies())
                .get();
    }
}
