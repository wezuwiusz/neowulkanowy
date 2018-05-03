package io.github.wulkanowy.di.modules;

import android.support.v4.app.Fragment;

import dagger.Module;
import dagger.Provides;
import eu.davidea.flexibleadapter.FlexibleAdapter;
import io.github.wulkanowy.di.annotations.PerFragment;
import io.github.wulkanowy.ui.base.BasePagerAdapter;
import io.github.wulkanowy.ui.main.attendance.AttendanceContract;
import io.github.wulkanowy.ui.main.attendance.AttendanceHeaderItem;
import io.github.wulkanowy.ui.main.attendance.AttendancePresenter;
import io.github.wulkanowy.ui.main.attendance.AttendanceTabContract;
import io.github.wulkanowy.ui.main.attendance.AttendanceTabPresenter;
import io.github.wulkanowy.ui.main.exams.ExamsContract;
import io.github.wulkanowy.ui.main.exams.ExamsPresenter;
import io.github.wulkanowy.ui.main.exams.ExamsSubItem;
import io.github.wulkanowy.ui.main.exams.ExamsTabContract;
import io.github.wulkanowy.ui.main.exams.ExamsTabPresenter;
import io.github.wulkanowy.ui.main.grades.GradeHeaderItem;
import io.github.wulkanowy.ui.main.grades.GradesContract;
import io.github.wulkanowy.ui.main.grades.GradesPresenter;
import io.github.wulkanowy.ui.main.timetable.TimetableContract;
import io.github.wulkanowy.ui.main.timetable.TimetableHeaderItem;
import io.github.wulkanowy.ui.main.timetable.TimetablePresenter;
import io.github.wulkanowy.ui.main.timetable.TimetableTabContract;
import io.github.wulkanowy.ui.main.timetable.TimetableTabPresenter;

@Module
public class FragmentModule {

    private final Fragment fragment;

    public FragmentModule(Fragment fragment) {
        this.fragment = fragment;
    }

    @PerFragment
    @Provides
    GradesContract.Presenter provideGradesPresenter(GradesPresenter gradesPresenter) {
        return gradesPresenter;
    }

    @PerFragment
    @Provides
    AttendanceContract.Presenter provideAttendancePresenter(AttendancePresenter attendancePresenter) {
        return attendancePresenter;
    }

    @PerFragment
    @Provides
    ExamsContract.Presenter provideDashboardPresenter(ExamsPresenter examsPresenter) {
        return examsPresenter;
    }

    @PerFragment
    @Provides
    AttendanceTabContract.Presenter provideAttendanceTabPresenter(AttendanceTabPresenter timetableTabPresenter) {
        return timetableTabPresenter;
    }

    @Provides
    BasePagerAdapter provideBasePagerAdapter() {
        return new BasePagerAdapter(fragment.getChildFragmentManager());
    }

    @Provides
    FlexibleAdapter<AttendanceHeaderItem> provideAttendanceTabAdapter() {
        return new FlexibleAdapter<>(null);
    }

    @Provides
    FlexibleAdapter<TimetableHeaderItem> provideTimetableTabAdapter() {
        return new FlexibleAdapter<>(null);
    }

    @Provides
    FlexibleAdapter<GradeHeaderItem> provideGradesAdapter() {
        return new FlexibleAdapter<>(null);
    }

    @Provides
    FlexibleAdapter<ExamsSubItem> provideExamAdapter() {
        return new FlexibleAdapter<>(null);
    }

    @PerFragment
    @Provides
    TimetableContract.Presenter provideTimetablePresenter(TimetablePresenter timetablePresenter) {
        return timetablePresenter;
    }

    @PerFragment
    @Provides
    TimetableTabContract.Presenter provideTimetableTabPresenter(TimetableTabPresenter timetableTabPresenter) {
        return timetableTabPresenter;
    }

    @Provides
    ExamsTabContract.Presenter provideExamsTabPresenter(ExamsTabPresenter examsTabPresenter) {
        return examsTabPresenter;
    }
}
