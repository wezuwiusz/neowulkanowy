package io.github.wulkanowy.ui.base

import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_LONG
import io.github.wulkanowy.R
import io.github.wulkanowy.utils.lifecycleAwareVariable

abstract class BaseFragment<VB : ViewBinding>(@LayoutRes layoutId: Int) : Fragment(layoutId),
    BaseView {

    protected var binding: VB by lifecycleAwareVariable()

    protected var messageContainer: View? = null

    override fun showError(text: String, error: Throwable) {
        if (messageContainer != null) {
            Snackbar.make(messageContainer!!, text, LENGTH_LONG)
                .setAction(R.string.all_details) { if (isAdded) showErrorDetailsDialog(error) }
                .show()
        } else {
            (activity as? BaseActivity<*, *>)?.showError(text, error)
        }
    }

    override fun showErrorDetailsDialog(error: Throwable) {
        ErrorDialog.newInstance(error).show(childFragmentManager, error.toString())
    }

    override fun showMessage(text: String) {
        if (messageContainer != null) {
            Snackbar.make(messageContainer!!, text, LENGTH_LONG).show()
        } else {
            (activity as? BaseActivity<*, *>)?.showMessage(text)
        }
    }

    override fun showExpiredCredentialsDialog() {
        (activity as? BaseActivity<*, *>)?.showExpiredCredentialsDialog()
    }

    override fun onCaptchaVerificationRequired(url: String?) {
        (activity as? BaseActivity<*, *>)?.onCaptchaVerificationRequired(url)
    }

    override fun showDecryptionFailedDialog() {
        (activity as? BaseActivity<*, *>)?.showDecryptionFailedDialog()
    }

    override fun showAuthDialog() {
        (activity as? BaseActivity<*, *>)?.showAuthDialog()
    }

    override fun openClearLoginView() {
        (activity as? BaseActivity<*, *>)?.openClearLoginView()
    }

    override fun showChangePasswordSnackbar(redirectUrl: String) {
        (activity as? BaseActivity<*, *>)?.showChangePasswordSnackbar(redirectUrl)
    }
}
