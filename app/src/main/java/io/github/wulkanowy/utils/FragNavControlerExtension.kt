package io.github.wulkanowy.utils

import androidx.fragment.app.Fragment
import com.ncapdevi.fragnav.FragNavController
import io.github.wulkanowy.ui.modules.main.MainView

inline fun FragNavController.setOnViewChangeListener(crossinline listener: (section: MainView.Section?, name: String?) -> Unit) {
    transactionListener = object : FragNavController.TransactionListener {
        override fun onFragmentTransaction(fragment: Fragment?, transactionType: FragNavController.TransactionType) {
            listener(fragment?.toSection(), fragment?.let { it::class.java.simpleName })
        }

        override fun onTabTransaction(fragment: Fragment?, index: Int) {
            listener(fragment?.toSection(), fragment?.let { it::class.java.simpleName })
        }
    }
}

fun FragNavController.safelyPopFragments(depth: Int) {
    if (!isRootFragment) popFragments(depth)
}
