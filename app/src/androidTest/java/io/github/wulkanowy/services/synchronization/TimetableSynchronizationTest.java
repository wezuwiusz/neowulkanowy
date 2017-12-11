package io.github.wulkanowy.services.synchronization;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.greenrobot.greendao.database.Database;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.timetable.Day;
import io.github.wulkanowy.api.timetable.Lesson;
import io.github.wulkanowy.api.timetable.Timetable;
import io.github.wulkanowy.api.timetable.Week;
import io.github.wulkanowy.dao.entities.Account;
import io.github.wulkanowy.dao.entities.DaoMaster;
import io.github.wulkanowy.dao.entities.DaoSession;
import io.github.wulkanowy.services.LoginSession;
import io.github.wulkanowy.services.synchronisation.TimetableSynchronization;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@RunWith(AndroidJUnit4.class)
public class TimetableSynchronizationTest {

    private static DaoSession daoSession;

    @BeforeClass
    public static void setUpClass() {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(InstrumentationRegistry.getTargetContext(), "wulkanowyTest-database");
        Database database = devOpenHelper.getWritableDb();

        daoSession = new DaoMaster(database).newSession();
    }

    @Test
    public void syncTimetableEmptyDatabaseTest() throws Exception {
        Long userId = daoSession.getAccountDao().insert(new Account().setEmail("TEST@TEST"));

        List<Day> dayList = new ArrayList<>();
        dayList.add(new Day()
                .setDate("20.12.2012")
                .setLesson(new Lesson().setSubject("Matematyka").setRoom("20")));
        Week week = new Week().setDays(dayList);

        List<Day> nextDayList = new ArrayList<>();
        dayList.add(new Day()
                .setDate("24.11.2013")
                .setLesson(new Lesson().setSubject("Matematyka").setRoom("22")));
        Week nextWeek = new Week().setDays(nextDayList);

        Timetable timetable = mock(Timetable.class);
        doReturn(week).when(timetable).getWeekTable();
        doReturn(nextWeek).when(timetable).getWeekTable(anyString());

        Vulcan vulcan = mock(Vulcan.class);
        doReturn(timetable).when(vulcan).getTimetable();

        LoginSession loginSession = mock(LoginSession.class);
        doReturn(vulcan).when(loginSession).getVulcan();
        doReturn(daoSession).when(loginSession).getDaoSession();
        doReturn(userId).when(loginSession).getUserId();

        TimetableSynchronization timetableSynchronization = new TimetableSynchronization();
        timetableSynchronization.sync(loginSession, null);

        List<io.github.wulkanowy.dao.entities.Day> dayEntityList = daoSession.getDayDao().loadAll();
        List<io.github.wulkanowy.dao.entities.Lesson> lessonEntityList = dayEntityList.get(0).getLessons();

        Assert.assertNotNull(dayEntityList.get(0));
        Assert.assertEquals(userId, dayEntityList.get(0).getUserId());
        Assert.assertEquals(1L, lessonEntityList.get(0).getDayId().longValue());
        Assert.assertEquals("Matematyka", lessonEntityList.get(0).getSubject());
        Assert.assertEquals("20", lessonEntityList.get(0).getRoom());
        Assert.assertEquals("20.12.2012", dayEntityList.get(0).getDate());

    }

    @Before
    public void setUp() {
        daoSession.getAccountDao().deleteAll();
        daoSession.getDayDao().deleteAll();
        daoSession.getLessonDao().deleteAll();
        daoSession.clear();
    }

    @AfterClass
    public static void cleanUp() {
        daoSession.getAccountDao().deleteAll();
        daoSession.getDayDao().deleteAll();
        daoSession.getLessonDao().deleteAll();
        daoSession.clear();
    }
}
