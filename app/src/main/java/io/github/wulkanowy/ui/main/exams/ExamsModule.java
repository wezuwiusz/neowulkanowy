package io.github.wulkanowy.ui.main.exams;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import io.github.wulkanowy.di.scopes.PerChildFragment;
import io.github.wulkanowy.di.scopes.PerFragment;
import io.github.wulkanowy.ui.base.BasePagerAdapter;
import io.github.wulkanowy.ui.main.exams.tab.ExamsTabFragment;
import io.github.wulkanowy.ui.main.exams.tab.ExamsTabModule;

@Module
public abstract class ExamsModule {

    @PerFragment
    @Binds
    abstract ExamsContract.Presenter provideExamsPresneter(ExamsPresenter examsPresenter);

    @Named("Exams")
    @PerFragment
    @Provides
    static BasePagerAdapter providePagerAdapter(ExamsFragment fragment) {
        return new BasePagerAdapter(fragment.getChildFragmentManager());
    }

    @PerChildFragment
    @ContributesAndroidInjector(modules = ExamsTabModule.class)
    abstract ExamsTabFragment bindExamsTabFragment();
}
