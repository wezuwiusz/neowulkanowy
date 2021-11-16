package io.github.wulkanowy.services.shortcuts

import android.content.Context
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.modules.Destination
import io.github.wulkanowy.ui.modules.splash.SplashActivity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShortcutsHelper @Inject constructor(@ApplicationContext private val context: Context) {

    private val destinations = mapOf(
        "grade" to Destination.Grade,
        "attendance" to Destination.Attendance,
        "exam" to Destination.Exam,
        "timetable" to Destination.Timetable()
    )

    init {
        initializeShortcuts()
    }

    fun getDestination(intent: Intent) =
        destinations[intent.getStringExtra(EXTRA_SHORTCUT_DESTINATION_ID)]

    private fun initializeShortcuts() {
        val shortcutsInfo = listOf(
            ShortcutInfoCompat.Builder(context, "grade_shortcut")
                .setShortLabel(context.getString(R.string.grade_title))
                .setLongLabel(context.getString(R.string.grade_title))
                .setIcon(IconCompat.createWithResource(context, R.drawable.ic_shortcut_grade))
                .setIntent(SplashActivity.getStartIntent(context)
                    .apply {
                        action = Intent.ACTION_VIEW
                        putExtra(EXTRA_SHORTCUT_DESTINATION_ID, "grade")
                    }
                )
                .build(),

            ShortcutInfoCompat.Builder(context, "attendance_shortcut")
                .setShortLabel(context.getString(R.string.attendance_title))
                .setLongLabel(context.getString(R.string.attendance_title))
                .setIcon(IconCompat.createWithResource(context, R.drawable.ic_shortcut_attendance))
                .setIntent(SplashActivity.getStartIntent(context)
                    .apply {
                        action = Intent.ACTION_VIEW
                        putExtra(EXTRA_SHORTCUT_DESTINATION_ID, "attendance")
                    }
                )
                .build(),

            ShortcutInfoCompat.Builder(context, "exam_shortcut")
                .setShortLabel(context.getString(R.string.exam_title))
                .setLongLabel(context.getString(R.string.exam_title))
                .setIcon(IconCompat.createWithResource(context, R.drawable.ic_shortcut_exam))
                .setIntent(SplashActivity.getStartIntent(context)
                    .apply {
                        action = Intent.ACTION_VIEW
                        putExtra(EXTRA_SHORTCUT_DESTINATION_ID, "exam")
                    }
                )
                .build(),

            ShortcutInfoCompat.Builder(context, "timetable_shortcut")
                .setShortLabel(context.getString(R.string.timetable_title))
                .setLongLabel(context.getString(R.string.timetable_title))
                .setIcon(IconCompat.createWithResource(context, R.drawable.ic_shortcut_timetable))
                .setIntent(SplashActivity.getStartIntent(context)
                    .apply {
                        action = Intent.ACTION_VIEW
                        putExtra(EXTRA_SHORTCUT_DESTINATION_ID, "timetable")
                    }
                )
                .build()
        )

        shortcutsInfo.forEach { ShortcutManagerCompat.pushDynamicShortcut(context, it) }
    }

    private companion object {

        private const val EXTRA_SHORTCUT_DESTINATION_ID = "shortcut_destination_id"
    }
}