package io.github.wulkanowy.data.repositories

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.fredporciuncula.flow.preferences.Preference
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.R
import io.github.wulkanowy.sdk.toLocalDate
import io.github.wulkanowy.ui.modules.dashboard.DashboardItem
import io.github.wulkanowy.ui.modules.grade.GradeAverageMode
import io.github.wulkanowy.ui.modules.grade.GradeSortingMode
import io.github.wulkanowy.utils.toLocalDateTime
import io.github.wulkanowy.utils.toTimestamp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class PreferencesRepository @Inject constructor(
    @ApplicationContext val context: Context,
    private val sharedPref: SharedPreferences,
    private val flowSharedPref: FlowSharedPreferences,
    private val json: Json,
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

    val isUpcomingLessonsNotificationsPersistentKey =
        context.getString(R.string.pref_key_notifications_upcoming_lessons_persistent)
    val isUpcomingLessonsNotificationsPersistent: Boolean
        get() = getBoolean(
            isUpcomingLessonsNotificationsPersistentKey,
            R.bool.pref_default_notification_upcoming_lessons_persistent
        )

    val isNotificationPiggybackEnabledKey =
        context.getString(R.string.pref_key_notifications_piggyback)
    val isNotificationPiggybackEnabled: Boolean
        get() = getBoolean(
            R.string.pref_key_notifications_piggyback,
            R.bool.pref_default_notification_piggyback
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

    var lasSyncDate: LocalDateTime
        get() = getLong(R.string.pref_key_last_sync_date, R.string.pref_default_last_sync_date)
            .toLocalDateTime()
        set(value) = sharedPref.edit().putLong("last_sync_date", value.toTimestamp()).apply()

    var dashboardItemsPosition: Map<DashboardItem.Type, Int>?
        get() {
            val value = sharedPref.getString(PREF_KEY_DASHBOARD_ITEMS_POSITION, null) ?: return null

            return json.decodeFromString(value)
        }
        set(value) = sharedPref.edit {
            putString(
                PREF_KEY_DASHBOARD_ITEMS_POSITION,
                json.encodeToString(value)
            )
        }

    val selectedDashboardTilesFlow: Flow<Set<DashboardItem.Tile>>
        get() = selectedDashboardTilesPreference.asFlow()
            .map { set ->
                set.map { DashboardItem.Tile.valueOf(it) }
                    .plus(DashboardItem.Tile.ACCOUNT)
                    .toSet()
            }

    var selectedDashboardTiles: Set<DashboardItem.Tile>
        get() = selectedDashboardTilesPreference.get()
            .map { DashboardItem.Tile.valueOf(it) }
            .plus(DashboardItem.Tile.ACCOUNT)
            .toSet()
        set(value) {
            val filteredValue = value.filterNot { it == DashboardItem.Tile.ACCOUNT }
                .map { it.name }
                .toSet()

            selectedDashboardTilesPreference.set(filteredValue)
        }

    private val selectedDashboardTilesPreference: Preference<Set<String>>
        get() {
            val defaultSet =
                context.resources.getStringArray(R.array.pref_default_dashboard_tiles).toSet()
            val prefKey = context.getString(R.string.pref_key_dashboard_tiles)

            return flowSharedPref.getStringSet(prefKey, defaultSet)
        }

    var inAppReviewCount: Int
        get() = sharedPref.getInt(PREF_KEY_IN_APP_REVIEW_COUNT, 0)
        set(value) = sharedPref.edit().putInt(PREF_KEY_IN_APP_REVIEW_COUNT, value).apply()

    var inAppReviewDate: LocalDate?
        get() = sharedPref.getLong(PREF_KEY_IN_APP_REVIEW_DATE, 0).takeIf { it != 0L }
            ?.toLocalDate()
        set(value) = sharedPref.edit().putLong(PREF_KEY_IN_APP_REVIEW_DATE, value!!.toTimestamp())
            .apply()

    var isAppReviewDone: Boolean
        get() = sharedPref.getBoolean(PREF_KEY_IN_APP_REVIEW_DONE, false)
        set(value) = sharedPref.edit().putBoolean(PREF_KEY_IN_APP_REVIEW_DONE, value).apply()

    private fun getLong(id: Int, default: Int) = getLong(context.getString(id), default)

    private fun getLong(id: String, default: Int) =
        sharedPref.getLong(id, context.resources.getString(default).toLong())

    private fun getString(id: Int, default: Int) = getString(context.getString(id), default)

    private fun getString(id: String, default: Int) =
        sharedPref.getString(id, context.getString(default)) ?: context.getString(default)

    private fun getBoolean(id: Int, default: Int) = getBoolean(context.getString(id), default)

    private fun getBoolean(id: String, default: Int) =
        sharedPref.getBoolean(id, context.resources.getBoolean(default))

    private companion object {

        private const val PREF_KEY_DASHBOARD_ITEMS_POSITION = "dashboard_items_position"

        private const val PREF_KEY_IN_APP_REVIEW_COUNT = "in_app_review_count"

        private const val PREF_KEY_IN_APP_REVIEW_DATE = "in_app_review_date"

        private const val PREF_KEY_IN_APP_REVIEW_DONE = "in_app_review_done"
    }
}
