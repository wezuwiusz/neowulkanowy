package io.github.wulkanowy.ui.widgets

import android.content.Context
import android.graphics.Canvas
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView

class DividerItemDecoration(context: Context) : DividerItemDecoration(context, VERTICAL) {

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        canvas.save()
        val dividerLeft = parent.paddingLeft
        val dividerRight = parent.width - parent.paddingRight

        for (i in 0..parent.childCount - 2) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val dividerTop = child.bottom + params.bottomMargin
            val dividerBottom = dividerTop + drawable!!.intrinsicHeight

            drawable?.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom)
            drawable?.draw(canvas)
        }
        canvas.restore()
    }
}
