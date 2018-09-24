package io.github.wulkanowy.utils.extension

import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem

fun FlexibleAdapter<*>.setOnItemClickListener(listener: (position: Int) -> Unit) {
    addListener(FlexibleAdapter.OnItemClickListener { _, position ->
        listener(position)
        true
    })
}

fun FlexibleAdapter<*>.setOnUpdateListener(listener: (size: Int) -> Unit) {
    addListener(FlexibleAdapter.OnUpdateListener { listener(it) })
}
