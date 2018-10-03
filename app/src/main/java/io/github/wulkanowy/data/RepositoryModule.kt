package io.github.wulkanowy.data

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.preference.PreferenceManager
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.strategy.SocketInternetObservingStrategy
import dagger.Module
import dagger.Provides
import io.github.wulkanowy.api.Api
import io.github.wulkanowy.data.db.AppDatabase
import javax.inject.Singleton

@Module
internal class RepositoryModule {

    @Singleton
    @Provides
    fun provideInternetObservingSettings(): InternetObservingSettings {
        return InternetObservingSettings
                .strategy(SocketInternetObservingStrategy())
                .host("www.google.com")
                .build()
    }

    @Singleton
    @Provides
    fun provideApi() = Api()

    @Singleton
    @Provides
    fun provideDatabase(context: Context) = AppDatabase.newInstance(context)

    @Provides
    fun provideErrorHandler(context: Context) = ErrorHandler(context.resources)

    @Singleton
    @Provides
    fun provideSharedPref(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    @Singleton
    @Provides
    fun provideStudentDao(database: AppDatabase) = database.studentDao()

    @Singleton
    @Provides
    fun provideSemesterDao(database: AppDatabase) = database.semesterDao()

    @Singleton
    @Provides
    fun provideGradeDao(database: AppDatabase) = database.gradeDao()

    @Singleton
    @Provides
    fun provideGradeSummaryDao(database: AppDatabase) = database.gradeSummaryDao()

    @Singleton
    @Provides
    fun provideExamDao(database: AppDatabase) = database.examsDao()

    @Singleton
    @Provides
    fun provideAttendanceDao(database: AppDatabase) = database.attendanceDao()
}
