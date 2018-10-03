package io.github.wulkanowy.utils

import android.support.v4.app.Fragment
import com.ncapdevi.fragnav.FragNavController

inline fun FragNavController.setOnTabTransactionListener(crossinline listener: (index: Int) -> Unit) {
    transactionListener = object : FragNavController.TransactionListener {
        override fun onFragmentTransaction(fragment: Fragment?, transactionType: FragNavController.TransactionType) {}
        override fun onTabTransaction(fragment: Fragment?, index: Int) {
            listener(index)
        }
    }
}