package io.github.wulkanowy.ui.modules.account

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.databinding.HeaderAccountBinding
import io.github.wulkanowy.databinding.ItemAccountBinding
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.getThemeAttrColor
import javax.inject.Inject

class AccountAdapter @Inject constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items = emptyList<AccountItem<*>>()

    var onClickListener: (Student) -> Unit = {}

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = items[position].viewType.id

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            AccountItem.ViewType.HEADER.id -> HeaderViewHolder(HeaderAccountBinding.inflate(inflater, parent, false))
            AccountItem.ViewType.ITEM.id -> ItemViewHolder(ItemAccountBinding.inflate(inflater, parent, false))
            else -> throw IllegalStateException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> bindHeaderViewHolder(holder.binding, items[position].value as Account)
            is ItemViewHolder -> bindItemViewHolder(holder.binding, items[position].value as Student)
        }
    }

    private fun bindHeaderViewHolder(binding: HeaderAccountBinding, account: Account) {
        with(binding) {
            accountHeaderEmail.text = account.email
            accountHeaderType.setText(if (account.isParent) R.string.account_type_parent else R.string.account_type_student)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindItemViewHolder(binding: ItemAccountBinding, student: Student) {
        with(binding) {
            accountItemName.text = "${student.studentName} ${student.className}"
            accountItemSchool.text = student.schoolName
            with(accountItemLoginMode) {
                visibility = when (Sdk.Mode.valueOf(student.loginMode)) {
                    Sdk.Mode.API -> {
                        setText(R.string.account_login_mobile_api)
                        VISIBLE
                    }
                    Sdk.Mode.HYBRID -> {
                        setText(R.string.account_login_hybrid)
                        VISIBLE
                    }
                    Sdk.Mode.SCRAPPER -> {
                        GONE
                    }
                }
            }

            with(accountItemImage) {
                val colorImage = if (student.isCurrent) context.getThemeAttrColor(R.attr.colorPrimary)
                else context.getThemeAttrColor(R.attr.colorOnSurface, 153)

                setColorFilter(colorImage, PorterDuff.Mode.SRC_IN)
            }

            root.setOnClickListener { onClickListener(student) }
        }
    }

    class HeaderViewHolder(val binding: HeaderAccountBinding) :
        RecyclerView.ViewHolder(binding.root)

    class ItemViewHolder(val binding: ItemAccountBinding) : RecyclerView.ViewHolder(binding.root)
}
