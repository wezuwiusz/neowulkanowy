package io.github.wulkanowy.data.db.shared;

import javax.inject.Singleton;

@Singleton
public interface SharedPrefContract {

    long getCurrentUserId();

    boolean isUserLoggedIn();

    void setCurrentUserId(long userId);

    void setTimetableWidgetState(boolean nextDay);

    boolean getTimetableWidgetState();

    int getStartupTab();

    boolean isShowGradesSummary();

    boolean isShowAttendancePresent();

    int getCurrentTheme();

    int getServicesInterval();

    boolean isMobileDisable();

    boolean isServicesEnable();

    boolean isNotifyEnable();

    void cleanSharedPref();
}
