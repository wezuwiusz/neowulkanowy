package io.github.wulkanowy.services;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class VulcanSynchronizationTest {

    @Test(expected = IOException.class)
    public void syncNoLoginSessionSubjectTest() throws IOException {
        VulcanSynchronization vulcanSynchronization = new VulcanSynchronization(new LoginSession());
        vulcanSynchronization.syncSubjectsAndGrades();
    }

    @Test(expected = IOException.class)
    public void syncNoLoginSessionGradeTest() throws IOException {
        VulcanSynchronization vulcanSynchronization = new VulcanSynchronization(new LoginSession());
        vulcanSynchronization.syncGrades();
    }

    @Test(expected = IOException.class)
    public void syncNoLoginSessionTimetableTest() throws IOException {
        VulcanSynchronization vulcanSynchronization = new VulcanSynchronization(new LoginSession());
        vulcanSynchronization.syncTimetable();
    }
}
