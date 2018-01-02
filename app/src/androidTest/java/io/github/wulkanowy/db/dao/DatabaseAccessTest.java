package io.github.wulkanowy.db.dao;

import android.support.test.InstrumentationRegistry;

import org.greenrobot.greendao.database.Database;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.github.wulkanowy.db.dao.entities.DaoMaster;
import io.github.wulkanowy.db.dao.entities.DaoSession;
import io.github.wulkanowy.db.dao.entities.Grade;

public class DatabaseAccessTest extends DatabaseAccess {

    private static DaoSession daoSession;

    @BeforeClass
    public static void setUpClass() {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(InstrumentationRegistry.getTargetContext()
                , "wulkanowyTest-db");
        Database database = devOpenHelper.getWritableDb();

        daoSession = new DaoMaster(database).newSession();
    }

    @Before
    public void setUp() {
        daoSession.getGradeDao().deleteAll();
        daoSession.clear();
    }

    @Test
    public void getNewGradesTest() {
        daoSession.getGradeDao().insert(new Grade()
                .setIsNew(true));

        Assert.assertEquals(1, new DatabaseAccess().getNewGrades(daoSession).size());
    }

    @AfterClass
    public static void cleanUp() {
        daoSession.getAccountDao().deleteAll();
        daoSession.getGradeDao().deleteAll();
        daoSession.getSubjectDao().deleteAll();
        daoSession.clear();
    }
}
