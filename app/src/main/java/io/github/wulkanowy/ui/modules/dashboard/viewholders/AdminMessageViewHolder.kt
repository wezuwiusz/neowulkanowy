package io.github.wulkanowy.ui.modules.dashboard.viewholders

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.AdminMessage
import io.github.wulkanowy.databinding.ItemDashboardAdminMessageBinding
import io.github.wulkanowy.utils.getThemeAttrColor

class AdminMessageViewHolder(
    private val binding: ItemDashboardAdminMessageBinding,
    private val onAdminMessageDismissClickListener: (AdminMessage) -> Unit,
    private val onAdminMessageClickListener: (String?) -> Unit,
    private val onPanicButtonClickListener: () -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: AdminMessage?, showPanicButton: Boolean = false) {
        item ?: return

        val context = binding.root.context
        val (backgroundColor, textColor) = when (item.priority) {
            "HIGH" -> {
                context.getThemeAttrColor(R.attr.colorMessageHigh) to
                    context.getThemeAttrColor(R.attr.colorOnMessageHigh)
            }

            "MEDIUM" -> {
                context.getThemeAttrColor(R.attr.colorMessageMedium) to Color.BLACK
            }

            else -> null to context.getThemeAttrColor(R.attr.colorOnSurface)
        }

        with(binding) {
            dashboardAdminMessageItemTitle.text = item.title
            dashboardAdminMessageItemTitle.setTextColor(textColor)
            dashboardAdminMessageItemDescription.text = item.content
            dashboardAdminMessageItemDescription.setTextColor(textColor)
            dashboardAdminMessageItemIcon.setColorFilter(textColor)
            dashboardAdminMessageItemDismiss.isVisible = item.isOkVisible
            dashboardAdminMessageItemClose.isVisible = item.isXVisible
            dashboardAdminMessageItemDismiss.setTextColor(textColor)
            dashboardAdminMessageItemClose.imageTintList = ColorStateList.valueOf(textColor)
            dashboardAdminMessageItemDismiss.setOnClickListener {
                onAdminMessageDismissClickListener(item)
            }
            dashboardAdminMessageItemClose.setOnClickListener {
                onAdminMessageDismissClickListener(item)
            }
            dashboardPanicSection.root.isVisible = showPanicButton
            dashboardPanicSection.dashboardPanicButton.setOnClickListener {
                onPanicButtonClickListener()
            }

            dashboardAdminMessage.setCardBackgroundColor(backgroundColor?.let { ColorStateList.valueOf(it) })
            item.destinationUrl?.let { url ->
                dashboardAdminMessage.setOnClickListener { onAdminMessageClickListener(url) }
            }
        }
    }
}
