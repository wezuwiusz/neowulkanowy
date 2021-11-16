package io.github.wulkanowy.utils

import androidx.viewpager2.widget.ViewPager2

inline fun ViewPager2.setOnSelectPageListener(crossinline selectListener: (position: Int) -> Unit) {
    registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            selectListener(position)
        }
    })
}
