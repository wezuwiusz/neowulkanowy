package io.github.wulkanowy.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Rect
import android.graphics.Typeface
import android.net.Uri
import android.text.TextPaint
import android.util.DisplayMetrics.DENSITY_DEFAULT
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.graphics.drawable.toBitmap
import io.github.wulkanowy.BuildConfig.APPLICATION_ID

@ColorInt
fun Context.getThemeAttrColor(@AttrRes colorAttr: Int): Int {
    val array = obtainStyledAttributes(null, intArrayOf(colorAttr))
    return try {
        array.getColor(0, 0)
    } finally {
        array.recycle()
    }
}

@ColorInt
fun Context.getThemeAttrColor(@AttrRes colorAttr: Int, alpha: Int): Int {
    return ColorUtils.setAlphaComponent(getThemeAttrColor(colorAttr), alpha)
}

@ColorInt
fun Context.getCompatColor(@ColorRes colorRes: Int) = ContextCompat.getColor(this, colorRes)

fun Context.getCompatDrawable(@DrawableRes drawableRes: Int) =
    ContextCompat.getDrawable(this, drawableRes)

fun Context.getCompatDrawable(@DrawableRes drawableRes: Int, @ColorRes colorRes: Int) =
    getCompatDrawable(drawableRes)?.mutate()?.apply {
        colorFilter = PorterDuffColorFilter(
            getCompatColor(colorRes), PorterDuff.Mode.MULTIPLY
        )
    }

fun Context.getCompatBitmap(@DrawableRes drawableRes: Int, @ColorRes colorRes: Int) =
    getCompatDrawable(drawableRes, colorRes)?.toBitmap()

fun Context.openInternetBrowser(uri: String, onActivityNotFound: (uri: String) -> Unit) {
    Intent.parseUri(uri, 0).let {
        if (it.resolveActivity(packageManager) != null) startActivity(it)
        else onActivityNotFound(uri)
    }
}

fun Context.openAppInMarket(onActivityNotFound: (uri: String) -> Unit) {
    openInternetBrowser("market://details?id=${APPLICATION_ID}") {
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
    startActivity(intent)
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

fun Context.dpToPx(dp: Float) = dp * resources.displayMetrics.densityDpi / DENSITY_DEFAULT

@SuppressLint("DefaultLocale")
fun Context.createNameInitialsDrawable(
    text: String,
    backgroundColor: Long,
    scaleFactory: Float = 1f
): RoundedBitmapDrawable {
    val words = text.split(" ")
    val firstCharFirstWord = words.getOrNull(0)?.firstOrNull() ?: ""
    val firstCharSecondWord = words.getOrNull(1)?.firstOrNull() ?: ""

    val initials = "$firstCharFirstWord$firstCharSecondWord".uppercase()

    val bounds = Rect()
    val dimension = this.dpToPx(64f * scaleFactory).toInt()
    val textPaint = TextPaint().apply {
        typeface = Typeface.SANS_SERIF
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        textSize = this@createNameInitialsDrawable.dpToPx(30f * scaleFactory)
        getTextBounds(initials, 0, initials.length, bounds)
    }

    val xCoordinate = (dimension / 2).toFloat()
    val yCoordinate = (dimension / 2 + (bounds.bottom - bounds.top) / 2).toFloat()

    val bitmap = Bitmap.createBitmap(dimension, dimension, Bitmap.Config.ARGB_8888)
        .applyCanvas {
            drawColor(backgroundColor.toInt())
            drawText(initials, 0, initials.length, xCoordinate, yCoordinate, textPaint)
        }

    return RoundedBitmapDrawableFactory.create(this.resources, bitmap)
        .apply { isCircular = true }
}
