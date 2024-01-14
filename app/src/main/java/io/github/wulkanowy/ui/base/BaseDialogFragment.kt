package io.github.wulkanowy.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.elevation.SurfaceColors
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

    override fun showExpiredCredentialsDialog() {
        (activity as? BaseActivity<*, *>)?.showExpiredCredentialsDialog()
    }

    override fun onCaptchaVerificationRequired(url: String?) {
        (activity as? BaseActivity<*, *>)?.onCaptchaVerificationRequired(url)
    }

    override fun showDecryptionFailedDialog() {
        (activity as? BaseActivity<*, *>)?.showDecryptionFailedDialog()
    }

    override fun openClearLoginView() {
        (activity as? BaseActivity<*, *>)?.openClearLoginView()
    }

    override fun showChangePasswordSnackbar(redirectUrl: String) {
        (activity as? BaseActivity<*, *>)?.showChangePasswordSnackbar(redirectUrl)
    }

    override fun showAuthDialog() {
        (activity as? BaseActivity<*, *>)?.showAuthDialog()
    }

    override fun showErrorDetailsDialog(error: Throwable) {
        ErrorDialog.newInstance(error).show(childFragmentManager, error.toString())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(SurfaceColors.SURFACE_3.getColor(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = binding.root

    override fun onResume() {
        super.onResume()
        analyticsHelper.setCurrentScreen(requireActivity(), this::class.simpleName)
    }

    override fun onPause() {
        super.onPause()
        analyticsHelper.popCurrentScreen(this::class.simpleName)
    }
}
