package io.github.wulkanowy.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.CalendarContract
import android.provider.Settings
import io.github.wulkanowy.BuildConfig
import timber.log.Timber
import java.time.LocalDateTime
import java.time.ZoneId

fun Context.openInternetBrowser(uri: String, onActivityNotFound: (uri: String) -> Unit = {}) {
    Intent.parseUri(uri, 0).let {
        try {
            startActivity(it)
        } catch (e: ActivityNotFoundException) {
            onActivityNotFound(uri)
        }
    }
}

fun Context.openAppInMarket(onActivityNotFound: (uri: String) -> Unit) {
    openInternetBrowser("market://details?id=${BuildConfig.APPLICATION_ID}") {
        openInternetBrowser("https://github.com/wulkanowy/wulkanowy/releases", onActivityNotFound)
    }
}

fun Context.openEmailClient(
    chooserTitle: String,
    email: String,
    subject: String,
    body: String,
    onActivityNotFound: () -> Unit = {}
) {
    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:")).apply {
        putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
    }

    if (intent.resolveActivity(packageManager) != null) {
        startActivity(Intent.createChooser(intent, chooserTitle))
    } else onActivityNotFound()
}

fun Context.openCalendarEventAdd(
    title: String,
    description: String,
    start: LocalDateTime,
    end: LocalDateTime? = null,
    isAllDay: Boolean = false,
    onActivityNotFound: (uri: String?) -> Unit = {},
) {
    val beginTime = start.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val endTime = end?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()

    val intent = Intent(Intent.ACTION_INSERT)
        .setData(CalendarContract.Events.CONTENT_URI)
        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime)
        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime)
        .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, isAllDay)
        .putExtra(CalendarContract.Events.TITLE, title)
        .putExtra(CalendarContract.Events.DESCRIPTION, description)
        .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)

    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        onActivityNotFound(intent.dataString)
    }
}

fun Context.openNavigation(location: String) {
    val intentUri = Uri.parse("geo:0,0?q=${Uri.encode(location)}")
    val intent = Intent(Intent.ACTION_VIEW, intentUri)
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    }
}

fun Context.openDialer(phone: String) {
    val intentUri = Uri.parse("tel:$phone")
    val intent = Intent(Intent.ACTION_DIAL, intentUri)
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    }
}

fun Activity.openNotificationSettings() {
    val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra("android.provider.extra.APP_PACKAGE", packageName)
        }
    } else {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
    }
    try {
        startActivity(intent)
    } catch (e: Exception) {
        Timber.e(e)
    }
}

fun Context.shareText(text: String, subject: String?) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        if (subject != null) {
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    startActivity(shareIntent)
}
