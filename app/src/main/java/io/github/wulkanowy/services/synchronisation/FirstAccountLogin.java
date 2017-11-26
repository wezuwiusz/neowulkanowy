package io.github.wulkanowy.services.synchronisation;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;

import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.login.AccountPermissionException;
import io.github.wulkanowy.api.login.BadCredentialsException;
import io.github.wulkanowy.api.login.Login;
import io.github.wulkanowy.api.login.NotLoggedInErrorException;
import io.github.wulkanowy.dao.entities.Account;
import io.github.wulkanowy.dao.entities.AccountDao;
import io.github.wulkanowy.dao.entities.DaoSession;
import io.github.wulkanowy.security.CryptoException;
import io.github.wulkanowy.security.Safety;
import io.github.wulkanowy.services.LoginSession;

public class FirstAccountLogin {


    private final Login login;

    private final Vulcan vulcan;

    private final String email;

    private final String password;

    private final String symbol;

    public FirstAccountLogin(Login login, Vulcan vulcan, String email, String password, String symbol) {
        this.login = login;
        this.vulcan = vulcan;
        this.email = email;
        this.password = password;
        this.symbol = symbol;
    }

    public String connect()
            throws BadCredentialsException, IOException {
        return login.sendCredentials(email, password, symbol);
    }

    public LoginSession login(Context context, DaoSession daoSession, String certificate)
            throws NotLoggedInErrorException, AccountPermissionException, IOException, CryptoException{

        long userId;

        String realSymbol = login.sendCertificate(certificate, symbol);

        vulcan.login(login.getCookiesObject(), realSymbol);

        AccountDao accountDao = daoSession.getAccountDao();
        Safety safety = new Safety();
        Account account = new Account()
                .setName(vulcan.getBasicInformation().getPersonalData().getFirstAndLastName())
                .setEmail(email)
                .setPassword(safety.encrypt(email, password, context))
                .setSymbol(vulcan.getStudentAndParent().getSymbol())
                .setSnpId(vulcan.getStudentAndParent().getId());

        userId = accountDao.insert(account);

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
