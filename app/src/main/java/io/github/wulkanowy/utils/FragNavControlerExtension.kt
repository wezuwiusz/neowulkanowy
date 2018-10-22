package io.github.wulkanowy.utils

import androidx.fragment.app.Fragment
import com.ncapdevi.fragnav.FragNavController

inline fun FragNavController.setOnViewChangeListener(crossinline listener: (fragment: Fragment?) -> Unit) {
    transactionListener = object : FragNavController.TransactionListener {
        override fun onFragmentTransaction(fragment: Fragment?, transactionType: FragNavController.TransactionType) {
            listener(fragment)
        }

        override fun onTabTransaction(fragment: Fragment?, index: Int) {
            listener(fragment)
        }
    }
}

fun FragNavController.safelyPopFragment() {
    if (!isRootFragment) popFragment()
}
