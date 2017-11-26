package io.github.wulkanowy.api.notes;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.api.StudentAndParent;

public class NotesList {

    private StudentAndParent snp = null;

    private List<Note> notes = new ArrayList<>();

    private static final String NOTES_PAGE_URL = "UwagiOsiagniecia.mvc/Wszystkie";

    public NotesList(StudentAndParent snp) {
        this.snp = snp;
    }

    public List<Note> getAllNotes() throws IOException {
        Element pageFragment = snp.getSnPPageDocument(NOTES_PAGE_URL)
                .select(".mainContainer > div").get(0);
        Elements items = pageFragment.select("article");
        Elements dates = pageFragment.select("h2");

        int index = 0;
        for (Element item : items) {
            notes.add(new Note()
                    .setDate(dates.get(index++).text())
                    .setTeacher(snp.getRowDataChildValue(item, 1))
                    .setCategory(snp.getRowDataChildValue(item, 2))
                    .setContent(snp.getRowDataChildValue(item, 3))
            );
        }

        return notes;
    }
}
