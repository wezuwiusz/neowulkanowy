package io.github.wulkanowy.ui.base

import android.support.design.widget.Snackbar
import android.support.design.widget.Snackbar.LENGTH_LONG
import android.view.View
import dagger.android.support.DaggerFragment

abstract class BaseFragment : DaggerFragment(), BaseView {

    protected var messageContainer: View? = null

    override fun showMessage(text: String) {
        if (messageContainer == null) (activity as? BaseActivity)?.showMessage(text)
        else messageContainer?.also { Snackbar.make(it, text, LENGTH_LONG).show() }
    }
}
