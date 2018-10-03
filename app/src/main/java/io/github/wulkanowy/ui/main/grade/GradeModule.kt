package io.github.wulkanowy.ui.main.grade

import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import io.github.wulkanowy.di.scopes.PerChildFragment
import io.github.wulkanowy.di.scopes.PerFragment
import io.github.wulkanowy.ui.base.BasePagerAdapter
import io.github.wulkanowy.ui.main.grade.details.GradeDetailsFragment
import io.github.wulkanowy.ui.main.grade.summary.GradeSummaryFragment

@Module
abstract class GradeModule {

    @Module
    companion object {

        @JvmStatic
        @PerFragment
        @Provides
        fun provideGradePagerAdapter(fragment: GradeFragment) = BasePagerAdapter(fragment.childFragmentManager)
    }

    @PerChildFragment
    @ContributesAndroidInjector()
    abstract fun bindGradeDetailsFragment(): GradeDetailsFragment

    @PerChildFragment
    @ContributesAndroidInjector
    abstract fun binGradeSummaryFragment(): GradeSummaryFragment
}

