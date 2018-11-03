package io.github.wulkanowy.utils

import eu.davidea.flexibleadapter.FlexibleAdapter

inline fun FlexibleAdapter<*>.setOnItemClickListener(crossinline listener: (position: Int) -> Unit) {
    addListener(FlexibleAdapter.OnItemClickListener { _, position ->
        listener(position)
        true
    })
}