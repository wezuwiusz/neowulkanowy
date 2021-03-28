package io.github.wulkanowy.ui.modules.account

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.databinding.HeaderAccountBinding
import io.github.wulkanowy.databinding.ItemAccountBinding
import io.github.wulkanowy.utils.createNameInitialsDrawable
import io.github.wulkanowy.utils.getThemeAttrColor
import io.github.wulkanowy.utils.nickOrName
import javax.inject.Inject

class AccountAdapter @Inject constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var isAccountQuickDialogMode = false

    var items = emptyList<AccountItem<*>>()

    var onClickListener: (StudentWithSemesters) -> Unit = {}

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = items[position].viewType.id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            AccountItem.ViewType.HEADER.id -> HeaderViewHolder(
                HeaderAccountBinding.inflate(inflater, parent, false)
            )
            AccountItem.ViewType.ITEM.id -> ItemViewHolder(
                ItemAccountBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalStateException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> bindHeaderViewHolder(
                holder.binding,
                items[position].value as Account,
                position
            )
            is ItemViewHolder -> bindItemViewHolder(
                holder.binding,
                items[position].value as StudentWithSemesters
            )
        }
    }

    private fun bindHeaderViewHolder(
        binding: HeaderAccountBinding,
        account: Account,
        position: Int
    ) {
        with(binding) {
            accountHeaderDivider.visibility = if (position == 0) GONE else VISIBLE
            accountHeaderEmail.text = account.email
            accountHeaderType.setText(if (account.isParent) R.string.account_type_parent else R.string.account_type_student)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindItemViewHolder(
        binding: ItemAccountBinding,
        studentWithSemesters: StudentWithSemesters
    ) {
        val context = binding.root.context
        val (student, semesters) = studentWithSemesters
        val semester = semesters.maxByOrNull { it.semesterId }
        val avatar = context.createNameInitialsDrawable(student.nickOrName, student.avatarColor)
        val checkBackgroundColor =
            context.getThemeAttrColor(if (isAccountQuickDialogMode) R.attr.colorBackgroundFloating else R.attr.colorSurface)
        val isDuplicatedStudent = items.filter {
            if (it.value !is StudentWithSemesters) return@filter false
            val studentToCompare = it.value.student

            studentToCompare.studentId == student.studentId
                && studentToCompare.schoolSymbol == student.schoolSymbol
                && studentToCompare.symbol == student.symbol
        }.size > 1 && isAccountQuickDialogMode

        with(binding) {
            accountItemName.text = "${student.nickOrName} ${semester?.diaryName.orEmpty()}"
            accountItemSchool.text = studentWithSemesters.student.schoolName
            accountItemImage.setImageDrawable(avatar)

            with(accountItemAccountType) {
                setText(if (student.isParent) R.string.account_type_parent else R.string.account_type_student)
                isVisible = isDuplicatedStudent
            }

            with(accountItemCheck) {
                isVisible = student.isCurrent
                borderColor = checkBackgroundColor
                circleColor = checkBackgroundColor
            }

            root.setOnClickListener { onClickListener(studentWithSemesters) }
        }
    }

    class HeaderViewHolder(val binding: HeaderAccountBinding) :
        RecyclerView.ViewHolder(binding.root)

    class ItemViewHolder(val binding: ItemAccountBinding) : RecyclerView.ViewHolder(binding.root)
}
