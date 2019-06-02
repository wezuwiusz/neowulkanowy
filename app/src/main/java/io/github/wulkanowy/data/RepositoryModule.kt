package io.github.wulkanowy.data

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.preference.PreferenceManager
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.strategy.WalledGardenInternetObservingStrategy
import com.readystatesoftware.chuck.api.ChuckCollector
import com.readystatesoftware.chuck.api.ChuckInterceptor
import com.readystatesoftware.chuck.api.RetentionManager
import dagger.Module
import dagger.Provides
import io.github.wulkanowy.api.Api
import io.github.wulkanowy.data.db.AppDatabase
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BASIC
import okhttp3.logging.HttpLoggingInterceptor.Level.NONE
import timber.log.Timber
import javax.inject.Singleton

@Module
internal class RepositoryModule {

    @Singleton
    @Provides
    fun provideInternetObservingSettings(): InternetObservingSettings {
        return InternetObservingSettings.builder()
            .strategy(WalledGardenInternetObservingStrategy())
            .build()
    }

    @Singleton
    @Provides
    fun provideApi(chuckCollector: ChuckCollector, context: Context): Api {
        return Api().apply {
            logLevel = NONE
            androidVersion = android.os.Build.VERSION.RELEASE
            buildTag = android.os.Build.MODEL
            setInterceptor(HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { Timber.d(it) }).setLevel(BASIC))

            // for debug only
            setInterceptor(ChuckInterceptor(context, chuckCollector).maxContentLength(250000L), true, 0)
        }
    }

    @Singleton
    @Provides
    fun provideChuckCollector(context: Context, prefRepository: PreferencesRepository): ChuckCollector {
        return ChuckCollector(context)
            .showNotification(prefRepository.isDebugNotificationEnable)
            .retentionManager(RetentionManager(context, ChuckCollector.Period.ONE_HOUR))
    }

    @Singleton
    @Provides
    fun provideDatabase(context: Context) = AppDatabase.newInstance(context)

    @Singleton
    @Provides
    fun provideResources(context: Context): Resources = context.resources

    @Singleton
    @Provides
    fun provideSharedPref(context: Context): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    @Singleton
    @Provides
    fun provideStudentDao(database: AppDatabase) = database.studentDao

    @Singleton
    @Provides
    fun provideSemesterDao(database: AppDatabase) = database.semesterDao

    @Singleton
    @Provides
    fun provideGradeDao(database: AppDatabase) = database.gradeDao

    @Singleton
    @Provides
    fun provideGradeSummaryDao(database: AppDatabase) = database.gradeSummaryDao

    @Singleton
    @Provides
    fun provideGradeStatisticsDao(database: AppDatabase) = database.gradeStatistics

    @Singleton
    @Provides
    fun provideMessagesDao(database: AppDatabase) = database.messagesDao

    @Singleton
    @Provides
    fun provideExamDao(database: AppDatabase) = database.examsDao

    @Singleton
    @Provides
    fun provideAttendanceDao(database: AppDatabase) = database.attendanceDao

    @Singleton
    @Provides
    fun provideAttendanceSummaryDao(database: AppDatabase) = database.attendanceSummaryDao

    @Singleton
    @Provides
    fun provideTimetableDao(database: AppDatabase) = database.timetableDao

    @Singleton
    @Provides
    fun provideNoteDao(database: AppDatabase) = database.noteDao

    @Singleton
    @Provides
    fun provideHomeworkDao(database: AppDatabase) = database.homeworkDao

    @Singleton
    @Provides
    fun provideSubjectDao(database: AppDatabase) = database.subjectDao

    @Singleton
    @Provides
    fun provideLuckyNumberDao(database: AppDatabase) = database.luckyNumberDao

    @Singleton
    @Provides
    fun provideCompletedLessonsDao(database: AppDatabase) = database.completedLessonsDao

    @Singleton
    @Provides
    fun provideReportingUnitDao(database: AppDatabase) = database.reportingUnitDao

    @Singleton
    @Provides
    fun provideRecipientDao(database: AppDatabase) = database.recipientDao

    @Singleton
    @Provides
    fun provideMobileDevicesDao(database: AppDatabase) = database.mobileDeviceDao
}
