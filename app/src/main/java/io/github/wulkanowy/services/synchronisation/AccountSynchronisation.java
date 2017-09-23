package io.github.wulkanowy.services.synchronisation;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.IOException;

import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.login.AccountPermissionException;
import io.github.wulkanowy.api.login.BadCredentialsException;
import io.github.wulkanowy.api.login.LoginErrorException;
import io.github.wulkanowy.api.login.NotLoggedInErrorException;
import io.github.wulkanowy.api.user.PersonalData;
import io.github.wulkanowy.dao.entities.Account;
import io.github.wulkanowy.dao.entities.AccountDao;
import io.github.wulkanowy.dao.entities.DaoSession;
import io.github.wulkanowy.security.CryptoException;
import io.github.wulkanowy.security.Safety;
import io.github.wulkanowy.services.LoginSession;
import io.github.wulkanowy.services.jobs.VulcanSync;

public class AccountSynchronisation {

    public LoginSession loginCurrentUser(Context context, DaoSession daoSession, Vulcan vulcan) throws CryptoException,
            BadCredentialsException, AccountPermissionException, IOException, LoginErrorException {

        AccountDao accountDao = daoSession.getAccountDao();

        long userId = context.getSharedPreferences("LoginData", Context.MODE_PRIVATE).getLong("userId", 0);

        if (userId != 0) {

            Log.d(VulcanSync.DEBUG_TAG, "Login current user id=" + String.valueOf(userId));

            Safety safety = new Safety(context);
            Account account = accountDao.load(userId);
            vulcan.login(
                    account.getEmail(),
                    safety.decrypt(account.getEmail(), account.getPassword()),
                    account.getSymbol()
            );

            return new LoginSession()
                    .setDaoSession(daoSession)
                    .setUserId(userId)
                    .setVulcan(vulcan);
        } else {
            Log.wtf(VulcanSync.DEBUG_TAG, "loginCurrentUser - USERID IS EMPTY");
            throw new IOException("Can't find user with index 0");
        }
    }

    public LoginSession loginNewUser(String email, String password, String symbol, Context context, DaoSession daoSession, Vulcan vulcan)
            throws BadCredentialsException, NotLoggedInErrorException, AccountPermissionException, IOException, CryptoException {

        long userId;

        vulcan.login(email, password, symbol);

        PersonalData personalData = vulcan.getBasicInformation().getPersonalData();
        AccountDao accountDao = daoSession.getAccountDao();
        Safety safety = new Safety(context);

        Account account = new Account()
                .setName(personalData.getFirstAndLastName())
                .setEmail(email)
                .setPassword(safety.encrypt(email, password))
                .setSymbol(symbol);

        userId = accountDao.insert(account);

        Log.d(VulcanSync.DEBUG_TAG, "Login and save new user id=" + String.valueOf(userId));

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
