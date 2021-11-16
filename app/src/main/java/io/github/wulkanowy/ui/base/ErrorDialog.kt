package io.github.wulkanowy.ui.base

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AlertDialog
import androidx.core.content.getSystemService
import androidx.core.view.isGone
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.databinding.DialogErrorBinding
import io.github.wulkanowy.sdk.exception.FeatureNotAvailableException
import io.github.wulkanowy.sdk.scrapper.exception.FeatureDisabledException
import io.github.wulkanowy.sdk.scrapper.exception.ServiceUnavailableException
import io.github.wulkanowy.utils.AppInfo
import io.github.wulkanowy.utils.getString
import io.github.wulkanowy.utils.openAppInMarket
import io.github.wulkanowy.utils.openEmailClient
import io.github.wulkanowy.utils.openInternetBrowser
import okhttp3.internal.http2.StreamResetException
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@AndroidEntryPoint
class ErrorDialog : BaseDialogFragment<DialogErrorBinding>() {

    private lateinit var error: Throwable

    @Inject
    lateinit var appInfo: AppInfo

    companion object {
        private const val ARGUMENT_KEY = "Data"

        fun newInstance(error: Throwable) = ErrorDialog().apply {
            arguments = Bundle().apply { putSerializable(ARGUMENT_KEY, error) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)
        arguments?.run {
            error = getSerializable(ARGUMENT_KEY) as Throwable
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = DialogErrorBinding.inflate(inflater).apply { binding = this }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val errorStacktrace = error.stackTraceToString()

        with(binding) {
            errorDialogContent.text = errorStacktrace.replace(": ${error.localizedMessage}", "")
            with(errorDialogHorizontalScroll) {
                post { fullScroll(HorizontalScrollView.FOCUS_LEFT) }
            }
            errorDialogCopy.setOnClickListener {
                val clip = ClipData.newPlainText("Error details", errorStacktrace)
                activity?.getSystemService<ClipboardManager>()?.setPrimaryClip(clip)

                Toast.makeText(context, R.string.all_copied, LENGTH_LONG).show()
            }
            errorDialogCancel.setOnClickListener { dismiss() }
            errorDialogReport.setOnClickListener {
                openConfirmDialog { openEmailClient(errorStacktrace) }
            }
            errorDialogHumanizedMessage.text = resources.getString(error)
            errorDialogErrorMessage.text = error.localizedMessage
            errorDialogErrorMessage.isGone = error.localizedMessage.isNullOrBlank()
            errorDialogReport.isEnabled = when (error) {
                is UnknownHostException,
                is InterruptedIOException,
                is ConnectException,
                is StreamResetException,
                is SocketTimeoutException,
                is ServiceUnavailableException,
                is FeatureDisabledException,
                is FeatureNotAvailableException -> false
                else -> true
            }
        }
    }

    private fun openConfirmDialog(callback: () -> Unit) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_error_check_update)
            .setMessage(R.string.dialog_error_check_update_message)
            .setNeutralButton(R.string.about_feedback) { _, _ -> callback() }
            .setPositiveButton(R.string.dialog_error_check_update) { _, _ ->
                requireContext().openAppInMarket(::showMessage)
            }
            .show()
    }

    private fun openEmailClient(content: String) {
        requireContext().openEmailClient(
            chooserTitle = getString(R.string.about_feedback),
            email = "wulkanowyinc@gmail.com",
            subject = "Zgłoszenie błędu",
            body = requireContext().getString(
                R.string.about_feedback_template,
                "${appInfo.systemManufacturer} ${appInfo.systemModel}",
                appInfo.systemVersion.toString(),
                "${appInfo.versionName}-${appInfo.buildFlavor}"
            ) + "\n" + content,
            onActivityNotFound = {
                requireContext().openInternetBrowser(
                    "https://github.com/wulkanowy/wulkanowy/issues",
                    ::showMessage
                )
            }
        )
    }
}
