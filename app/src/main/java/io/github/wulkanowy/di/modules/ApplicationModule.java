package io.github.wulkanowy.di.modules;

import android.app.Application;
import android.content.Context;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.github.wulkanowy.api.Vulcan;
import io.github.wulkanowy.data.Repository;
import io.github.wulkanowy.data.RepositoryContract;
import io.github.wulkanowy.data.db.dao.DbHelper;
import io.github.wulkanowy.data.db.dao.entities.DaoMaster;
import io.github.wulkanowy.data.db.dao.entities.DaoSession;
import io.github.wulkanowy.data.db.resources.AppResources;
import io.github.wulkanowy.data.db.resources.ResourcesContract;
import io.github.wulkanowy.data.db.shared.SharedPref;
import io.github.wulkanowy.data.db.shared.SharedPrefContract;
import io.github.wulkanowy.data.sync.SyncContract;
import io.github.wulkanowy.data.sync.attendance.AttendanceSync;
import io.github.wulkanowy.data.sync.attendance.AttendanceSyncContract;
import io.github.wulkanowy.data.sync.grades.GradeSync;
import io.github.wulkanowy.data.sync.login.LoginSync;
import io.github.wulkanowy.data.sync.login.LoginSyncContract;
import io.github.wulkanowy.data.sync.subjects.SubjectSync;
import io.github.wulkanowy.data.sync.timetable.TimetableSync;
import io.github.wulkanowy.data.sync.timetable.TimetableSyncContract;
import io.github.wulkanowy.di.annotations.ApplicationContext;
import io.github.wulkanowy.di.annotations.DatabaseInfo;
import io.github.wulkanowy.di.annotations.SharedPreferencesInfo;
import io.github.wulkanowy.di.annotations.SyncGrades;
import io.github.wulkanowy.di.annotations.SyncSubjects;
import io.github.wulkanowy.utils.AppConstant;

@Module
public class ApplicationModule {

    private final Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Provides
    Application provideApplication() {
        return application;
    }

    @ApplicationContext
    @Provides
    Context provideAppContext() {
        return application;
    }

    @DatabaseInfo
    @Provides
    String provideDatabaseName() {
        return AppConstant.DATABASE_NAME;
    }

    @SharedPreferencesInfo
    @Provides
    String provideSharedPreferencesName() {
        return AppConstant.SHARED_PREFERENCES_NAME;
    }

    @Singleton
    @Provides
    DaoSession provideDaoSession(DbHelper dbHelper) {
        return new DaoMaster(dbHelper.getWritableDb()).newSession();
    }

    @Singleton
    @Provides
    Vulcan provideVulcan() {
        return new Vulcan();
    }

    @Singleton
    @Provides
    RepositoryContract provideRepository(Repository repository) {
        return repository;
    }

    @Singleton
    @Provides
    SharedPrefContract provideSharedPref(SharedPref sharedPref) {
        return sharedPref;
    }

    @Singleton
    @Provides
    ResourcesContract provideAppResources(AppResources appResources) {
        return appResources;
    }

    @Singleton
    @Provides
    LoginSyncContract provideLoginSync(LoginSync loginSync) {
        return loginSync;
    }

    @SyncGrades
    @Singleton
    @Provides
    SyncContract provideGradesSync(GradeSync gradeSync) {
        return gradeSync;
    }

    @SyncSubjects
    @Singleton
    @Provides
    SyncContract provideSubjectSync(SubjectSync subjectSync) {
        return subjectSync;
    }

    @Singleton
    @Provides
    TimetableSyncContract provideTimetableSync(TimetableSync timetableSync) {
        return timetableSync;
    }

    @Singleton
    @Provides
    AttendanceSyncContract provideAttendanceSync(AttendanceSync attendanceSync) {
        return attendanceSync;
    }

    @Provides
    FirebaseJobDispatcher provideDispatcher() {
        return new FirebaseJobDispatcher(new GooglePlayDriver(application));
    }
}
