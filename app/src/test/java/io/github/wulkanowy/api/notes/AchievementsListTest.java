package io.github.wulkanowy.api.notes;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import io.github.wulkanowy.api.StudentAndParentTestCase;

public class AchievementsListTest extends StudentAndParentTestCase {

    private AchievementsList filledAchievementsList;

    private AchievementsList emptyAchievementsList;

    @Before
    public void setUp() throws Exception {
        filledAchievementsList = new AchievementsList(getSnp("UwagiOsiagniecia-filled.html"));
        emptyAchievementsList = new AchievementsList(getSnp("UwagiOsiagniecia-empty.html"));
    }

    @Test
    public void getAllAchievementsTest() throws Exception {
        Assert.assertEquals(2, filledAchievementsList.getAllAchievements().size());
        Assert.assertEquals(0, emptyAchievementsList.getAllAchievements().size());
    }

    @Test
    public void getAchievements() throws Exception {
        List<String> filledList = filledAchievementsList.getAllAchievements();

        Assert.assertEquals("I miejsce w ogólnopolskim konkursie ortograficznym", filledList.get(0));
        Assert.assertEquals("III miejsce w ogólnopolskim konkursie plastycznym", filledList.get(1));
    }
}
