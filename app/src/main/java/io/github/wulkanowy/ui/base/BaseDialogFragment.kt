package io.github.wulkanowy.ui.base

import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.lifecycleAwareVariable
import javax.inject.Inject

abstract class BaseDialogFragment<VB : ViewBinding> : DialogFragment(), BaseView {

    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

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

    override fun onResume() {
        super.onResume()
        analyticsHelper.setCurrentScreen(requireActivity(), this::class.simpleName)
    }

    override fun onPause() {
        super.onPause()
        analyticsHelper.popCurrentScreen(this::class.simpleName)
    }
}
