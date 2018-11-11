package io.github.wulkanowy.ui.modules.note

import android.graphics.Typeface.BOLD
import android.graphics.Typeface.NORMAL
import android.view.View
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Note
import io.github.wulkanowy.utils.toFormattedString
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_note.*

class NoteItem(val note: Note) : AbstractFlexibleItem<NoteItem.ViewHolder>() {

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>): NoteItem.ViewHolder {
        return NoteItem.ViewHolder(view, adapter)
    }

    override fun getLayoutRes(): Int = R.layout.item_note

    override fun bindViewHolder(
        adapter: FlexibleAdapter<IFlexible<*>>,
        holder: NoteItem.ViewHolder, position: Int, payloads: MutableList<Any>?
    ) {
        holder.apply {
            noteItemDate.apply {
                text = note.date.toFormattedString()
                setTypeface(null, if (note.isRead) NORMAL else BOLD)
            }
            noteItemType.apply {
                text = note.category
                setTypeface(null, if (note.isRead) NORMAL else BOLD)
            }
            noteItemTeacher.text = note.teacher
            noteItemContent.text = note.content
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NoteItem

        if (note != other.note) return false
        return true
    }

    override fun hashCode(): Int {
        return note.hashCode()
    }

    class ViewHolder(val view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter), LayoutContainer {

        override val containerView: View
            get() = contentView
    }
}
