package io.github.wulkanowy.ui.modules.luckynumberwidget

import android.annotation.SuppressLint
import android.view.View
import androidx.core.graphics.ColorUtils
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.ui.modules.timetablewidget.TimetableWidgetConfigureItem
import io.github.wulkanowy.utils.getThemeAttrColor
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_account.*

class LuckyNumberWidgetConfigureItem(var student: Student, val isCurrent: Boolean) :
    AbstractFlexibleItem<LuckyNumberWidgetConfigureItem.ViewHolder>() {

    override fun getLayoutRes() = R.layout.item_account

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>) = ViewHolder(view, adapter)

    @SuppressLint("SetTextI18n")
    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>, holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        val context = holder.contentView.context

        val colorImage = if (isCurrent) context.getThemeAttrColor(R.attr.colorPrimary)
        else ColorUtils.setAlphaComponent(context.getThemeAttrColor(R.attr.colorOnSurface), 153)

        with(holder) {
            accountItemName.text = "${student.studentName} ${student.className}"
            accountItemSchool.text = student.schoolName
            accountItemImage.setColorFilter(colorImage)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TimetableWidgetConfigureItem

        if (student != other.student) return false

        return true
    }

    override fun hashCode(): Int {
        var result = student.hashCode()
        result = 31 * result + student.id.toInt()
        return result
    }

    class ViewHolder(view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter),
        LayoutContainer {

        override val containerView: View? get() = contentView
    }
}
