package io.github.wulkanowy.ui.modules.luckynumber.history

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.data.db.entities.LuckyNumber
import io.github.wulkanowy.databinding.ItemLuckyNumberHistoryBinding
import io.github.wulkanowy.utils.capitalise
import io.github.wulkanowy.utils.toFormattedString
import io.github.wulkanowy.utils.weekDayName
import javax.inject.Inject

class LuckyNumberHistoryAdapter @Inject constructor() :
    RecyclerView.Adapter<LuckyNumberHistoryAdapter.ItemViewHolder>() {

    var items = emptyList<LuckyNumber>()

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        ItemLuckyNumberHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            luckyNumberHistoryWeekName.text = item.date.weekDayName.capitalise()
            luckyNumberHistoryDate.text = item.date.toFormattedString()
            luckyNumberHistory.text = item.luckyNumber.toString()
        }
    }

    class ItemViewHolder(val binding: ItemLuckyNumberHistoryBinding) : RecyclerView.ViewHolder(binding.root)
}