package io.github.wulkanowy.api.notes;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.api.FixtureHelper;
import io.github.wulkanowy.api.StudentAndParent;

public class NotesListTest {

    private String fixtureFilledFileName = "UwagiOsiagniecia-filled.html";

    private String fixtureEmptyFileName = "UwagiOsiagniecia-empty.html";

    private NotesList getSetUpNotesList(String fixtureFileName) throws Exception {
        String input = FixtureHelper.getAsString(getClass().getResourceAsStream(fixtureFileName));

        Document notesPageDocument = Jsoup.parse(input);

        Notes notes = Mockito.mock(Notes.class);
        Mockito.when(notes.getNotesPageDocument()).thenReturn(notesPageDocument);
        StudentAndParent snp = Mockito.mock(StudentAndParent.class);
        Mockito.when(snp.getRowDataChildValue(Mockito.any(Element.class),
                Mockito.anyInt())).thenCallRealMethod();

        return new NotesList(notes, snp);
    }

    @Test
    public void getAllNotesFilledTest() throws Exception {
        List<Note> expectedList = new ArrayList<>();
        expectedList.add(new Note()
                .setDate("06.06.2017")
                .setTeacher("Jan Kowalski [JK]")
                .setCategory("Zaangażowanie społeczne")
                .setContent("Pomoc przy pikniku charytatywnym")
        );
        expectedList.add(new Note()
                .setDate("01.12.2016")
                .setTeacher("Ochocka Zofia [PZ]")
                .setCategory("Reprezentowanie szkoły")
                .setContent("Udział w przygotowaniu spektaklu")
        );
        expectedList.add(new Note()
                .setDate("01.10.2016")
                .setTeacher("Kochański Leszek [KL]")
                .setCategory("Zachowanie na lekcji")
                .setContent("Przeszkadzanie w prowadzeniu lekcji")
        );

        List<Note> actualList = getSetUpNotesList(fixtureFilledFileName).getAllNotes();

        Assert.assertEquals(3, actualList.size());
        ReflectionAssert.assertReflectionEquals(expectedList, actualList);
    }

    @Test
    public void getAllNotesWhenEmpty() throws Exception {
        List<Note> actualList = getSetUpNotesList(fixtureEmptyFileName).getAllNotes();

        List<Note> expectedList = new ArrayList<>();

        Assert.assertEquals(0, actualList.size());
        ReflectionAssert.assertReflectionEquals(expectedList, actualList);
    }
}
