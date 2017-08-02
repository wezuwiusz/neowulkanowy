package io.github.wulkanowy.api.notes;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import io.github.wulkanowy.api.Cookies;
import io.github.wulkanowy.api.StudentAndParent;
import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.login.LoginErrorException;

public class Notes extends Vulcan {

    private StudentAndParent snp = null;

    private String notesPageUrl =
            "https://uonetplus-opiekun.vulcan.net.pl/{locationID}/{ID}/UwagiOsiagniecia.mvc/Wszystkie";

    public Notes(Cookies cookies, StudentAndParent snp) {
        this.cookies = cookies;
        this.snp = snp;
    }

    public Document getNotesPageDocument() throws IOException, LoginErrorException {
        notesPageUrl = notesPageUrl.replace("{locationID}", snp.getLocationID());
        notesPageUrl = notesPageUrl.replace("{ID}", snp.getID());

        return Jsoup.connect(notesPageUrl)
                .cookies(getCookies())
                .get();
    }
}
