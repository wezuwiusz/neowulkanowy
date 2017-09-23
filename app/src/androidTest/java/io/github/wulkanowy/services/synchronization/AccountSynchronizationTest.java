package io.github.wulkanowy.services.synchronization;

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
import io.github.wulkanowy.api.user.BasicInformation;
import io.github.wulkanowy.api.user.PersonalData;
import io.github.wulkanowy.dao.entities.Account;
import io.github.wulkanowy.dao.entities.AccountDao;
import io.github.wulkanowy.dao.entities.DaoMaster;
import io.github.wulkanowy.dao.entities.DaoSession;
import io.github.wulkanowy.security.CryptoException;
import io.github.wulkanowy.security.Safety;
import io.github.wulkanowy.services.LoginSession;
import io.github.wulkanowy.services.synchronisation.AccountSynchronisation;

@RunWith(AndroidJUnit4.class)
public class AccountSynchronizationTest {

    private static DaoSession daoSession;

    private Context context;

    private Context targetContext;

    @BeforeClass
    public static void setUpClass() {

        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(InstrumentationRegistry.getTargetContext(), "wulkanowyTest-database");
        Database database = devOpenHelper.getWritableDb();

        daoSession = new DaoMaster(database).newSession();
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
            AccountPermissionException, IOException, LoginErrorException {

        AccountSynchronisation accountSynchronisation = new AccountSynchronisation();
        accountSynchronisation.loginCurrentUser(context, daoSession, new Vulcan());
    }

    @Test
    public void loginCurrentUserTest() throws Exception {
        AccountDao accountDao = daoSession.getAccountDao();

        Safety safety = new Safety(context);

        Long userId = accountDao.insert(new Account()
                .setEmail("TEST@TEST")
                .setPassword(safety.encrypt("TEST@TEST", "TEST"))
                .setSymbol(""));

        setUserIdSharePreferences(userId);

        Vulcan vulcan = Mockito.mock(Vulcan.class);
        Mockito.doNothing().when(vulcan).login("TEST@TEST", "TEST", "");

        AccountSynchronisation accountSynchronisation = new AccountSynchronisation();
        LoginSession loginSession = accountSynchronisation.loginCurrentUser(targetContext, daoSession, vulcan);

        Assert.assertNotNull(loginSession);
        Assert.assertEquals(loginSession.getUserId(), userId);
        Assert.assertNotNull(loginSession.getDaoSession());
        Assert.assertEquals(loginSession.getVulcan(), vulcan);
    }

    @Test
    public void loginNewUserTest() throws Exception {
        PersonalData personalData = Mockito.mock(PersonalData.class);
        Mockito.doReturn("NAME-TEST").when(personalData).getFirstAndLastName();

        BasicInformation basicInformation = Mockito.mock(BasicInformation.class);
        Mockito.doReturn(personalData).when(basicInformation).getPersonalData();

        Vulcan vulcan = Mockito.mock(Vulcan.class);
        Mockito.doNothing().when(vulcan).login("TEST@TEST", "TEST", "");
        Mockito.doReturn(basicInformation).when(vulcan).getBasicInformation();

        AccountSynchronisation accountSynchronisation = new AccountSynchronisation();
        LoginSession loginSession = accountSynchronisation
                .loginNewUser("TEST@TEST", "TEST", "", targetContext, daoSession, vulcan);

        Long userId = targetContext.getSharedPreferences("LoginData", Context.MODE_PRIVATE).getLong("userId", 0);

        Assert.assertNotNull(loginSession);
        Assert.assertNotEquals(0, userId.longValue());
        Assert.assertEquals(loginSession.getUserId(), userId);
        Assert.assertNotNull(loginSession.getDaoSession());
        Assert.assertEquals(loginSession.getVulcan(), vulcan);

        Safety safety = new Safety(context);
        Account account = daoSession.getAccountDao().load(userId);

        Assert.assertNotNull(account);
        Assert.assertEquals("TEST@TEST", account.getEmail());
        Assert.assertEquals("NAME-TEST", account.getName());
        Assert.assertEquals("TEST", safety.decrypt("TEST@TEST", account.getPassword()));

    }

    @AfterClass
    public static void cleanUp() {
        daoSession.getAccountDao().deleteAll();
        daoSession.clear();
    }

    private void setUserIdSharePreferences(long id) {
        SharedPreferences sharedPreferences = targetContext.getSharedPreferences("LoginData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("userId", id);
        editor.apply();
    }
}
