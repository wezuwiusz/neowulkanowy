package io.github.wulkanowy.services.sync;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;

import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.login.AccountPermissionException;
import io.github.wulkanowy.api.login.BadCredentialsException;
import io.github.wulkanowy.api.login.NotLoggedInErrorException;
import io.github.wulkanowy.api.login.VulcanOfflineException;
import io.github.wulkanowy.db.dao.entities.Account;
import io.github.wulkanowy.db.dao.entities.AccountDao;
import io.github.wulkanowy.db.dao.entities.DaoSession;
import io.github.wulkanowy.utils.security.CryptoException;
import io.github.wulkanowy.utils.security.Safety;

public class FirstAccountLogin {

    private final Context context;

    private final DaoSession daoSession;

    private final Vulcan vulcan;

    public FirstAccountLogin(Context context, DaoSession daoSession, Vulcan vulcan) {
        this.context = context;
        this.daoSession = daoSession;
        this.vulcan = vulcan;
    }

    public LoginSession login(String email, String password, String symbol)
            throws NotLoggedInErrorException, AccountPermissionException, IOException, CryptoException, VulcanOfflineException, BadCredentialsException {

        vulcan.login(email, password, symbol);

        AccountDao accountDao = daoSession.getAccountDao();
        Safety safety = new Safety();
        Account account = new Account()
                .setName(vulcan.getBasicInformation().getPersonalData().getFirstAndLastName())
                .setEmail(email)
                .setPassword(safety.encrypt(email, password, context))
                .setSymbol(vulcan.getStudentAndParent().getSymbol())
                .setSnpId(vulcan.getStudentAndParent().getId());

        long userId = accountDao.insert(account);

        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("userId", userId);
        editor.apply();

        return new LoginSession()
                .setVulcan(vulcan)
                .setUserId(userId)
                .setDaoSession(daoSession);
    }
}
