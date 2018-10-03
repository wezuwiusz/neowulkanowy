package io.github.wulkanowy.ui.base

import dagger.android.support.DaggerFragment

abstract class BaseFragment : DaggerFragment(), BaseView {

    override fun showMessage(text: String) {
        (activity as BaseActivity?)?.showMessage(text)
    }
}
