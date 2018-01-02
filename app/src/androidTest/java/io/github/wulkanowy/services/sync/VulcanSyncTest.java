package io.github.wulkanowy.services.sync;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class VulcanSyncTest {

    @Test(expected = IOException.class)
    public void syncNoLoginSessionSubjectTest() throws IOException {
        VulcanSync vulcanSync = new VulcanSync(new LoginSession());
        vulcanSync.syncSubjectsAndGrades();
    }

    @Test(expected = IOException.class)
    public void syncNoLoginSessionGradeTest() throws IOException {
        VulcanSync vulcanSync = new VulcanSync(new LoginSession());
        vulcanSync.syncGrades();
    }

    @Test(expected = IOException.class)
    public void syncNoLoginSessionTimetableTest() throws IOException {
        VulcanSync vulcanSync = new VulcanSync(new LoginSession());
        vulcanSync.syncTimetable();
    }
}
