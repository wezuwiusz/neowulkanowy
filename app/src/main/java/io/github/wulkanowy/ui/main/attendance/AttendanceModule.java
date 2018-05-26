package io.github.wulkanowy.ui.main.attendance;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import io.github.wulkanowy.di.scopes.PerChildFragment;
import io.github.wulkanowy.di.scopes.PerFragment;
import io.github.wulkanowy.ui.base.BasePagerAdapter;
import io.github.wulkanowy.ui.main.attendance.tab.AttendanceTabFragment;
import io.github.wulkanowy.ui.main.attendance.tab.AttendanceTabModule;

@Module
public abstract class AttendanceModule {

    @PerFragment
    @Binds
    abstract AttendanceContract.Presenter provideAttendancePresenter(AttendancePresenter attendancePresenter);

    @PerFragment
    @Named("Attendance")
    @Provides
    static BasePagerAdapter providePagerAdapter(AttendanceFragment fragment) {
        return new BasePagerAdapter(fragment.getChildFragmentManager());
    }

    @PerChildFragment
    @ContributesAndroidInjector(modules = AttendanceTabModule.class)
    abstract AttendanceTabFragment bindAttendanceTabFragment();
}
