package io.github.wulkanowy.api.notes;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import io.github.wulkanowy.api.FixtureHelper;
import io.github.wulkanowy.api.StudentAndParent;

public class AchievementsListTest {

    private String fixtureFilledFileName = "UwagiOsiagniecia-filled.html";

    private String fixtureEmptyFileName = "UwagiOsiagniecia-empty.html";

    private AchievementsList getSetUpAchievementsList(String fixtureFileName) throws Exception {
        String input = FixtureHelper.getAsString(getClass().getResourceAsStream(fixtureFileName));

        Document notesPageDocument = Jsoup.parse(input);

        StudentAndParent snp = Mockito.mock(StudentAndParent.class);
        Mockito.when(snp.getSnPPageDocument(Mockito.anyString())).thenReturn(notesPageDocument);

        return new AchievementsList(snp);
    }

    @Test
    public void getAllAchievementsFilledTest() throws Exception {
        List<String> list = getSetUpAchievementsList(fixtureFilledFileName).getAllAchievements();

        Assert.assertEquals(2, list.size());
        Assert.assertEquals("I miejsce w ogólnopolskim konkursie ortograficznym", list.get(0));
        Assert.assertEquals("III miejsce w ogólnopolskim konkursie plastycznym", list.get(1));
    }

    @Test
    public void getAllAchievementsEmptyTest() throws Exception {
        List<String> list = getSetUpAchievementsList(fixtureEmptyFileName).getAllAchievements();

        Assert.assertEquals(0, list.size());
    }
}
