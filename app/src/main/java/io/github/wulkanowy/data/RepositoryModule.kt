package io.github.wulkanowy.data

import android.arch.persistence.room.Room
import android.content.Context
import android.content.SharedPreferences
import android.support.v7.preference.PreferenceManager
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import dagger.Module
import dagger.Provides
import io.github.wulkanowy.api.Api
import io.github.wulkanowy.data.db.AppDatabase
import io.github.wulkanowy.utils.DATABASE_NAME
import javax.inject.Singleton

@Module
internal class RepositoryModule {

    @Singleton
    @Provides
    fun provideInternetObservingSettings(): InternetObservingSettings {
        return InternetObservingSettings.create()
    }

    @Singleton
    @Provides
    fun provideApi() = Api()

    @Singleton
    @Provides
    fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .build()
    }

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
    fun provideExamDao(database: AppDatabase) = database.examsDao()
}
