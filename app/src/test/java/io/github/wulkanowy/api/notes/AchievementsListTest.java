package io.github.wulkanowy.api.notes;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.api.FixtureHelper;

public class AchievementsListTest {

    private String fixtureFilledFileName = "UwagiOsiagniecia-filled.html";

    private String fixtureEmptyFileName = "UwagiOsiagniecia-empty.html";

    private AchievementsList getSetUpAchievementsList(String fixtureFileName) throws Exception {
        String input = FixtureHelper.getAsString(getClass().getResourceAsStream(fixtureFileName));

        Document notesPageDocument = Jsoup.parse(input);

        Notes notes = Mockito.mock(Notes.class);
        Mockito.when(notes.getNotesPageDocument()).thenReturn(notesPageDocument);

        return new AchievementsList(notes);
    }

    @Test
    public void getAllAchievementsFilledTest() throws Exception {
        List<String> expectedList = new ArrayList<>();
        expectedList.add("I miejsce w ogólnopolskim konkursie ortograficznym");
        expectedList.add("III miejsce w ogólnopolskim konkursie plastycznym");

        List<String> actualList = getSetUpAchievementsList(
                fixtureFilledFileName).getAllAchievements();

        Assert.assertEquals(2, actualList.size());
        ReflectionAssert.assertReflectionEquals(expectedList, actualList);
    }

    @Test
    public void getAllAchievementsEmptyTest() throws Exception {
        List<String> expectedList = new ArrayList<>();

        List<String> actualList = getSetUpAchievementsList(
                fixtureEmptyFileName).getAllAchievements();

        Assert.assertEquals(0, actualList.size());
        ReflectionAssert.assertReflectionEquals(expectedList, actualList);
    }
}
