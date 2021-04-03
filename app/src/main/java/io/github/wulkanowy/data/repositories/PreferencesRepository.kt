package io.github.wulkanowy.data.repositories

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.modules.grade.GradeAverageMode
import io.github.wulkanowy.ui.modules.grade.GradeSortingMode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesRepository @Inject constructor(
    private val sharedPref: SharedPreferences,
    @ApplicationContext val context: Context
) {
    val startMenuIndex: Int
        get() = getString(R.string.pref_key_start_menu, R.string.pref_default_startup).toInt()

    val isShowPresent: Boolean
        get() = getBoolean(
            R.string.pref_key_attendance_present,
            R.bool.pref_default_attendance_present
        )

    val gradeAverageMode: GradeAverageMode
        get() = GradeAverageMode.getByValue(
            getString(
                R.string.pref_key_grade_average_mode,
                R.string.pref_default_grade_average_mode
            )
        )

    val gradeAverageForceCalc: Boolean
        get() = getBoolean(
            R.string.pref_key_grade_average_force_calc,
            R.bool.pref_default_grade_average_force_calc
        )

    val isGradeExpandable: Boolean
        get() = !getBoolean(R.string.pref_key_expand_grade, R.bool.pref_default_expand_grade)

    val showAllSubjectsOnStatisticsList: Boolean
        get() = getBoolean(
            R.string.pref_key_grade_statistics_list,
            R.bool.pref_default_grade_statistics_list
        )

    val appThemeKey = context.getString(R.string.pref_key_app_theme)
    val appTheme: String
        get() = getString(appThemeKey, R.string.pref_default_app_theme)

    val gradeColorTheme: String
        get() = getString(
            R.string.pref_key_grade_color_scheme,
            R.string.pref_default_grade_color_scheme
        )

    val appLanguageKey = context.getString(R.string.pref_key_app_language)
    val appLanguage
        get() = getString(appLanguageKey, R.string.pref_default_app_language)

    val serviceEnableKey = context.getString(R.string.pref_key_services_enable)
    val isServiceEnabled: Boolean
        get() = getBoolean(serviceEnableKey, R.bool.pref_default_services_enable)

    val servicesIntervalKey = context.getString(R.string.pref_key_services_interval)
    val servicesInterval: Long
        get() = getString(servicesIntervalKey, R.string.pref_default_services_interval).toLong()

    val servicesOnlyWifiKey = context.getString(R.string.pref_key_services_wifi_only)
    val isServicesOnlyWifi: Boolean
        get() = getBoolean(servicesOnlyWifiKey, R.bool.pref_default_services_wifi_only)

    val notificationsEnableKey = context.getString(R.string.pref_key_notifications_enable)
    val isNotificationsEnable: Boolean
        get() = getBoolean(notificationsEnableKey, R.bool.pref_default_notifications_enable)

    val isUpcomingLessonsNotificationsEnableKey =
        context.getString(R.string.pref_key_notifications_upcoming_lessons_enable)
    val isUpcomingLessonsNotificationsEnable: Boolean
        get() = getBoolean(
            isUpcomingLessonsNotificationsEnableKey,
            R.bool.pref_default_notification_upcoming_lessons_enable
        )

    val isDebugNotificationEnableKey = context.getString(R.string.pref_key_notification_debug)
    val isDebugNotificationEnable: Boolean
        get() = getBoolean(isDebugNotificationEnableKey, R.bool.pref_default_notification_debug)

    val gradePlusModifier: Double
        get() = getString(
            R.string.pref_key_grade_modifier_plus,
            R.string.pref_default_grade_modifier_plus
        ).toDouble()

    val gradeMinusModifier: Double
        get() = getString(
            R.string.pref_key_grade_modifier_minus,
            R.string.pref_default_grade_modifier_minus
        ).toDouble()

    val fillMessageContent: Boolean
        get() = getBoolean(
            R.string.pref_key_fill_message_content,
            R.bool.pref_default_fill_message_content
        )

    val showGroupsInPlan: Boolean
        get() = getBoolean(
            R.string.pref_key_timetable_show_groups,
            R.bool.pref_default_timetable_show_groups
        )

    val showWholeClassPlan: String
        get() = getString(
            R.string.pref_key_timetable_show_whole_class,
            R.string.pref_default_timetable_show_whole_class
        )

    val gradeSortingMode: GradeSortingMode
        get() = GradeSortingMode.getByValue(
            getString(
                R.string.pref_key_grade_sorting_mode,
                R.string.pref_default_grade_sorting_mode
            )
        )

    val showTimetableTimers: Boolean
        get() = getBoolean(
            R.string.pref_key_timetable_show_timers,
            R.bool.pref_default_timetable_show_timers
        )

    var isHomeworkFullscreen: Boolean
        get() = getBoolean(
            R.string.pref_key_homework_fullscreen,
            R.bool.pref_default_homework_fullscreen
        )
        set(value) = sharedPref.edit().putBoolean("homework_fullscreen", value).apply()

    val showSubjectsWithoutGrades: Boolean
        get() = getBoolean(
            R.string.pref_key_subjects_without_grades,
            R.bool.pref_default_subjects_without_grades
        )

    val isOptionalArithmeticAverage: Boolean
        get() = getBoolean(
            R.string.pref_key_optional_arithmetic_average,
            R.bool.pref_default_optional_arithmetic_average
        )

    private fun getString(id: Int, default: Int) = getString(context.getString(id), default)

    private fun getString(id: String, default: Int) =
        sharedPref.getString(id, context.getString(default)) ?: context.getString(default)

    private fun getBoolean(id: Int, default: Int) = getBoolean(context.getString(id), default)

    private fun getBoolean(id: String, default: Int) =
        sharedPref.getBoolean(id, context.resources.getBoolean(default))
}
