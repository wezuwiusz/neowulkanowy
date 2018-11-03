package io.github.wulkanowy.utils

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt

@ColorInt
fun Context.getThemeAttrColor(@AttrRes colorAttr: Int): Int {
    val array = this.obtainStyledAttributes(null, intArrayOf(colorAttr))
    try {
        return array.getColor(0, 0)
    } finally {
        array.recycle()
    }
}
