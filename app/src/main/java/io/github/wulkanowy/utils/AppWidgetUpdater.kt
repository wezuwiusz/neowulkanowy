package io.github.wulkanowy.utils

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.reflect.KClass

class AppWidgetUpdater @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appWidgetManager: AppWidgetManager
) {

    fun updateAllAppWidgetsByProvider(providerClass: KClass<out BroadcastReceiver>) {
        try {
            val ids = appWidgetManager.getAppWidgetIds(ComponentName(context, providerClass.java))
            if (ids.isEmpty()) return

            val intent = Intent(context, providerClass.java)
                .apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                }

            context.sendBroadcast(intent)
        } catch (e: Exception) {
            Timber.e(e, "Failed to update all widgets for provider $providerClass")
        }
    }
}
