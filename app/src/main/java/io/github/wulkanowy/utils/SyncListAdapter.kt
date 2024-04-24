package io.github.wulkanowy.utils

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 * Custom alternative to androidx.recyclerview.widget.ListAdapter. ListAdapter is asynchronous which
 * caused data race problems in views when a Resource.Error arrived shortly after
 * Resource.Intermediate/Success - occasionally in that case the user could see both the Resource's
 * data and an error message one on top of the other. This is synchronized by design to avoid that
 * problem, however it retains the quality of life improvements of the original.
 */
abstract class SyncListAdapter<T : Any, VH : RecyclerView.ViewHolder> private constructor(
    private val updateStrategy: SyncListAdapter<T, VH>.(List<T>) -> Unit
) : RecyclerView.Adapter<VH>() {

    constructor(differ: DiffUtil.ItemCallback<T>) : this({ newItems ->
        val diffResult = DiffUtil.calculateDiff(toCallback(differ, items, newItems))
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    })

    var items = emptyList<T>()
        private set

    final override fun getItemCount() = items.size

    fun getItem(position: Int): T {
        return items[position]
    }

    /**
     * Updates all items, same as submitList, however also disables animations temporarily.
     * This prevents a flashing effect on some views. Should be used in favor of submitList when
     * all data is changed (e.g. the selected day changes in timetable causing all lessons to change).
     */
    @SuppressLint("NotifyDataSetChanged")
    fun recreate(data: List<T>) {
        items = data
        notifyDataSetChanged()
    }

    fun submitList(data: List<T>) {
        updateStrategy(data.toList())
    }

    private fun <T : Any> toCallback(
        itemCallback: DiffUtil.ItemCallback<T>,
        old: List<T>,
        new: List<T>,
    ) = object : DiffUtil.Callback() {
        override fun getOldListSize() = old.size

        override fun getNewListSize() = new.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            itemCallback.areItemsTheSame(old[oldItemPosition], new[newItemPosition])

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            itemCallback.areContentsTheSame(old[oldItemPosition], new[newItemPosition])

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int) =
            itemCallback.getChangePayload(old[oldItemPosition], new[newItemPosition])
    }
}
