package io.github.wulkanowy.ui.modules.about.license

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.aboutlibraries.entity.Library
import io.github.wulkanowy.databinding.ItemLicenseBinding
import javax.inject.Inject

class LicenseAdapter @Inject constructor() : RecyclerView.Adapter<LicenseAdapter.ItemViewHolder>() {

    var items = emptyList<Library>()

    var onClickListener: (Library) -> Unit = {}

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        ItemLicenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]

        with(holder.binding) {
            licenseItemName.text = item.name
            licenseItemSummary.text = item.licenses.firstOrNull()?.name?.takeIf { it.isNotBlank() }
                ?: item.artifactVersion

            root.setOnClickListener { onClickListener(item) }
        }
    }

    class ItemViewHolder(val binding: ItemLicenseBinding) : RecyclerView.ViewHolder(binding.root)
}
