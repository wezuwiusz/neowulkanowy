package io.github.wulkanowy.ui.modules.grade.details

import android.content.Context
import android.graphics.Canvas
import androidx.recyclerview.widget.RecyclerView
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration

class GradeDetailsHeaderItemDecoration(context: Context) : FlexibleItemDecoration(context) {

    override fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        val left: Int
        val right: Int
        if (parent.clipToPadding) {
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
            canvas.clipRect(left, parent.paddingTop, right,
                parent.height - parent.paddingBottom)
        } else {
            left = 0
            right = parent.width
        }

        val itemCount = parent.childCount
        for (i in 1 until itemCount) {
            val child = parent.getChildAt(i)
            val viewHolder = parent.getChildViewHolder(child)
            if (shouldDrawDivider(viewHolder)) {
                parent.getDecoratedBoundsWithMargins(child, mBounds)
                val bottom = mBounds.top + Math.round(child.translationY)
                val top = bottom - mDivider.intrinsicHeight
                mDivider.setBounds(left, top, right, bottom)
                mDivider.draw(canvas)
            }
        }
        canvas.restore()
    }
}
