package io.github.wulkanowy.ui.base

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AlertDialog
import androidx.core.content.getSystemService
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.databinding.DialogErrorBinding
import io.github.wulkanowy.sdk.exception.FeatureNotAvailableException
import io.github.wulkanowy.sdk.scrapper.exception.FeatureDisabledException
import io.github.wulkanowy.sdk.scrapper.exception.ServiceUnavailableException
import io.github.wulkanowy.utils.*
import okhttp3.internal.http2.StreamResetException
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@AndroidEntryPoint
class ErrorDialog : DialogFragment() {

    @Inject
    lateinit var appInfo: AppInfo

    companion object {
        private const val ARGUMENT_KEY = "error"

        fun newInstance(error: Throwable) = ErrorDialog().apply {
            arguments = bundleOf(ARGUMENT_KEY to error)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val error = requireArguments().getSerializable(ARGUMENT_KEY) as Throwable

        val binding = DialogErrorBinding.inflate(LayoutInflater.from(context))
        binding.bindErrorDetails(error)

        return getAlertDialog(binding, error).apply {
            enableReportButtonIfErrorIsReportable(error)
        }
    }

    private fun getAlertDialog(binding: DialogErrorBinding, error: Throwable): AlertDialog {
        return MaterialAlertDialogBuilder(requireContext()).apply {
            val errorStacktrace = error.stackTraceToString()
            setTitle(R.string.all_details)
            setView(binding.root)
            setNeutralButton(R.string.about_feedback) { _, _ ->
                openConfirmDialog { openEmailClient(errorStacktrace) }
            }
            setNegativeButton(android.R.string.cancel) { _, _ -> }
            setPositiveButton(android.R.string.copy) { _, _ -> copyErrorToClipboard(errorStacktrace) }
        }.create()
    }

    private fun DialogErrorBinding.bindErrorDetails(error: Throwable) {
        return with(this) {
            errorDialogHumanizedMessage.text = resources.getString(error)
            errorDialogErrorMessage.text = error.localizedMessage
            errorDialogErrorMessage.isGone = error.localizedMessage.isNullOrBlank()
            errorDialogContent.text = error.stackTraceToString()
                .replace(": ${error.localizedMessage}", "")
        }
    }

    private fun AlertDialog.enableReportButtonIfErrorIsReportable(error: Throwable) {
        setOnShowListener {
            getButton(AlertDialog.BUTTON_NEUTRAL).isEnabled = isErrorShouldBeReported(error)
        }
    }

    private fun isErrorShouldBeReported(error: Throwable): Boolean = when (error) {
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

    private fun copyErrorToClipboard(errorStacktrace: String) {
        val clip = ClipData.newPlainText("Error details", errorStacktrace)
        requireActivity().getSystemService<ClipboardManager>()?.setPrimaryClip(clip)
        Toast.makeText(requireContext(), R.string.all_copied, LENGTH_LONG).show()
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

    private fun showMessage(text: String) {
        Toast.makeText(requireContext(), text, LENGTH_LONG).show()
    }
}
