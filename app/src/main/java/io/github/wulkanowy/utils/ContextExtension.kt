package io.github.wulkanowy.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.DisplayMetrics.DENSITY_DEFAULT
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.graphics.drawable.toBitmap


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

fun Context.getPlural(@PluralsRes pluralRes: Int, quantity: Int, vararg arguments: Any) =
    resources.getQuantityString(pluralRes, quantity, *arguments)

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
