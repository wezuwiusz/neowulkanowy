package io.github.wulkanowy.activity.main;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

class CheckPass {

    private Document document;

    CheckPass(Document doc) {
        document = doc;
    }

    boolean isLogged() {
        Element messageAlert = document.select(".ErrorMessage").first();

        return null == messageAlert;
    }
}
