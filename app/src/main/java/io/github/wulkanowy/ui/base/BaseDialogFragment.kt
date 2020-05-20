package io.github.wulkanowy.ui.base

import android.widget.Toast
import androidx.viewbinding.ViewBinding
import dagger.android.support.DaggerAppCompatDialogFragment
import io.github.wulkanowy.utils.lifecycleAwareVariable

abstract class BaseDialogFragment<VB : ViewBinding> : DaggerAppCompatDialogFragment(), BaseView {

    protected var binding: VB by lifecycleAwareVariable()

    override fun showError(text: String, error: Throwable) {
        showMessage(text)
    }

    override fun showMessage(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }

    override fun showExpiredDialog() {
        (activity as? BaseActivity<*, *>)?.showExpiredDialog()
    }

    override fun openClearLoginView() {
        (activity as? BaseActivity<*, *>)?.openClearLoginView()
    }

    override fun showErrorDetailsDialog(error: Throwable) {
        ErrorDialog.newInstance(error).show(childFragmentManager, error.toString())
    }
}
