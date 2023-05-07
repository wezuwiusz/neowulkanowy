package io.github.wulkanowy.ui.modules.settings.appearance.menuorder

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.ShapeDrawable
import android.view.View
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.forEach
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.R
import io.github.wulkanowy.utils.getThemeAttrColor

class MenuOrderDividerItemDecoration(private val context: Context) :
    DividerItemDecoration(context, VERTICAL) {

    private val dividerDrawable = ShapeDrawable()
        .apply {
            DrawableCompat.setTint(this, context.getThemeAttrColor(R.attr.colorDivider))
        }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        canvas.save()
        val dividerLeft = parent.paddingLeft
        val dividerRight = parent.width - parent.paddingRight

        parent.forEach {
            if (parent.getChildAdapterPosition(it) == 3) {
                val params = it.layoutParams as RecyclerView.LayoutParams
                val dividerTop = it.bottom + params.bottomMargin
                val dividerBottom = dividerTop + dividerDrawable.intrinsicHeight

                dividerDrawable.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom + 30)
                dividerDrawable.draw(canvas)
            }
        }

        canvas.restore()
    }

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (parent.getChildAdapterPosition(view) == 3) {
            outRect.bottom = dividerDrawable.intrinsicHeight + 30
        }
    }
}
