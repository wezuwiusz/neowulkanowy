package io.github.wulkanowy.ui.main.timetable.tab;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import io.github.wulkanowy.di.scopes.PerChildFragment;

@Module
public abstract class TimetableTabModule {

    @PerChildFragment
    @Binds
    abstract TimetableTabContract.Presenter provideTimetableTabPresneter(TimetableTabPresenter timetableTabPresenter);

    @PerChildFragment
    @Provides
    static FlexibleAdapter<TimetableHeaderItem> provideTimetableAdapter() {
        return new FlexibleAdapter<>(null);
    }
}
