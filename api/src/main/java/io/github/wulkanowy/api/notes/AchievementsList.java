package io.github.wulkanowy.api.notes;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.api.SnP;
import io.github.wulkanowy.api.VulcanException;

public class AchievementsList {

    private static final String NOTES_PAGE_URL = "UwagiOsiagniecia.mvc/Wszystkie";

    private SnP snp = null;

    private List<String> achievements = new ArrayList<>();

    public AchievementsList(SnP snp) {
        this.snp = snp;
    }

    public List<String> getAllAchievements() throws IOException, VulcanException {
        Element pageFragment = snp.getSnPPageDocument(NOTES_PAGE_URL)
                .select(".mainContainer > div").get(1);
        Elements items = pageFragment.select("article");

        for (Element item : items) {
            achievements.add(item.text());
        }

        return achievements;
    }
}
