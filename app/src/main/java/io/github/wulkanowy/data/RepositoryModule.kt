package io.github.wulkanowy.data

import android.content.Context
import android.content.SharedPreferences
import android.content.res.AssetManager
import android.content.res.Resources
import androidx.preference.PreferenceManager
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.wulkanowy.data.db.AppDatabase
import io.github.wulkanowy.data.db.SharedPrefProvider
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.AppInfo
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class RepositoryModule {

    @Singleton
    @Provides
    fun provideSdk(chuckerCollector: ChuckerCollector, @ApplicationContext context: Context): Sdk {
        return Sdk().apply {
            androidVersion = android.os.Build.VERSION.RELEASE
            buildTag = android.os.Build.MODEL
            setSimpleHttpLogger { Timber.d(it) }

            // for debug only
            addInterceptor(
                ChuckerInterceptor.Builder(context)
                    .collector(chuckerCollector)
                    .alwaysReadResponseBody(true)
                    .build(), network = true
            )
        }
    }

    @Singleton
    @Provides
    fun provideChuckerCollector(
        @ApplicationContext context: Context,
        prefRepository: PreferencesRepository
    ): ChuckerCollector {
        return ChuckerCollector(
            context = context,
            showNotification = prefRepository.isDebugNotificationEnable,
            retentionPeriod = RetentionManager.Period.ONE_HOUR
        )
    }

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context,
        sharedPrefProvider: SharedPrefProvider,
        appInfo: AppInfo
    ) = AppDatabase.newInstance(context, sharedPrefProvider, appInfo)

    @Singleton
    @Provides
    fun provideResources(@ApplicationContext context: Context): Resources = context.resources

    @Singleton
    @Provides
    fun provideAssets(@ApplicationContext context: Context): AssetManager = context.assets

    @Singleton
    @Provides
    fun provideSharedPref(@ApplicationContext context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

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
    fun provideGradePartialStatisticsDao(database: AppDatabase) = database.gradePartialStatisticsDao

    @Singleton
    @Provides
    fun provideGradeSemesterStatisticsDao(database: AppDatabase) =
        database.gradeSemesterStatisticsDao

    @Singleton
    @Provides
    fun provideGradePointsStatisticsDao(database: AppDatabase) = database.gradePointsStatisticsDao

    @Singleton
    @Provides
    fun provideMessagesDao(database: AppDatabase) = database.messagesDao

    @Singleton
    @Provides
    fun provideMessageAttachmentsDao(database: AppDatabase) = database.messageAttachmentDao

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

    @Singleton
    @Provides
    fun provideTeacherDao(database: AppDatabase) = database.teacherDao

    @Singleton
    @Provides
    fun provideSchoolInfoDao(database: AppDatabase) = database.schoolDao

    @Singleton
    @Provides
    fun provideConferenceDao(database: AppDatabase) = database.conferenceDao

    @Singleton
    @Provides
    fun provideTimetableAdditionalDao(database: AppDatabase) = database.timetableAdditionalDao

    @Singleton
    @Provides
    fun provideStudentInfoDao(database: AppDatabase) = database.studentInfoDao

    @Singleton
    @Provides
    fun provideTimetableHeaderDao(database: AppDatabase) = database.timetableHeaderDao
}
