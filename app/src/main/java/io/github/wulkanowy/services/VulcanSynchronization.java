package io.github.wulkanowy.services;

import android.content.Context;
import android.support.annotation.Nullable;
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
import io.github.wulkanowy.services.synchronisation.TimetableSynchronization;

public class VulcanSynchronization {

    private LoginSession loginSession;

    private FirstAccountLogin firstAccountLogin;

    private String certificate;

    public VulcanSynchronization(LoginSession loginSession) {
        this.loginSession = loginSession;
    }

    public VulcanSynchronization() {
        this.loginSession = new LoginSession();
    }

    public void firstLoginConnectStep(String email, String password, String symbol)
            throws BadCredentialsException, IOException {
        firstAccountLogin = new FirstAccountLogin(new Login(new Cookies()), new Vulcan(), email, password, symbol);
        certificate = firstAccountLogin.connect();
    }

    public void firstLoginSignInStep(Context context, DaoSession daoSession)
            throws NotLoggedInErrorException, AccountPermissionException, IOException, CryptoException {
        if (firstAccountLogin != null && certificate != null) {
            loginSession = firstAccountLogin.login(context, daoSession, certificate);
        } else {
            Log.e(VulcanJobHelper.DEBUG_TAG, "Before first login, should call firstLoginConnectStep",
                    new UnsupportedOperationException());
        }
    }

    public VulcanSynchronization loginCurrentUser(Context context, DaoSession daoSession) throws CryptoException,
            BadCredentialsException, AccountPermissionException, LoginErrorException, IOException {
        return loginCurrentUser(context, daoSession, new Vulcan());
    }

    public VulcanSynchronization loginCurrentUser(Context context, DaoSession daoSession, Vulcan vulcan)
            throws CryptoException, BadCredentialsException, AccountPermissionException,
            LoginErrorException, IOException {
        CurrentAccountLogin currentAccountLogin = new CurrentAccountLogin(context, daoSession, vulcan);
        loginSession = currentAccountLogin.loginCurrentUser();
        return this;
    }

    public void syncAll() throws IOException {
        syncSubjectsAndGrades();
        syncTimetable();
    }

    public void syncGrades() throws IOException {
        if (loginSession != null) {
            GradesSynchronisation gradesSynchronisation = new GradesSynchronisation();
            try {
                gradesSynchronisation.sync(loginSession);
            } catch (Exception e) {
                Log.e(VulcanJobHelper.DEBUG_TAG, "Synchronisation of grades failed", e);
                throw new IOException(e.getCause());
            }
        } else {
            Log.e(VulcanJobHelper.DEBUG_TAG, "Before synchronization, should login user to log",
                    new UnsupportedOperationException());
        }
    }

    public void syncSubjectsAndGrades() throws IOException {
        if (loginSession != null) {
            SubjectsSynchronisation subjectsSynchronisation = new SubjectsSynchronisation();
            try {
                subjectsSynchronisation.sync(loginSession);
                syncGrades();
            } catch (Exception e) {
                Log.e(VulcanJobHelper.DEBUG_TAG, "Synchronisation of subjects failed", e);
                throw new IOException(e.getCause());
            }
        } else {
            Log.e(VulcanJobHelper.DEBUG_TAG, "Before synchronization, should login user to log",
                    new UnsupportedOperationException());
        }
    }

    public void syncTimetable() throws IOException {
        syncTimetable(null);
    }

    public void syncTimetable(@Nullable String date) throws IOException {
        if (loginSession != null) {
            TimetableSynchronization timetableSynchronization = new TimetableSynchronization();
            try {
                timetableSynchronization.sync(loginSession, date);
            } catch (Exception e) {
                Log.e(VulcanJobHelper.DEBUG_TAG, "Synchronization of timetable failed", e);
                throw new IOException(e.getCause());
            }
        } else {
            Log.e(VulcanJobHelper.DEBUG_TAG, "Before synchronization, should login user to log",
                    new UnsupportedOperationException());
        }
    }
}
