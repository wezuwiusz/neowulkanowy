package io.github.wulkanowy.utils

import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem

inline fun FlexibleAdapter<*>.setOnItemClickListener(crossinline listener: (item: AbstractFlexibleItem<*>) -> Unit) {
    addListener(FlexibleAdapter.OnItemClickListener { _, position ->
        listener(getItem(position) as AbstractFlexibleItem<*>)
        true
    })
}