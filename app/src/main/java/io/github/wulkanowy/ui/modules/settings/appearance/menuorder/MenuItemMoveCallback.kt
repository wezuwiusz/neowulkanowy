package io.github.wulkanowy.ui.modules.settings.appearance.menuorder

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class MenuItemMoveCallback(
    private val menuOrderAdapter: MenuOrderAdapter,
    private var onUserInteractionEndListener: (List<MenuOrderItem>) -> Unit = {}
) : ItemTouchHelper.Callback() {

    override fun isLongPressDragEnabled() = true

    override fun isItemViewSwipeEnabled() = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        //Not implemented
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ) = makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0)

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val list = menuOrderAdapter.items.toMutableList()

        Collections.swap(list, viewHolder.bindingAdapterPosition, target.bindingAdapterPosition)

        menuOrderAdapter.submitList(list)
        return true
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)

        onUserInteractionEndListener(menuOrderAdapter.items.toList())
    }
}

