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

    fun initializeShortcuts() {
        val shortcutsInfo = listOf(
            ShortcutInfoCompat.Builder(context, "grade_shortcut")
                .setShortLabel(context.getString(R.string.grade_title))
                .setLongLabel(context.getString(R.string.grade_title))
                .setIcon(IconCompat.createWithResource(context, R.drawable.ic_shortcut_grade))
                .setIntent(SplashActivity.getStartIntent(context, Destination.Grade)
                    .apply { action = Intent.ACTION_VIEW })
                .build(),

            ShortcutInfoCompat.Builder(context, "attendance_shortcut")
                .setShortLabel(context.getString(R.string.attendance_title))
                .setLongLabel(context.getString(R.string.attendance_title))
                .setIcon(IconCompat.createWithResource(context, R.drawable.ic_shortcut_attendance))
                .setIntent(SplashActivity.getStartIntent(context, Destination.Attendance)
                    .apply { action = Intent.ACTION_VIEW })
                .build(),

            ShortcutInfoCompat.Builder(context, "exam_shortcut")
                .setShortLabel(context.getString(R.string.exam_title))
                .setLongLabel(context.getString(R.string.exam_title))
                .setIcon(IconCompat.createWithResource(context, R.drawable.ic_shortcut_exam))
                .setIntent(SplashActivity.getStartIntent(context, Destination.Exam)
                    .apply { action = Intent.ACTION_VIEW })
                .build(),

            ShortcutInfoCompat.Builder(context, "timetable_shortcut")
                .setShortLabel(context.getString(R.string.timetable_title))
                .setLongLabel(context.getString(R.string.timetable_title))
                .setIcon(IconCompat.createWithResource(context, R.drawable.ic_shortcut_timetable))
                .setIntent(SplashActivity.getStartIntent(context, Destination.Timetable())
                    .apply { action = Intent.ACTION_VIEW })
                .build()
        )

        shortcutsInfo.forEach { ShortcutManagerCompat.pushDynamicShortcut(context, it) }
    }
}
