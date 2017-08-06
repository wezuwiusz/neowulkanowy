package io.github.wulkanowy.api.grades;

import org.jsoup.nodes.Document;

import java.io.IOException;

import io.github.wulkanowy.api.Cookies;
import io.github.wulkanowy.api.StudentAndParent;
import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.login.LoginErrorException;

public class Grades extends Vulcan {

    private StudentAndParent snp = null;

    public Grades(Cookies cookies, StudentAndParent snp) {
        this.cookies = cookies;
        this.snp = snp;
    }

    //TODO: move to snp
    public Document getGradesPageDocument(String url) throws IOException, LoginErrorException {
        return getPageByUrl(url
                .replace("{locationID}", snp.getLocationID())
                .replace("{ID}", snp.getID())
        );
    }
}
