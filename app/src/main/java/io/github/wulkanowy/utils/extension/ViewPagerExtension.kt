package io.github.wulkanowy.utils.extension

import android.support.v4.view.ViewPager

fun ViewPager.setOnSelectPageListener(selectListener: (position: Int) -> Unit) {
    addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageSelected(position: Int) {
            selectListener(position)
        }

        override fun onPageScrollStateChanged(state: Int) {}
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
    })
}
