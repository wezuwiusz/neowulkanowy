package io.github.wulkanowy.data

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.wulkanowy.data.api.services.SchoolsService
import io.github.wulkanowy.data.api.services.WulkanowyService
import io.github.wulkanowy.data.db.AppDatabase
import io.github.wulkanowy.data.db.SharedPrefProvider
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.utils.AppInfo
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class DataModule {

    @Singleton
    @Provides
    fun provideChuckerCollector(
        @ApplicationContext context: Context,
        prefRepository: PreferencesRepository
    ) = ChuckerCollector(
        context = context,
        showNotification = prefRepository.isDebugNotificationEnable,
        retentionPeriod = RetentionManager.Period.ONE_HOUR
    )

    @Singleton
    @Provides
    fun provideChuckerInterceptor(
        @ApplicationContext context: Context,
        chuckerCollector: ChuckerCollector
    ) = ChuckerInterceptor.Builder(context)
        .collector(chuckerCollector)
        .alwaysReadResponseBody(true)
        .build()

    @Singleton
    @Provides
    fun provideOkHttpClient(chuckerInterceptor: ChuckerInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addNetworkInterceptor(chuckerInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

    @Singleton
    @Provides
    fun provideAdminMessageService(
        okHttpClient: OkHttpClient,
        json: Json,
        appInfo: AppInfo
    ): WulkanowyService = Retrofit.Builder()
        .baseUrl(appInfo.messagesBaseUrl)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create()

    @Singleton
    @Provides
    fun provideSchoolsService(
        okHttpClient: OkHttpClient,
        json: Json,
        appInfo: AppInfo,
    ): SchoolsService = Retrofit.Builder()
        .baseUrl(appInfo.schoolsBaseUrl)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create()

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context,
        sharedPrefProvider: SharedPrefProvider,
        appInfo: AppInfo
    ) = AppDatabase.newInstance(context, sharedPrefProvider, appInfo)

    @Singleton
    @Provides
    fun provideSharedPref(@ApplicationContext context: Context): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    @Singleton
    @Provides
    fun provideFlowSharedPref(sharedPreferences: SharedPreferences) =
        FlowSharedPreferences(sharedPreferences)

    @Singleton
    @Provides
    fun provideJson() = Json {
        ignoreUnknownKeys = true
    }

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
    fun provideMailboxesDao(database: AppDatabase) = database.mailboxDao

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

    @Singleton
    @Provides
    fun provideSchoolAnnouncementDao(database: AppDatabase) = database.schoolAnnouncementDao

    @Singleton
    @Provides
    fun provideNotificationDao(database: AppDatabase) = database.notificationDao

    @Singleton
    @Provides
    fun provideAdminMessageDao(database: AppDatabase) = database.adminMessagesDao

    @Singleton
    @Provides
    fun provideMutesDao(database: AppDatabase) = database.mutedMessageSendersDao

    @Singleton
    @Provides
    fun provideGradeDescriptiveDao(database: AppDatabase) = database.gradeDescriptiveDao
}
