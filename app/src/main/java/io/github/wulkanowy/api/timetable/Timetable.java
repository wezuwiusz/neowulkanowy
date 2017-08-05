package io.github.wulkanowy.api.timetable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import io.github.wulkanowy.api.Cookies;
import io.github.wulkanowy.api.StudentAndParent;
import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.login.LoginErrorException;

public class Timetable extends Vulcan {

    private StudentAndParent snp;

    private String timetablePageurl =
            "https://uonetplus-opiekun.vulcan.net.pl/{locationID}/{ID}/Lekcja.mvc/PlanLekcji?data=";

    public Timetable(Cookies cookies, StudentAndParent snp) {
        this.cookies = cookies;
        this.snp = snp;
    }

    public Document getTablePageDocument(String tick) throws IOException, LoginErrorException {
        timetablePageurl = timetablePageurl.replace("{locationID}", snp.getLocationID());
        timetablePageurl = timetablePageurl.replace("{ID}", snp.getID());

        return Jsoup.connect(timetablePageurl + tick)
                .cookies(getCookies())
                .get();
    }
}
