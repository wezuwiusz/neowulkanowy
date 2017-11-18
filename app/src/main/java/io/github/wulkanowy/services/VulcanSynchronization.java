package io.github.wulkanowy.services;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import io.github.wulkanowy.api.Cookies;
import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.login.AccountPermissionException;
import io.github.wulkanowy.api.login.BadCredentialsException;
import io.github.wulkanowy.api.login.Login;
import io.github.wulkanowy.api.login.LoginErrorException;
import io.github.wulkanowy.api.login.NotLoggedInErrorException;
import io.github.wulkanowy.dao.entities.DaoSession;
import io.github.wulkanowy.security.CryptoException;
import io.github.wulkanowy.services.jobs.VulcanJobHelper;
import io.github.wulkanowy.services.synchronisation.CurrentAccountLogin;
import io.github.wulkanowy.services.synchronisation.FirstAccountLogin;
import io.github.wulkanowy.services.synchronisation.GradesSynchronisation;
import io.github.wulkanowy.services.synchronisation.SubjectsSynchronisation;

public class VulcanSynchronization {

    private LoginSession loginSession;

    private FirstAccountLogin firstAccountLogin;

    private String certificate;

    public VulcanSynchronization(LoginSession loginSession) {
        this.loginSession = loginSession;
    }

    public void firstLoginConnectStep(String email, String password, String symbol)
            throws BadCredentialsException, IOException {
        firstAccountLogin = new FirstAccountLogin(new Login(new Cookies()), new Vulcan(), email, password, symbol);
        certificate = firstAccountLogin.connect();
    }

    public void firstLoginSignInStep(Context context, DaoSession daoSession)
            throws NotLoggedInErrorException, AccountPermissionException, IOException, CryptoException,
            UnsupportedOperationException {
        if (firstAccountLogin != null && certificate != null) {
            loginSession = firstAccountLogin.login(context, daoSession, certificate);
        } else {
            Log.e(VulcanJobHelper.DEBUG_TAG, "Before first login, should call firstLoginConnectStep",
                    new UnsupportedOperationException());
        }
    }

    public void loginCurrentUser(Context context, DaoSession daoSession, Vulcan vulcan)
            throws CryptoException, BadCredentialsException, AccountPermissionException, LoginErrorException, IOException {
        CurrentAccountLogin currentAccountLogin = new CurrentAccountLogin(context, daoSession, vulcan);
        loginSession = currentAccountLogin.loginCurrentUser();
    }

    public boolean syncGrades() {
        if (loginSession != null) {
            GradesSynchronisation gradesSynchronisation = new GradesSynchronisation();
            try {
                gradesSynchronisation.sync(loginSession);
                return true;
            } catch (Exception e) {
                Log.e(VulcanJobHelper.DEBUG_TAG, "Synchronisation of grades failed", e);
                return false;
            }
        } else {
            Log.e(VulcanJobHelper.DEBUG_TAG, "Before synchronization, should login user to log",
                    new UnsupportedOperationException());
            return false;
        }
    }

    public boolean syncSubjectsAndGrades() {
        if (loginSession != null) {
            SubjectsSynchronisation subjectsSynchronisation = new SubjectsSynchronisation();
            try {
                subjectsSynchronisation.sync(loginSession);
                syncGrades();
                return true;
            } catch (Exception e) {
                Log.e(VulcanJobHelper.DEBUG_TAG, "Synchronisation of subjects failed", e);
                return false;
            }
        } else {
            Log.e(VulcanJobHelper.DEBUG_TAG, "Before synchronization, should login user to log",
                    new UnsupportedOperationException());
            return false;
        }
    }
}
