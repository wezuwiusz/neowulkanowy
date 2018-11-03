package io.github.wulkanowy.ui.base

import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_LONG
import dagger.android.support.DaggerFragment

abstract class BaseFragment : DaggerFragment(), BaseView {

    protected var messageContainer: View? = null

    override fun showMessage(text: String) {
        if (messageContainer == null) (activity as? BaseActivity)?.showMessage(text)
        else messageContainer?.also { Snackbar.make(it, text, LENGTH_LONG).show() }
    }
}
