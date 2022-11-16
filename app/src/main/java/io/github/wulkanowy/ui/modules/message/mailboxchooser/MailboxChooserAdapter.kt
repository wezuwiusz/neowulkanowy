package io.github.wulkanowy.ui.modules.message.mailboxchooser

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Mailbox
import io.github.wulkanowy.data.db.entities.MailboxType
import io.github.wulkanowy.databinding.ItemMailboxChooserBinding
import javax.inject.Inject

class MailboxChooserAdapter @Inject constructor() :
    ListAdapter<MailboxChooserItem, MailboxChooserAdapter.ItemViewHolder>(Differ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        ItemMailboxChooserBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ItemViewHolder(
        private val binding: ItemMailboxChooserBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MailboxChooserItem) {
            with(binding) {
                mailboxItemName.text = item.mailbox?.getFirstLine()
                    ?: root.resources.getString(R.string.message_chip_all_mailboxes)
                mailboxItemSchool.text = item.mailbox?.getSecondLine()
                mailboxItemSchool.isVisible = !item.isAll

                root.setOnClickListener { item.onClickListener(item.mailbox) }
            }
        }

        private fun Mailbox.getFirstLine() = buildString {
            if (studentName.isNotBlank() && studentName != userName) {
                append(studentName)
                append(" - ")
            }
            append(userName)
        }

        private fun Mailbox.getSecondLine() = buildString {
            append(schoolNameShort)
            append(" - ")
            append(getMailboxType(type))
        }

        private fun getMailboxType(type: MailboxType): String = when (type) {
            MailboxType.STUDENT -> R.string.message_mailbox_type_student
            MailboxType.PARENT -> R.string.message_mailbox_type_parent
            MailboxType.GUARDIAN -> R.string.message_mailbox_type_guardian
            MailboxType.EMPLOYEE -> R.string.message_mailbox_type_employee
            MailboxType.UNKNOWN -> null
        }.let { it?.let { it1 -> binding.root.resources.getString(it1) }.orEmpty() }
    }

    private object Differ : ItemCallback<MailboxChooserItem>() {
        override fun areItemsTheSame(
            oldItem: MailboxChooserItem,
            newItem: MailboxChooserItem
        ): Boolean {
            return oldItem.mailbox?.globalKey == newItem.mailbox?.globalKey
        }

        override fun areContentsTheSame(
            oldItem: MailboxChooserItem,
            newItem: MailboxChooserItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}
