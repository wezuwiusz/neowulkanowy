package io.github.wulkanowy.ui.modules.captcha

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.os.bundleOf
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.databinding.DialogCaptchaBinding
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.ui.base.BaseDialogFragment
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class CaptchaDialog : BaseDialogFragment<DialogCaptchaBinding>() {

    @Inject
    lateinit var sdk: Sdk

    companion object {
        private const val CAPTCHA_URL = "captcha_url"
        fun newInstance(url: String?): CaptchaDialog {
            return CaptchaDialog().apply {
                arguments = bundleOf(CAPTCHA_URL to url)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = DialogCaptchaBinding.inflate(inflater).apply { binding = this }.root

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding.captchaWebview) {
            with(settings) {
                javaScriptEnabled = true
                userAgentString = sdk.userAgent
            }

            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    view?.evaluateJavascript("document.getElementById('challenge-running') == undefined") {
                        if (it == "true") {
                            dismiss()
                        } else Timber.e("JS result: $it")
                    }
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                }
            }

            loadUrl(arguments?.getString(CAPTCHA_URL).orEmpty())
        }
    }
}
