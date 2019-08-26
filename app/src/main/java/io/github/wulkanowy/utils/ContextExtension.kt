package io.github.wulkanowy.utils

import android.content.Context
import android.content.Intent
import android.util.DisplayMetrics.DENSITY_DEFAULT
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

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
fun Context.getCompatColor(@ColorRes colorRes: Int) = ContextCompat.getColor(this, colorRes)

fun Context.getCompatDrawable(@DrawableRes drawableRes: Int) = ContextCompat.getDrawable(this, drawableRes)

fun Context.openInternetBrowser(uri: String, onActivityNotFound: (uri: String) -> Unit) {
    Intent.parseUri(uri, 0).let {
        if (it.resolveActivity(packageManager) != null) startActivity(it)
        else onActivityNotFound(uri)
    }
}

fun Context.dpToPx(dp: Float) = dp * resources.displayMetrics.densityDpi / DENSITY_DEFAULT
