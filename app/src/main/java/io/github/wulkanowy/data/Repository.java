package io.github.wulkanowy.data;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.wulkanowy.api.login.AccountPermissionException;
import io.github.wulkanowy.api.login.BadCredentialsException;
import io.github.wulkanowy.api.login.NotLoggedInErrorException;
import io.github.wulkanowy.api.login.VulcanOfflineException;
import io.github.wulkanowy.data.db.dao.entities.Account;
import io.github.wulkanowy.data.db.dao.entities.AttendanceLesson;
import io.github.wulkanowy.data.db.dao.entities.DaoSession;
import io.github.wulkanowy.data.db.dao.entities.Grade;
import io.github.wulkanowy.data.db.dao.entities.GradeDao;
import io.github.wulkanowy.data.db.dao.entities.Week;
import io.github.wulkanowy.data.db.dao.entities.WeekDao;
import io.github.wulkanowy.data.db.resources.ResourcesContract;
import io.github.wulkanowy.data.db.shared.SharedPrefContract;
import io.github.wulkanowy.data.sync.SyncContract;
import io.github.wulkanowy.data.sync.attendance.AttendanceSyncContract;
import io.github.wulkanowy.data.sync.login.LoginSyncContract;
import io.github.wulkanowy.data.sync.timetable.TimetableSyncContract;
import io.github.wulkanowy.di.annotations.SyncGrades;
import io.github.wulkanowy.di.annotations.SyncSubjects;
import io.github.wulkanowy.utils.security.CryptoException;

@Singleton
public class Repository implements RepositoryContract {

    private final SharedPrefContract sharedPref;

    private final ResourcesContract resources;

    private final DaoSession daoSession;

    private final LoginSyncContract loginSync;

    private final AttendanceSyncContract attendanceSync;

    private final TimetableSyncContract timetableSync;

    private final SyncContract gradeSync;

    private final SyncContract subjectSync;

    @Inject
    Repository(SharedPrefContract sharedPref,
               ResourcesContract resources,
               DaoSession daoSession,
               LoginSyncContract loginSync,
               AttendanceSyncContract attendanceSync,
               TimetableSyncContract timetableSync,
               @SyncGrades SyncContract gradeSync,
               @SyncSubjects SyncContract subjectSync) {
        this.sharedPref = sharedPref;
        this.resources = resources;
        this.daoSession = daoSession;
        this.loginSync = loginSync;
        this.attendanceSync = attendanceSync;
        this.timetableSync = timetableSync;
        this.gradeSync = gradeSync;
        this.subjectSync = subjectSync;
    }

    @Override
    public long getCurrentUserId() {
        return sharedPref.getCurrentUserId();
    }

    @Override
    public String[] getSymbolsKeysArray() {
        return resources.getSymbolsKeysArray();
    }

    @Override
    public String[] getSymbolsValuesArray() {
        return resources.getSymbolsValuesArray();
    }

    @Override
    public String getErrorLoginMessage(Exception e) {
        return resources.getErrorLoginMessage(e);
    }

    @Override
    public String getAttendanceLessonDescription(AttendanceLesson lesson) {
        return resources.getAttendanceLessonDescription(lesson);
    }

    @Override
    public void loginUser(String email, String password, String symbol)
            throws NotLoggedInErrorException, AccountPermissionException, IOException,
            CryptoException, VulcanOfflineException, BadCredentialsException {
        loginSync.loginUser(email, password, symbol);
    }

    @Override
    public void loginCurrentUser() throws NotLoggedInErrorException, AccountPermissionException,
            IOException, CryptoException, VulcanOfflineException, BadCredentialsException {
        loginSync.loginCurrentUser();
    }

    @Override
    public void syncGrades() throws NotLoggedInErrorException, IOException, ParseException {
        gradeSync.sync();
    }

    @Override
    public void syncSubjects() throws NotLoggedInErrorException, IOException, ParseException {
        subjectSync.sync();
    }

    @Override
    public void syncAttendance() throws NotLoggedInErrorException, ParseException, IOException {
        attendanceSync.syncAttendance();
    }

    @Override
    public void syncAttendance(String date) throws NotLoggedInErrorException, ParseException, IOException {
        attendanceSync.syncAttendance(date);
    }

    @Override
    public void syncTimetable() throws NotLoggedInErrorException, IOException, ParseException {
        timetableSync.syncTimetable();
    }

    @Override
    public void syncTimetable(String date) throws NotLoggedInErrorException, IOException, ParseException {
        timetableSync.syncTimetable(date);
    }

    @Override
    public void syncAll() throws NotLoggedInErrorException, IOException, ParseException {
        syncSubjects();
        syncGrades();
        syncAttendance();
        syncTimetable();
    }

    @Override
    public Account getCurrentUser() {
        return daoSession.getAccountDao().load(sharedPref.getCurrentUserId());
    }

    @Override
    public Week getWeek(String date) {
        return daoSession.getWeekDao().queryBuilder()
                .where(WeekDao.Properties.StartDayDate.eq(date),
                        WeekDao.Properties.UserId.eq(getCurrentUserId()))
                .unique();
    }

    @Override
    public List<Grade> getNewGrades() {
        return daoSession.getGradeDao().queryBuilder()
                .where(GradeDao.Properties.IsNew.eq(1))
                .list();
    }
}
