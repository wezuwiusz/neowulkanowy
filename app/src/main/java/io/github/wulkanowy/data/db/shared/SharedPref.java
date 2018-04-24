package io.github.wulkanowy.data.db.shared;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.wulkanowy.di.annotations.ApplicationContext;
import io.github.wulkanowy.di.annotations.SharedPreferencesInfo;
import io.github.wulkanowy.ui.main.settings.SettingsFragment;

@Singleton
public class SharedPref implements SharedPrefContract {

    private static final String SHARED_KEY_USER_ID = "USER_ID";

    private static final String SHARED_KEY_TIMETABLE_WIDGET_STATE = "TIMETABLE_WIDGET_STATE";

    private final SharedPreferences appSharedPref;

    private final SharedPreferences settingsSharedPref;

    @Inject
    SharedPref(@ApplicationContext Context context, @SharedPreferencesInfo String sharedName) {
        appSharedPref = context.getSharedPreferences(sharedName, Context.MODE_PRIVATE);
        settingsSharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public long getCurrentUserId() {
        return appSharedPref.getLong(SHARED_KEY_USER_ID, 0);
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
        return Integer.parseInt(settingsSharedPref.getString(SettingsFragment.SHARED_KEY_START_TAB, "2"));
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
}
