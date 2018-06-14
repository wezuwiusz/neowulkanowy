package io.github.wulkanowy.data.db.shared;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import io.github.wulkanowy.ui.main.settings.SettingsFragment;

@Singleton
public class SharedPrefRepository implements SharedPrefContract {

    private static final String SHARED_KEY_USER_ID = "USER_ID";

    private static final String SHARED_KEY_TIMETABLE_WIDGET_STATE = "TIMETABLE_WIDGET_STATE";

    private final SharedPreferences appSharedPref;

    private final SharedPreferences settingsSharedPref;

    @Inject
    SharedPrefRepository(Context context, @Named("sharedPrefName") String sharedName) {
        appSharedPref = context.getSharedPreferences(sharedName, Context.MODE_PRIVATE);
        settingsSharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public long getCurrentUserId() {
        return appSharedPref.getLong(SHARED_KEY_USER_ID, 0);
    }

    @Override
    public boolean isUserLoggedIn() {
        return getCurrentUserId() != 0;
    }

    @Override
    public void setCurrentUserId(long userId) {
        appSharedPref.edit().putLong(SHARED_KEY_USER_ID, userId).apply();
    }

    @SuppressLint("ApplySharedPref")
    @Override
    public void setTimetableWidgetState(boolean nextDay) {
        appSharedPref.edit().putBoolean(SHARED_KEY_TIMETABLE_WIDGET_STATE, nextDay).commit();
    }

    @Override
    public boolean getTimetableWidgetState() {
        return appSharedPref.getBoolean(SHARED_KEY_TIMETABLE_WIDGET_STATE, false);
    }

    @Override
    public int getStartupTab() {
        return Integer.parseInt(settingsSharedPref.getString(SettingsFragment.SHARED_KEY_START_TAB, "0"));
    }

    @Override
    public boolean isShowGradesSummary() {
        return settingsSharedPref.getBoolean(SettingsFragment.SHARED_KEY_GRADES_SUMMARY, false);
    }

    @Override
    public boolean isShowAttendancePresent() {
        return settingsSharedPref.getBoolean(SettingsFragment.SHARED_KEY_ATTENDANCE_PRESENT, false);
    }

    @Override
    public int getCurrentTheme() {
        return Integer.parseInt(settingsSharedPref.getString(SettingsFragment.SHARED_KEY_THEME, "1"));
    }

    @Override
    public int getServicesInterval() {
        return Integer.parseInt(settingsSharedPref.getString(SettingsFragment.SHARED_KEY_SERVICES_INTERVAL, "60"));
    }

    @Override
    public boolean isServicesEnable() {
        return settingsSharedPref.getBoolean(SettingsFragment.SHARED_KEY_SERVICES_ENABLE, true);
    }

    @Override
    public boolean isNotifyEnable() {
        return settingsSharedPref.getBoolean(SettingsFragment.SHARED_KEY_NOTIFY_ENABLE, true);
    }

    @Override
    public boolean isMobileDisable() {
        return settingsSharedPref.getBoolean(SettingsFragment.SHARED_KEY_SERVICES_MOBILE_DISABLED, false);
    }

    @Override
    public void cleanSharedPref() {
        appSharedPref.edit().clear().apply();
        settingsSharedPref.edit().clear().apply();
    }
}
