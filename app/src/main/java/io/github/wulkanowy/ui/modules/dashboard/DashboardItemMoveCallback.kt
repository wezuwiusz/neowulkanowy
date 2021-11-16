package io.github.wulkanowy.ui.modules.dashboard

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.Collections

class DashboardItemMoveCallback(
    private val dashboardAdapter: DashboardAdapter,
    private var onUserInteractionEndListener: (List<DashboardItem>) -> Unit = {}
) : ItemTouchHelper.Callback() {

    override fun isLongPressDragEnabled() = true

    override fun isItemViewSwipeEnabled() = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        //Not implemented
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = if (!viewHolder.isAdminMessageOrAccountItem) {
            ItemTouchHelper.UP or ItemTouchHelper.DOWN
        } else 0

        return makeMovementFlags(dragFlags, 0)
    }

    override fun canDropOver(
        recyclerView: RecyclerView,
        current: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ) = !target.isAdminMessageOrAccountItem

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val list = dashboardAdapter.items.toMutableList()

        Collections.swap(list, viewHolder.bindingAdapterPosition, target.bindingAdapterPosition)

        dashboardAdapter.submitList(list)
        return true
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)

        onUserInteractionEndListener(dashboardAdapter.items.toList())
    }

    private val RecyclerView.ViewHolder.isAdminMessageOrAccountItem: Boolean
        get() = this is DashboardAdapter.AdminMessageViewHolder || this is DashboardAdapter.AccountViewHolder
}
