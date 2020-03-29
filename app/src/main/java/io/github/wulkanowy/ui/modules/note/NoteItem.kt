package io.github.wulkanowy.ui.modules.note

import android.annotation.SuppressLint
import android.graphics.Typeface.BOLD
import android.graphics.Typeface.NORMAL
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.content.ContextCompat
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Note
import io.github.wulkanowy.sdk.scrapper.notes.Note.CategoryType
import io.github.wulkanowy.utils.getThemeAttrColor
import io.github.wulkanowy.utils.toFormattedString
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_note.*

class NoteItem(val note: Note) : AbstractFlexibleItem<NoteItem.ViewHolder>() {

    override fun getLayoutRes() = R.layout.item_note

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>): ViewHolder {
        return ViewHolder(view, adapter)
    }

    @SuppressLint("SetTextI18n")
    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>, holder: ViewHolder, position: Int, payloads: MutableList<Any>?) {
        holder.apply {
            with(noteItemDate) {
                text = note.date.toFormattedString()
                setTypeface(null, if (note.isRead) NORMAL else BOLD)
            }
            with(noteItemType) {
                text = note.category
                setTypeface(null, if (note.isRead) NORMAL else BOLD)
            }
            with(noteItemPoints) {
                text = "${if (note.points > 0) "+" else ""}${note.points}"
                visibility = if (note.isPointsShow) VISIBLE else GONE
                setTextColor(when(CategoryType.getByValue(note.categoryType)) {
                    CategoryType.POSITIVE -> ContextCompat.getColor(context, R.color.note_positive)
                    CategoryType.NEGATIVE -> ContextCompat.getColor(context, R.color.note_negative)
                    else -> context.getThemeAttrColor(android.R.attr.textColorPrimary)
                })
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
        if (note.id != other.note.id) return false
        return true
    }

    override fun hashCode(): Int {
        var result = note.hashCode()
        result = 31 * result + note.id.toInt()
        return result
    }

    class ViewHolder(val view: View, adapter: FlexibleAdapter<*>) :
        FlexibleViewHolder(view, adapter), LayoutContainer {
        override val containerView: View
            get() = contentView
    }
}
