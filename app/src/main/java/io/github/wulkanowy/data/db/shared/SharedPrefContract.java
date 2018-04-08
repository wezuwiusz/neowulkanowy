package io.github.wulkanowy.data.db.shared;

public interface SharedPrefContract {

    long getCurrentUserId();

    void setCurrentUserId(long userId);

    int getStartupTab();

    int getServicesInterval();

    boolean isMobileDisable();

    boolean isServicesEnable();

    boolean isNotifyEnable();
}
