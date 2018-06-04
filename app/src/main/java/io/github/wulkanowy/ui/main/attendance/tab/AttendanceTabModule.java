package io.github.wulkanowy.ui.main.attendance.tab;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import io.github.wulkanowy.di.scopes.PerChildFragment;

@Module
public abstract class AttendanceTabModule {

    @PerChildFragment
    @Binds
    abstract AttendanceTabContract.Presenter provideAttendanceTabPresenter(AttendanceTabPresenter attendanceTabPresenter);

    @PerChildFragment
    @Provides
    static FlexibleAdapter<AttendanceHeader> provideAdapter() {
        return new FlexibleAdapter<>(null);
    }
}
