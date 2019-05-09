package io.github.wulkanowy.ui.base

import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_LONG
import dagger.android.support.DaggerFragment
import io.github.wulkanowy.R

abstract class BaseFragment : DaggerFragment(), BaseView {

    protected var messageContainer: View? = null

    override fun showError(text: String, error: Throwable) {
        if (messageContainer != null) {
            Snackbar.make(messageContainer!!, text, LENGTH_LONG)
                .setAction(R.string.all_details) {
                    if (isAdded) ErrorDialog.newInstance(error).show(childFragmentManager, error.toString())
                }
                .show()
        } else {
            (activity as? BaseActivity)?.showError(text, error)
        }
    }

    override fun showMessage(text: String) {
        if (messageContainer != null) {
            Snackbar.make(messageContainer!!, text, LENGTH_LONG).show()
        } else {
            (activity as? BaseActivity)?.showMessage(text)
        }
    }
}
