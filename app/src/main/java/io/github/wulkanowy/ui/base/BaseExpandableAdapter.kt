package io.github.wulkanowy.ui.base

import android.util.DisplayMetrics
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max
import kotlin.math.min

abstract class BaseExpandableAdapter<T : RecyclerView.ViewHolder> : RecyclerView.Adapter<T>() {

    companion object {
        private const val MILLISECONDS_PER_INCH = 100f
        private const val AUTO_SCROLL_DELAY = 150L
    }

    private var recyclerView: RecyclerView? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }

    // original: https://github.com/davideas/FlexibleAdapter/blob/5.1.0/flexible-adapter/src/main/java/eu/davidea/flexibleadapter/FlexibleAdapter.java#L4984-L5011
    protected fun scrollToHeaderWithSubItems(position: Int, subItemsCount: Int) {
        val layoutManager = recyclerView!!.layoutManager as LinearLayoutManager
        val firstVisibleItem = layoutManager.findFirstCompletelyVisibleItemPosition()
        val lastVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition()
        val itemsToShow = position + subItemsCount - lastVisibleItem
        if (itemsToShow > 0) {
            val scrollMax: Int = position - firstVisibleItem
            val scrollMin = max(0, position + subItemsCount - lastVisibleItem)
            val scrollBy = min(scrollMax, scrollMin)
            val scrollTo = firstVisibleItem + scrollBy
            scrollToPosition(scrollTo)
        } else if (position < firstVisibleItem) {
            scrollToPosition(position)
        }
    }

    private fun scrollToPosition(position: Int) {
        recyclerView?.run {
            postDelayed({
                layoutManager?.startSmoothScroll(object : LinearSmoothScroller(context) {
                    override fun getVerticalSnapPreference() = SNAP_TO_START
                    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics) = MILLISECONDS_PER_INCH / displayMetrics.densityDpi
                }.apply {
                    targetPosition = position
                })
            }, AUTO_SCROLL_DELAY)
        }
    }
}
