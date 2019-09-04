package io.github.wulkanowy.utils

import androidx.fragment.app.Fragment
import com.ncapdevi.fragnav.FragNavController
import io.github.wulkanowy.ui.modules.main.MainView

inline fun FragNavController.setOnViewChangeListener(crossinline listener: (section: MainView.Section?) -> Unit) {
    transactionListener = object : FragNavController.TransactionListener {
        override fun onFragmentTransaction(fragment: Fragment?, transactionType: FragNavController.TransactionType) {
            listener(fragment?.toSection())
        }

        override fun onTabTransaction(fragment: Fragment?, index: Int) {
            listener(fragment?.toSection())
        }
    }
}

fun FragNavController.safelyPopFragments(depth: Int) {
    if (!isRootFragment) popFragments(depth)
}
