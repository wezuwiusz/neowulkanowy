package io.github.wulkanowy.services.sync;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.api.login.AccountPermissionException;
import io.github.wulkanowy.api.login.BadCredentialsException;
import io.github.wulkanowy.api.login.LoginErrorException;
import io.github.wulkanowy.api.login.NotLoggedInErrorException;
import io.github.wulkanowy.api.login.VulcanOfflineException;
import io.github.wulkanowy.db.dao.entities.DaoSession;
import io.github.wulkanowy.services.jobs.VulcanJobHelper;
import io.github.wulkanowy.utils.security.CryptoException;

public class VulcanSync {

    private LoginSession loginSession;

    public VulcanSync(LoginSession loginSession) {
        this.loginSession = loginSession;
    }

    public VulcanSync() {
        this.loginSession = new LoginSession();
    }

    public void firstLoginSignInStep(Context context, DaoSession daoSession, String email, String password, String symbol)
            throws NotLoggedInErrorException, AccountPermissionException, IOException, CryptoException, VulcanOfflineException, BadCredentialsException {
        FirstAccountLogin firstAccountLogin = new FirstAccountLogin(context, daoSession, new Vulcan());
        loginSession = firstAccountLogin.login(email, password, symbol);
    }

    public VulcanSync loginCurrentUser(Context context, DaoSession daoSession) throws CryptoException,
            BadCredentialsException, AccountPermissionException, LoginErrorException, IOException, VulcanOfflineException {
        return loginCurrentUser(context, daoSession, new Vulcan());
    }

    public VulcanSync loginCurrentUser(Context context, DaoSession daoSession, Vulcan vulcan)
            throws CryptoException, BadCredentialsException, AccountPermissionException,
            LoginErrorException, IOException, VulcanOfflineException {

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
            GradesSync gradesSync = new GradesSync();
            try {
                gradesSync.sync(loginSession);
            } catch (Exception e) {
                Log.e(VulcanJobHelper.DEBUG_TAG, "Synchronisation of grades failed", e);
                throw new IOException(e.getCause());
            }
        } else {
            Log.e(VulcanJobHelper.DEBUG_TAG, "Before sync, should login user to log",
                    new UnsupportedOperationException());
        }
    }

    public void syncSubjectsAndGrades() throws IOException {
        if (loginSession != null) {
            SubjectsSync subjectsSync = new SubjectsSync();
            try {
                subjectsSync.sync(loginSession);
                syncGrades();
            } catch (Exception e) {
                Log.e(VulcanJobHelper.DEBUG_TAG, "Synchronisation of subjects failed", e);
                throw new IOException(e.getCause());
            }
        } else {
            Log.e(VulcanJobHelper.DEBUG_TAG, "Before sync, should login user to log",
                    new UnsupportedOperationException());
        }
    }

    public void syncTimetable() throws IOException {
        syncTimetable(null);
    }

    public void syncTimetable(@Nullable String date) throws IOException {
        if (loginSession != null) {
            TimetableSync timetableSync = new TimetableSync();
            try {
                timetableSync.sync(loginSession, date);
            } catch (Exception e) {
                Log.e(VulcanJobHelper.DEBUG_TAG, "Synchronization of timetable failed", e);
                throw new IOException(e.getCause());
            }
        } else {
            Log.e(VulcanJobHelper.DEBUG_TAG, "Before sync, should login user to log",
                    new UnsupportedOperationException());
        }
    }
}
