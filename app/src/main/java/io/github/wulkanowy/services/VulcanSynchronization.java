package io.github.wulkanowy.services;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.login.AccountPermissionException;
import io.github.wulkanowy.api.login.BadCredentialsException;
import io.github.wulkanowy.api.login.LoginErrorException;
import io.github.wulkanowy.api.login.NotLoggedInErrorException;
import io.github.wulkanowy.dao.entities.DaoSession;
import io.github.wulkanowy.security.CryptoException;
import io.github.wulkanowy.services.jobs.VulcanSync;
import io.github.wulkanowy.services.synchronisation.AccountSynchronisation;
import io.github.wulkanowy.services.synchronisation.GradesSynchronisation;
import io.github.wulkanowy.services.synchronisation.SubjectsSynchronisation;

public class VulcanSynchronization {

    private LoginSession loginSession;

    public VulcanSynchronization(LoginSession loginSession) {
        this.loginSession = loginSession;
    }

    public void loginCurrentUser(Context context, DaoSession daoSession, Vulcan vulcan)
            throws CryptoException, BadCredentialsException, AccountPermissionException, IOException, LoginErrorException {

        AccountSynchronisation accountSynchronisation = new AccountSynchronisation();
        loginSession = accountSynchronisation.loginCurrentUser(context, daoSession, vulcan);
    }

    public void loginNewUser(String email, String password, String symbol,
                             Context context, DaoSession daoSession, Vulcan vulcan)
            throws BadCredentialsException, NotLoggedInErrorException, AccountPermissionException, IOException, CryptoException {

        AccountSynchronisation accountSynchronisation = new AccountSynchronisation();
        loginSession = accountSynchronisation.loginNewUser(email, password, symbol, context, daoSession, vulcan);
    }

    public boolean syncGrades() {
        GradesSynchronisation gradesSynchronisation = new GradesSynchronisation();

        try {
            gradesSynchronisation.sync(loginSession);
            return true;
        } catch (Exception e) {
            Log.e(VulcanSync.DEBUG_TAG, "Synchronisation of grades failed", e);
            return false;
        }
    }

    public boolean syncSubjectsAndGrades() {
        SubjectsSynchronisation subjectsSynchronisation = new SubjectsSynchronisation();

        try {
            subjectsSynchronisation.sync(loginSession);
            syncGrades();
            return true;
        } catch (Exception e) {
            Log.e(VulcanSync.DEBUG_TAG, "Synchronisation of subjects failed", e);
            return false;
        }
    }
}
