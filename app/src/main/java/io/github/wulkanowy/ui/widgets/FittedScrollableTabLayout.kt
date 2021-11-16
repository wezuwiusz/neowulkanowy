package io.github.wulkanowy.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout

/**
 * @see <a href="https://stackoverflow.com/a/50382854">Tabs don't fit to screen with tabmode=scrollable, Even with a Custom Tab Layout</a>
 */
class FittedScrollableTabLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : TabLayout(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
        val tabLayout = getChildAt(0) as ViewGroup
        val childCount = tabLayout.childCount

        if (childCount == 0) return

        val tabMinWidth = context.resources.displayMetrics.widthPixels / childCount

        for (i in 0 until childCount) {
            tabLayout.getChildAt(i).minimumWidth = tabMinWidth
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}
