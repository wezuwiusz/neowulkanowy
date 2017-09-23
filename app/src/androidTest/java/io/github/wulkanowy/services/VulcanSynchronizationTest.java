package io.github.wulkanowy.services;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class VulcanSynchronizationTest {

    @Test
    public void syncNoLoginSessionSubjectTest() {
        VulcanSynchronization vulcanSynchronization = new VulcanSynchronization(new LoginSession());
        Assert.assertFalse(vulcanSynchronization.syncSubjectsAndGrades());
    }

    @Test
    public void syncNoLoginSessionGradeTest() {
        VulcanSynchronization vulcanSynchronization = new VulcanSynchronization(new LoginSession());
        Assert.assertFalse(vulcanSynchronization.syncGrades());
    }
}
