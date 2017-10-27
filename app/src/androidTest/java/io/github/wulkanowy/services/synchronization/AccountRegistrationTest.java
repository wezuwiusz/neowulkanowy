package io.github.wulkanowy.services.synchronization;

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
import io.github.wulkanowy.api.login.Login;
import io.github.wulkanowy.api.user.BasicInformation;
import io.github.wulkanowy.api.user.PersonalData;
import io.github.wulkanowy.dao.entities.Account;
import io.github.wulkanowy.dao.entities.DaoMaster;
import io.github.wulkanowy.dao.entities.DaoSession;
import io.github.wulkanowy.security.Safety;
import io.github.wulkanowy.services.LoginSession;
import io.github.wulkanowy.services.synchronisation.AccountRegistration;

public class AccountRegistrationTest {

    private static DaoSession daoSession;

    private Context targetContext;

    @BeforeClass
    public static void setUpClass() {

        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(InstrumentationRegistry.getTargetContext(), "wulkanowyTest-database");
        Database database = devOpenHelper.getWritableDb();

        daoSession = new DaoMaster(database).newSession();
    }

    @Before
    public void setUp() {
        targetContext = InstrumentationRegistry.getTargetContext();

        daoSession.getAccountDao().deleteAll();
        daoSession.clear();

        setUserIdSharePreferences(0);
    }

    @Test
    public void connectTest() throws Exception {
        String certificate = "<xml>Certificate</xml>";
        Login login = Mockito.mock(Login.class);
        Mockito.when(login.sendCredentials(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(certificate);
        AccountRegistration accountRegistration = new AccountRegistration(login, new Vulcan(), "TEST@TEST", "TEST_PASS", "TEST_SYMBOL");

        Assert.assertEquals(certificate, accountRegistration.connect());
    }

    @Test
    public void loginTest() throws Exception {
        StudentAndParent snp = Mockito.mock(StudentAndParent.class);
        Mockito.when(snp.getSymbol()).thenReturn("TEST-SYMBOL");
        Mockito.when(snp.getId()).thenReturn("TEST-ID");

        PersonalData personalData = Mockito.mock(PersonalData.class);
        Mockito.doReturn("NAME-TEST").when(personalData).getFirstAndLastName();

        BasicInformation basicInformation = Mockito.mock(BasicInformation.class);
        Mockito.doReturn(personalData).when(basicInformation).getPersonalData();

        Vulcan vulcan = Mockito.mock(Vulcan.class);
        Mockito.doReturn(basicInformation).when(vulcan).getBasicInformation();
        Mockito.doReturn(snp).when(vulcan).getStudentAndParent();

        Login login = Mockito.mock(Login.class);
        Mockito.when(login.sendCertificate(Mockito.anyString(), Mockito.anyString())).thenReturn("TEST-SYMBOL");

        AccountRegistration accountRegistration = new AccountRegistration(login, vulcan, "TEST@TEST", "TEST-PASS", "default");
        LoginSession loginSession = accountRegistration.login(targetContext, daoSession, "<xml>cert</xml>");

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
