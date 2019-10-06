package io.github.wulkanowy.ui.modules.teacher

import android.annotation.SuppressLint
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Teacher
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_teacher.*

class TeacherItem(val teacher: Teacher, private val noSubjectText: String) : AbstractFlexibleItem<TeacherItem.ViewHolder>() {

    override fun getLayoutRes() = R.layout.item_teacher

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>): TeacherItem.ViewHolder {
        return TeacherItem.ViewHolder(view, adapter)
    }

    @SuppressLint("SetTextI18n")
    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>, holder: TeacherItem.ViewHolder, position: Int, payloads: MutableList<Any>?) {
        holder.apply {
            teacherItemName.text = teacher.name
            teacherItemSubject.text = if (teacher.subject.isNotBlank()) teacher.subject else noSubjectText
            if (teacher.shortName.isNotBlank()) {
                teacherItemShortName.visibility = VISIBLE
                teacherItemShortName.text = "[${teacher.shortName}]"
            } else {
                teacherItemShortName.visibility = GONE
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TeacherItem

        if (teacher != other.teacher) return false
        if (teacher.id != other.teacher.id) return false
        return true
    }

    override fun hashCode(): Int {
        var result = teacher.hashCode()
        result = 31 * result + teacher.id.toInt()
        return result
    }

    class ViewHolder(val view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter), LayoutContainer {
        override val containerView: View
            get() = contentView
    }
}
