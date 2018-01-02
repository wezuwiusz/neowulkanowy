package io.github.wulkanowy.services.sync;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.greenrobot.greendao.database.Database;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.grades.Grade;
import io.github.wulkanowy.api.grades.GradesList;
import io.github.wulkanowy.db.dao.entities.Account;
import io.github.wulkanowy.db.dao.entities.DaoMaster;
import io.github.wulkanowy.db.dao.entities.DaoSession;
import io.github.wulkanowy.db.dao.entities.Subject;

@RunWith(AndroidJUnit4.class)
public class GradeSyncTest {

    private static DaoSession daoSession;

    @BeforeClass
    public static void setUpClass() {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(InstrumentationRegistry.getTargetContext(), "wulkanowyTest-db");
        Database database = devOpenHelper.getWritableDb();
        daoSession = new DaoMaster(database).newSession();

        DaoMaster.dropAllTables(database, true);
        DaoMaster.createAllTables(database, true);
    }

    @Before
    public void setUp() {
        daoSession.getAccountDao().deleteAll();
        daoSession.getGradeDao().deleteAll();
        daoSession.getSubjectDao().deleteAll();
        daoSession.clear();
    }

    @Test
    public void syncGradesEmptyDatabaseTest() throws Exception {
        Long userId = daoSession.getAccountDao().insert(new Account().setEmail("TEST@TEST"));
        Long subjectId = daoSession.getSubjectDao().insert(new Subject().setName("Matematyka").setUserId(userId));

        List<Grade> gradeList = new ArrayList<>();
        gradeList.add(new Grade().setSubject("Matematyka").setValue("5"));

        GradesList gradesListApi = Mockito.mock(GradesList.class);
        Mockito.doReturn(gradeList).when(gradesListApi).getAll();

        Vulcan vulcan = Mockito.mock(Vulcan.class);
        Mockito.doReturn(gradesListApi).when(vulcan).getGradesList();

        LoginSession loginSession = Mockito.mock(LoginSession.class);
        Mockito.doReturn(vulcan).when(loginSession).getVulcan();
        Mockito.doReturn(daoSession).when(loginSession).getDaoSession();
        Mockito.doReturn(userId).when(loginSession).getUserId();

        GradesSync gradesSync = new GradesSync();
        gradesSync.sync(loginSession);

        io.github.wulkanowy.db.dao.entities.Grade grade = daoSession.getGradeDao().load(1L);

        Assert.assertNotNull(grade);
        Assert.assertEquals(userId, grade.getUserId());
        Assert.assertEquals(subjectId, grade.getSubjectId());
        Assert.assertEquals("Matematyka", grade.getSubject());
        Assert.assertEquals("5", grade.getValue());
        Assert.assertFalse(grade.getIsNew());
    }

    @AfterClass
    public static void cleanUp() {
        daoSession.getAccountDao().deleteAll();
        daoSession.getGradeDao().deleteAll();
        daoSession.getSubjectDao().deleteAll();
        daoSession.clear();
    }
}
