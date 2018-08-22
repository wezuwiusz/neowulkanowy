package io.github.wulkanowy.utils.extension

import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem

fun <K : AbstractFlexibleItem<*>, T : FlexibleAdapter<K>> T.setOnItemClickListener(listener: (K?) -> Unit) {
    addListener(FlexibleAdapter.OnItemClickListener { _, position ->
        listener(getItem(position))
        true
    })
}
