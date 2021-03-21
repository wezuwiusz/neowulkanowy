package io.github.wulkanowy.ui.base

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.databinding.ItemAccountBinding
import io.github.wulkanowy.utils.createNameInitialsDrawable
import io.github.wulkanowy.utils.getThemeAttrColor
import io.github.wulkanowy.utils.nickOrName
import javax.inject.Inject

class WidgetConfigureAdapter @Inject constructor() :
    RecyclerView.Adapter<WidgetConfigureAdapter.ItemViewHolder>() {

    var items = emptyList<Pair<Student, Boolean>>()

    var onClickListener: (Student) -> Unit = {}

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        ItemAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val (student, isCurrent) = items[position]
        val context = holder.binding.root.context
        val checkBackgroundColor = context.getThemeAttrColor(R.attr.colorSurface)
        val avatar = context.createNameInitialsDrawable(student.nickOrName, student.avatarColor)
        val isDuplicatedStudent = items.filter {
            val studentToCompare = it.first

            studentToCompare.studentId == student.studentId
                && studentToCompare.schoolSymbol == student.schoolSymbol
                && studentToCompare.symbol == student.symbol
        }.size > 1

        with(holder.binding) {
            accountItemName.text = "${student.nickOrName} ${student.className}"
            accountItemSchool.text = student.schoolName
            accountItemImage.setImageDrawable(avatar)

            with(accountItemAccountType) {
                setText(if (student.isParent) R.string.account_type_parent else R.string.account_type_student)
                isVisible = isDuplicatedStudent
            }

            with(accountItemCheck) {
                isVisible = isCurrent
                borderColor = checkBackgroundColor
                circleColor = checkBackgroundColor
            }

            root.setOnClickListener { onClickListener(student) }
        }
    }

    class ItemViewHolder(val binding: ItemAccountBinding) : RecyclerView.ViewHolder(binding.root)
}
