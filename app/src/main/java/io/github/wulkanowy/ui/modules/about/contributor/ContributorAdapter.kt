package io.github.wulkanowy.ui.modules.about.contributor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import io.github.wulkanowy.data.pojos.Contributor
import io.github.wulkanowy.databinding.ItemContributorBinding
import javax.inject.Inject

class ContributorAdapter @Inject constructor() :
    RecyclerView.Adapter<ContributorAdapter.ItemViewHolder>() {

    var items = emptyList<Contributor>()

    var onClickListener: (Contributor) -> Unit = {}

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        ItemContributorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]

        with(holder.binding) {
            creatorItemName.text = item.displayName
            creatorItemAvatar.load("https://github.com/${item.githubUsername}.png") {
                transformations(RoundedCornersTransformation(8f))
                crossfade(true)
            }

            root.setOnClickListener { onClickListener(item) }
        }
    }

    class ItemViewHolder(val binding: ItemContributorBinding) :
        RecyclerView.ViewHolder(binding.root)
}
