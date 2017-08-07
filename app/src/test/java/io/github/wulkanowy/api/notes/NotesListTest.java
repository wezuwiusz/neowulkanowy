package io.github.wulkanowy.api.notes;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import io.github.wulkanowy.api.FixtureHelper;
import io.github.wulkanowy.api.StudentAndParent;

public class NotesListTest {

    private String fixtureFilledFileName = "UwagiOsiagniecia-filled.html";

    private String fixtureEmptyFileName = "UwagiOsiagniecia-empty.html";

    private NotesList getSetUpNotesList(String fixtureFileName) throws Exception {
        String input = FixtureHelper.getAsString(getClass().getResourceAsStream(fixtureFileName));

        Document notesPageDocument = Jsoup.parse(input);

        StudentAndParent snp = Mockito.mock(StudentAndParent.class);
        Mockito.when(snp.getSnPPageDocument(Mockito.anyString())).thenReturn(notesPageDocument);
        Mockito.when(snp.getRowDataChildValue(Mockito.any(Element.class),
                Mockito.anyInt())).thenCallRealMethod();

        return new NotesList(snp);
    }

    @Test
    public void getAllNotesFilledTest() throws Exception {
        List<Note> list = getSetUpNotesList(fixtureFilledFileName).getAllNotes();

        Assert.assertEquals(3, list.size());

        Assert.assertEquals("06.06.2017", list.get(0).getDate());
        Assert.assertEquals("Jan Kowalski [JK]", list.get(0).getTeacher());
        Assert.assertEquals("Zaangażowanie społeczne", list.get(0).getCategory());
        Assert.assertEquals("Pomoc przy pikniku charytatywnym", list.get(0).getContent());

        Assert.assertEquals("01.10.2016", list.get(2).getDate());
        Assert.assertEquals("Kochański Leszek [KL]", list.get(2).getTeacher());
        Assert.assertEquals("Zachowanie na lekcji", list.get(2).getCategory());
        Assert.assertEquals("Przeszkadzanie w prowadzeniu lekcji", list.get(2).getContent());
    }

    @Test
    public void getAllNotesWhenEmpty() throws Exception {
        List<Note> list = getSetUpNotesList(fixtureEmptyFileName).getAllNotes();

        Assert.assertEquals(0, list.size());
    }
}
