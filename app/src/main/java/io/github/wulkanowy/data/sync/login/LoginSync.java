package io.github.wulkanowy.data.sync.login;

import android.content.Context;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.login.AccountPermissionException;
import io.github.wulkanowy.api.login.BadCredentialsException;
import io.github.wulkanowy.api.login.NotLoggedInErrorException;
import io.github.wulkanowy.api.login.VulcanOfflineException;
import io.github.wulkanowy.data.db.dao.entities.Account;
import io.github.wulkanowy.data.db.dao.entities.DaoSession;
import io.github.wulkanowy.data.db.shared.SharedPrefContract;
import io.github.wulkanowy.di.annotations.ApplicationContext;
import io.github.wulkanowy.utils.LogUtils;
import io.github.wulkanowy.utils.security.CryptoException;
import io.github.wulkanowy.utils.security.Scrambler;

@Singleton
public class LoginSync implements LoginSyncContract {

    private final DaoSession daoSession;

    private final SharedPrefContract sharedPref;

    private final Vulcan vulcan;

    private final Context context;

    @Inject
    LoginSync(DaoSession daoSession, SharedPrefContract sharedPref,
              Vulcan vulcan, @ApplicationContext Context context) {
        this.daoSession = daoSession;
        this.sharedPref = sharedPref;
        this.vulcan = vulcan;
        this.context = context;
    }

    @Override
    public void loginUser(String email, String password, String symbol)
            throws NotLoggedInErrorException, AccountPermissionException, IOException,
            CryptoException, VulcanOfflineException, BadCredentialsException {

        LogUtils.debug("Login new user email=" + email);

        vulcan.login(email, password, symbol);

        Account account = new Account()
                .setName(vulcan.getBasicInformation().getPersonalData().getFirstAndLastName())
                .setEmail(email)
                .setPassword(Scrambler.encrypt(email, password, context))
                .setSymbol(vulcan.getSymbol())
                .setSnpId(vulcan.getStudentAndParent().getId());

        sharedPref.setCurrentUserId(daoSession.getAccountDao().insert(account));
    }

    @Override
    public void loginCurrentUser() throws NotLoggedInErrorException, AccountPermissionException,
            IOException, CryptoException, VulcanOfflineException, BadCredentialsException {

        long userId = sharedPref.getCurrentUserId();

        if (userId == 0) {
            throw new IOException("Can't find logged user");
        }

        LogUtils.debug("Login current user id=" + userId);

        Account account = daoSession.getAccountDao().load(userId);

        vulcan.login(account.getEmail(),
                Scrambler.decrypt(account.getEmail(), account.getPassword()),
                account.getSymbol(),
                account.getSnpId());
    }
}
