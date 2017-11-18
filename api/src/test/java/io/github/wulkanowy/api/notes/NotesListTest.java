package io.github.wulkanowy.api.notes;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import io.github.wulkanowy.api.StudentAndParentTestCase;

public class NotesListTest extends StudentAndParentTestCase {

    private NotesList filled;

    private NotesList empty;

    @Before
    public void setUp() throws Exception {
        filled = new NotesList(getSnp("UwagiOsiagniecia-filled.html"));
        empty = new NotesList(getSnp("UwagiOsiagniecia-empty.html"));
    }

    @Test
    public void getAllNotesTest() throws Exception {
        Assert.assertEquals(3, filled.getAllNotes().size());
        Assert.assertEquals(0, empty.getAllNotes().size());
    }

    @Test
    public void getDateTest() throws Exception {
        List<Note> filledList = filled.getAllNotes();

        Assert.assertEquals("06.06.2017", filledList.get(0).getDate());
        Assert.assertEquals("01.10.2016", filledList.get(2).getDate());
    }

    @Test
    public void getTeacherTest() throws Exception {
        List<Note> filledList = filled.getAllNotes();

        Assert.assertEquals("Jan Kowalski [JK]", filledList.get(0).getTeacher());
        Assert.assertEquals("Kochański Leszek [KL]", filledList.get(2).getTeacher());
    }

    @Test
    public void getCategoryTest() throws Exception {
        List<Note> filledList = filled.getAllNotes();

        Assert.assertEquals("Zaangażowanie społeczne", filledList.get(0).getCategory());
        Assert.assertEquals("Zachowanie na lekcji", filledList.get(2).getCategory());
    }

    @Test
    public void getContentTest() throws Exception {
        List<Note> filledList = filled.getAllNotes();

        Assert.assertEquals("Pomoc przy pikniku charytatywnym", filledList.get(0).getContent());
        Assert.assertEquals("Przeszkadzanie w prowadzeniu lekcji", filledList.get(2).getContent());
    }
}
