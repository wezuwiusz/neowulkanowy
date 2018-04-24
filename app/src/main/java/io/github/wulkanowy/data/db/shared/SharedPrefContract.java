package io.github.wulkanowy.data.db.shared;

public interface SharedPrefContract {

    long getCurrentUserId();

    void setCurrentUserId(long userId);

    void setTimetableWidgetState(boolean nextDay);

    boolean getTimetableWidgetState();

    int getStartupTab();

    int getServicesInterval();

    boolean isMobileDisable();

    boolean isServicesEnable();

    boolean isNotifyEnable();
}
