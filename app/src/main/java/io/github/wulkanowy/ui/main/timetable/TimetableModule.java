package io.github.wulkanowy.ui.main.timetable;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import io.github.wulkanowy.di.scopes.PerChildFragment;
import io.github.wulkanowy.di.scopes.PerFragment;
import io.github.wulkanowy.ui.base.BasePagerAdapter;
import io.github.wulkanowy.ui.main.timetable.tab.TimetableTabFragment;
import io.github.wulkanowy.ui.main.timetable.tab.TimetableTabModule;

@Module
public abstract class TimetableModule {

    @Named("Timetable")
    @PerFragment
    @Provides
    static BasePagerAdapter providePagerAdapter(TimetableFragment fragment) {
        return new BasePagerAdapter(fragment.getChildFragmentManager());
    }

    @PerFragment
    @Binds
    abstract TimetableContract.Presenter provideTimetablePresenter(TimetablePresenter timetablePresenter);

    @PerChildFragment
    @ContributesAndroidInjector(modules = TimetableTabModule.class)
    abstract TimetableTabFragment bindTimetableTabFragment();
}
