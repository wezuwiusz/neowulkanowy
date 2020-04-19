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
import androidx.core.content.getSystemService
import io.github.wulkanowy.R
import io.github.wulkanowy.sdk.exception.FeatureDisabledException
import io.github.wulkanowy.sdk.exception.FeatureNotAvailableException
import io.github.wulkanowy.sdk.exception.ServiceUnavailableException
import io.github.wulkanowy.utils.AppInfo
import io.github.wulkanowy.utils.getString
import io.github.wulkanowy.utils.openEmailClient
import io.github.wulkanowy.utils.openInternetBrowser
import kotlinx.android.synthetic.main.dialog_error.*
import java.io.PrintWriter
import java.io.StringWriter
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class ErrorDialog : BaseDialogFragment() {

    private lateinit var error: Throwable

    @Inject
    lateinit var appInfo: AppInfo

    companion object {
        private const val ARGUMENT_KEY = "Data"

        fun newInstance(error: Throwable): ErrorDialog {
            return ErrorDialog().apply {
                arguments = Bundle().apply { putSerializable(ARGUMENT_KEY, error) }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, 0)
        arguments?.run {
            error = getSerializable(ARGUMENT_KEY) as Throwable
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_error, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val stringWriter = StringWriter().apply {
            error.printStackTrace(PrintWriter(this))
        }

        errorDialogContent.text = stringWriter.toString()
        with(errorDialogHorizontalScroll) {
            post { fullScroll(HorizontalScrollView.FOCUS_LEFT) }
        }
        errorDialogCopy.setOnClickListener {
            val clip = ClipData.newPlainText("wulkanowy", stringWriter.toString())
            activity?.getSystemService<ClipboardManager>()?.setPrimaryClip(clip)

            Toast.makeText(context, R.string.all_copied, LENGTH_LONG).show()
        }
        errorDialogCancel.setOnClickListener { dismiss() }
        errorDialogReport.setOnClickListener { openEmailClient(stringWriter.toString()) }
        errorDialogMessage.text = resources.getString(error)
        errorDialogReport.isEnabled = when (error) {
            is UnknownHostException,
            is SocketTimeoutException,
            is ServiceUnavailableException,
            is FeatureDisabledException,
            is FeatureNotAvailableException -> false
            else -> true
        }
    }

    private fun openEmailClient(content: String) {
        requireContext().openEmailClient(
            chooserTitle = getString(R.string.about_feedback),
            email = "wulkanowyinc@gmail.com",
            subject = "Zgłoszenie błędu",
            body = requireContext().getString(R.string.about_feedback_template,
                "${appInfo.systemManufacturer} ${appInfo.systemModel}", appInfo.systemVersion.toString(), appInfo.versionName
            ) + "\n" + content,
            onActivityNotFound = {
                requireContext().openInternetBrowser("https://github.com/wulkanowy/wulkanowy/issues", ::showMessage)
            }
        )
    }
}
