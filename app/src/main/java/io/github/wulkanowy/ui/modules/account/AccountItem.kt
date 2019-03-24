package io.github.wulkanowy.ui.modules.account

import android.view.View
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Student
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_account.*

class AccountItem(val student: Student) : AbstractFlexibleItem<AccountItem.ViewHolder>() {

    override fun getLayoutRes() = R.layout.item_account

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>, holder: ViewHolder, position: Int, payloads: MutableList<Any>?) {
        holder.apply {
            accountItemName.text = student.studentName
            accountItemSchool.text = student.schoolName
            accountItemImage.setBackgroundResource(if (student.isCurrent) R.drawable.ic_account_circular_border else 0)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AccountItem

        if (student != other.student) return false

        return true
    }

    override fun hashCode(): Int {
        var result = student.hashCode()
        result = 31 * result + student.id.toInt()
        return result
    }

    class ViewHolder(view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter), LayoutContainer {
        override val containerView: View
            get() = contentView
    }
}
