package io.github.wulkanowy.services.sync;

import android.content.Context;
import android.content.SharedPreferences;
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

import java.io.IOException;

import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.login.AccountPermissionException;
import io.github.wulkanowy.api.login.BadCredentialsException;
import io.github.wulkanowy.api.login.LoginErrorException;
import io.github.wulkanowy.api.login.VulcanOfflineException;
import io.github.wulkanowy.db.dao.entities.Account;
import io.github.wulkanowy.db.dao.entities.AccountDao;
import io.github.wulkanowy.db.dao.entities.DaoMaster;
import io.github.wulkanowy.db.dao.entities.DaoSession;
import io.github.wulkanowy.utils.security.CryptoException;
import io.github.wulkanowy.utils.security.Safety;

@RunWith(AndroidJUnit4.class)
public class CurrentAccountLoginTest {

    private static DaoSession daoSession;

    private Context context;

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
        context = InstrumentationRegistry.getContext();
        targetContext = InstrumentationRegistry.getTargetContext();

        daoSession.getAccountDao().deleteAll();
        daoSession.clear();

        setUserIdSharePreferences(0);
    }

    @Test(expected = IOException.class)
    public void emptyUserIdTest() throws CryptoException, BadCredentialsException,
            AccountPermissionException, IOException, LoginErrorException, VulcanOfflineException {

        CurrentAccountLogin currentAccountLogin = new CurrentAccountLogin(context, daoSession, new Vulcan());
        currentAccountLogin.loginCurrentUser();
    }

    @Test
    public void loginCurrentUserTest() throws Exception {
        AccountDao accountDao = daoSession.getAccountDao();

        Safety safety = new Safety();

        Long userId = accountDao.insert(new Account()
                .setEmail("TEST@TEST")
                .setPassword(safety.encrypt("TEST@TEST", "TEST", context))
                .setSymbol(""));

        setUserIdSharePreferences(userId);

        Vulcan vulcan = Mockito.mock(Vulcan.class);
        Mockito.when(vulcan.login("TEST@TEST", "TEST", "TEST_SYMBOL", "TEST_ID")).thenReturn(new Vulcan());

        CurrentAccountLogin currentAccountLogin = new CurrentAccountLogin(targetContext, daoSession, vulcan);
        LoginSession loginSession = currentAccountLogin.loginCurrentUser();

        Assert.assertNotNull(loginSession);
        Assert.assertEquals(loginSession.getUserId(), userId);
        Assert.assertNotNull(loginSession.getDaoSession());
        Assert.assertEquals(loginSession.getVulcan(), vulcan);
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
