package io.github.wulkanowy.ui.modules.schoolandteachers

import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import io.github.wulkanowy.di.scopes.PerChildFragment
import io.github.wulkanowy.di.scopes.PerFragment
import io.github.wulkanowy.ui.base.BaseFragmentPagerAdapter
import io.github.wulkanowy.ui.modules.schoolandteachers.school.SchoolFragment
import io.github.wulkanowy.ui.modules.schoolandteachers.teacher.TeacherFragment

@Suppress("unused")
@Module(includes = [SchoolAndTeachersModule.Static::class])
abstract class SchoolAndTeachersModule {

    @Module
    object Static {

        @PerFragment
        @Provides
        fun provideSchoolAndTeachersAdapter(fragment: SchoolAndTeachersFragment) = BaseFragmentPagerAdapter(fragment.childFragmentManager)
    }

    @PerChildFragment
    @ContributesAndroidInjector
    abstract fun provideSchoolFragment(): SchoolFragment

    @PerChildFragment
    @ContributesAndroidInjector
    abstract fun provideTeacherFragment(): TeacherFragment
}
