package io.github.wulkanowy.ui.base

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AlertDialog
import androidx.core.content.getSystemService
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.databinding.DialogErrorBinding
import io.github.wulkanowy.utils.*
import javax.inject.Inject

@AndroidEntryPoint
class ErrorDialog : BaseDialogFragment<DialogErrorBinding>() {

    @Inject
    lateinit var appInfo: AppInfo

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    private lateinit var error: Throwable

    companion object {
        private const val ARGUMENT_KEY = "error"

        fun newInstance(error: Throwable) = ErrorDialog().apply {
            arguments = bundleOf(ARGUMENT_KEY to error)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        error = requireArguments().serializable(ARGUMENT_KEY)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext()).apply {
            val errorStacktrace = error.stackTraceToString()
            setTitle(R.string.all_details)
            setView(DialogErrorBinding.inflate(layoutInflater).apply { binding = this }.root)
            setNeutralButton(R.string.about_feedback) { _, _ ->
                openConfirmDialog { openEmailClient(errorStacktrace) }
            }
            setNegativeButton(android.R.string.cancel) { _, _ -> }
            setPositiveButton(android.R.string.copy) { _, _ -> copyErrorToClipboard(errorStacktrace) }
        }.create().apply {
            setOnShowListener {
                getButton(AlertDialog.BUTTON_NEUTRAL).isEnabled = error.isShouldBeReported()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            errorDialogHumanizedMessage.text = resources.getErrorString(error)
            errorDialogErrorMessage.text = error.localizedMessage
            errorDialogErrorMessage.isGone = error.localizedMessage.isNullOrBlank()
            errorDialogContent.text = error.stackTraceToString()
                .replace(": ${error.localizedMessage}", "")
        }
    }

    private fun copyErrorToClipboard(errorStacktrace: String) {
        val clip = ClipData.newPlainText("Error details", errorStacktrace)
        requireActivity().getSystemService<ClipboardManager>()?.setPrimaryClip(clip)
        Toast.makeText(requireContext(), R.string.all_copied, LENGTH_LONG).show()
    }

    private fun openConfirmDialog(callback: () -> Unit) {
        MaterialAlertDialogBuilder(requireContext())
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
                "${appInfo.versionName}-${appInfo.buildFlavor}",
                preferencesRepository.installationId,
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
