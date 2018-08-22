package io.github.wulkanowy.ui.base

import android.support.annotation.StringRes

import dagger.android.support.DaggerFragment

abstract class BaseFragment : DaggerFragment(), BaseView {

    fun setTitle(title: String) {
        activity?.title = title
    }

    override fun showMessage(text: String) {
        (activity as BaseActivity?)?.showMessage(text)
    }

    fun showMessage(@StringRes stringId: Int) {
        showMessage(getString(stringId))
    }

    override fun showNoNetworkMessage() {
        (activity as BaseActivity?)?.showNoNetworkMessage()
    }
}
