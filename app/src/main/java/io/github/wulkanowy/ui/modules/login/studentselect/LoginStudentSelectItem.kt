package io.github.wulkanowy.ui.modules.login.studentselect

import android.view.View
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Student
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_login_student_select.view.*

class LoginStudentSelectItem(val student: Student) : AbstractFlexibleItem<LoginStudentSelectItem.ItemViewHolder>() {

    override fun getLayoutRes(): Int = R.layout.item_login_student_select

    override fun createViewHolder(view: View?, adapter: FlexibleAdapter<IFlexible<*>>?): ItemViewHolder {
        return ItemViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>?, holder: ItemViewHolder?,
                                position: Int, payloads: MutableList<Any>?) {
        holder?.bind(student)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LoginStudentSelectItem

        if (student != other.student) return false

        return true
    }

    override fun hashCode(): Int {
        return student.hashCode()
    }

    class ItemViewHolder(view: View?, adapter: FlexibleAdapter<*>?)
        : FlexibleViewHolder(view, adapter), LayoutContainer {

        override val containerView: View?
            get() = itemView

        fun bind(item: Student) {
            itemView.run {
                loginItemName.text = item.studentName
                loginItemSchool.text = item.schoolName
            }
        }
    }
}
