package io.github.wulkanowy.ui.base

import android.view.View
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerFragment
import io.github.wulkanowy.R

abstract class BaseFragment : DaggerFragment(), BaseView {

    protected var messageContainer: View? = null

    override fun showError(text: String, error: Throwable) {
        if (messageContainer == null) (activity as? BaseActivity)?.showError(text, error)
        else messageContainer?.also {
            Snackbar.make(it, text, Snackbar.LENGTH_LONG).setAction(R.string.all_details) {
                ErrorDialog.newInstance(error).show(fragmentManager, error.toString())
            }.show()
        }
    }

    override fun showMessage(text: String) {
        if (messageContainer == null) (activity as? BaseActivity)?.showMessage(text)
        else messageContainer?.also { Snackbar.make(it, text, Snackbar.LENGTH_LONG).show() }
    }
}
