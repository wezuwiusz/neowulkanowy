package io.github.wulkanowy.api.notes;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.api.login.LoginErrorException;

public class AchievementsList {

    private Notes notes = null;

    private List<String> achievementsList = new ArrayList<>();

    public AchievementsList(Notes notes) {
        this.notes = notes;
    }

    public List<String> getAllAchievements() throws LoginErrorException, IOException {
        Element pageFragment = notes.getNotesPageDocument().select(".mainContainer > div").get(1);
        Elements items = pageFragment.select("article");

        for (Element item : items) {
            achievementsList.add(item.text());
        }

        return achievementsList;
    }
}
