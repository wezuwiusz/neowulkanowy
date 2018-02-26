package io.github.wulkanowy.services.sync;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;

import org.greenrobot.greendao.database.Database;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import io.github.wulkanowy.api.StudentAndParent;
import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.user.BasicInformation;
import io.github.wulkanowy.api.user.PersonalData;
import io.github.wulkanowy.db.dao.entities.Account;
import io.github.wulkanowy.db.dao.entities.DaoMaster;
import io.github.wulkanowy.db.dao.entities.DaoSession;
import io.github.wulkanowy.utils.security.Safety;

public class FirstAccountLoginTest {

    private static DaoSession daoSession;

    private Context targetContext;

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
        targetContext = InstrumentationRegistry.getTargetContext();

        daoSession.getAccountDao().deleteAll();
        daoSession.clear();

        setUserIdSharePreferences(0);
    }

    @Test
    public void loginTest() throws Exception {
        StudentAndParent snp = Mockito.mock(StudentAndParent.class);
        Mockito.when(snp.getId()).thenReturn("TEST-ID");

        PersonalData personalData = Mockito.mock(PersonalData.class);
        Mockito.doReturn("NAME-TEST").when(personalData).getFirstAndLastName();

        BasicInformation basicInformation = Mockito.mock(BasicInformation.class);
        Mockito.doReturn(personalData).when(basicInformation).getPersonalData();

        Vulcan vulcan = Mockito.mock(Vulcan.class);
        Mockito.doReturn("TEST-SYMBOL").when(vulcan).login(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn(snp).when(vulcan).getStudentAndParent();
        Mockito.doReturn(basicInformation).when(vulcan).getBasicInformation();

        FirstAccountLogin firstAccountLogin = new FirstAccountLogin(targetContext, daoSession, vulcan);
        LoginSession loginSession = firstAccountLogin.login("TEST@TEST", "TEST-PASS", "default");

        Long userId = targetContext.getSharedPreferences("LoginData", Context.MODE_PRIVATE).getLong("userId", 0);

        Assert.assertNotNull(loginSession);
        Assert.assertNotEquals(0, userId.longValue());
        Assert.assertEquals(loginSession.getUserId(), userId);
        Assert.assertNotNull(loginSession.getDaoSession());
        Assert.assertEquals(loginSession.getVulcan(), vulcan);

        Safety safety = new Safety();
        Account account = daoSession.getAccountDao().load(userId);
        Assert.assertNotNull(account);
        Assert.assertEquals("TEST@TEST", account.getEmail());
        Assert.assertEquals("NAME-TEST", account.getName());
        Assert.assertEquals("TEST-PASS", safety.decrypt("TEST@TEST", account.getPassword()));
        Assert.assertEquals("TEST-SYMBOL", account.getSymbol());
        Assert.assertEquals("TEST-ID", account.getSnpId());
    }

    private void setUserIdSharePreferences(long id) {
        SharedPreferences sharedPreferences = targetContext.getSharedPreferences("LoginData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("userId", id);
        editor.apply();
    }

    @AfterClass
    public static void cleanUp() {
        daoSession.getAccountDao().deleteAll();
        daoSession.clear();
    }
}
