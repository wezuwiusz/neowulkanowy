package io.github.wulkanowy.services.synchronisation;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.login.AccountPermissionException;
import io.github.wulkanowy.api.login.BadCredentialsException;
import io.github.wulkanowy.api.login.LoginErrorException;
import io.github.wulkanowy.dao.entities.Account;
import io.github.wulkanowy.dao.entities.AccountDao;
import io.github.wulkanowy.dao.entities.DaoSession;
import io.github.wulkanowy.security.CryptoException;
import io.github.wulkanowy.security.Safety;
import io.github.wulkanowy.services.LoginSession;
import io.github.wulkanowy.services.jobs.VulcanJobHelper;

public class AccountAuthorization {

    private final Context context;

    private final DaoSession daoSession;

    private final Vulcan vulcan;

    public AccountAuthorization(Context context, DaoSession daoSession, Vulcan vulcan) {
        this.context = context;
        this.daoSession = daoSession;
        this.vulcan = vulcan;
    }

    public LoginSession loginCurrentUser() throws CryptoException,
            BadCredentialsException, AccountPermissionException, IOException, LoginErrorException {

        AccountDao accountDao = daoSession.getAccountDao();

        long userId = context.getSharedPreferences("LoginData", Context.MODE_PRIVATE).getLong("userId", 0);

        if (userId != 0) {

            Log.d(VulcanJobHelper.DEBUG_TAG, "Login current user id=" + String.valueOf(userId));

            Safety safety = new Safety();
            Account account = accountDao.load(userId);
            vulcan.login(
                    account.getEmail(),
                    safety.decrypt(account.getEmail(), account.getPassword()),
                    account.getSymbol(),
                    account.getSnpId()
            );

            return new LoginSession()
                    .setDaoSession(daoSession)
                    .setUserId(userId)
                    .setVulcan(vulcan);
        } else {
            Log.wtf(VulcanJobHelper.DEBUG_TAG, "loginCurrentUser - USERID IS EMPTY");
            throw new IOException("Can't find user with index 0");
        }
    }
}
