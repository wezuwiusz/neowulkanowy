package io.github.wulkanowy.di.component;

import dagger.Component;
import io.github.wulkanowy.di.annotations.PerFragment;
import io.github.wulkanowy.di.modules.FragmentModule;
import io.github.wulkanowy.ui.main.attendance.AttendanceFragment;
import io.github.wulkanowy.ui.main.attendance.AttendanceTabFragment;
import io.github.wulkanowy.ui.main.dashboard.DashboardFragment;
import io.github.wulkanowy.ui.main.grades.GradesFragment;
import io.github.wulkanowy.ui.main.timetable.TimetableFragment;
import io.github.wulkanowy.ui.main.timetable.TimetableTabFragment;

@PerFragment
@Component(dependencies = ApplicationComponent.class, modules = FragmentModule.class)
public interface FragmentComponent {

    void inject(GradesFragment gradesFragment);

    void inject(AttendanceFragment attendanceFragment);

    void inject(AttendanceTabFragment attendanceTabFragment);

    void inject(DashboardFragment dashboardFragment);

    void inject(TimetableFragment timetableFragment);

    void inject(TimetableTabFragment timetableTabFragment);
}
