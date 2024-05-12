package io.github.wulkanowy.ui.modules.captcha

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.os.bundleOf
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.WulkanowySdkFactory
import io.github.wulkanowy.databinding.DialogCaptchaBinding
import io.github.wulkanowy.ui.base.BaseDialogFragment
import io.github.wulkanowy.utils.WebkitCookieManagerProxy
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class CaptchaDialog : BaseDialogFragment<DialogCaptchaBinding>() {

    @Inject
    lateinit var wulkanowySdkFactory: WulkanowySdkFactory

    @Inject
    lateinit var webkitCookieManagerProxy: WebkitCookieManagerProxy

    private var webView: WebView? = null

    companion object {
        const val CAPTCHA_SUCCESS = "captcha_success"
        private const val CAPTCHA_URL = "captcha_url"
        private const val CAPTCHA_CHECK_JS = "document.getElementById('challenge-running') == null"

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
        isCancelable = false
        binding.captchaRefresh.setOnClickListener {
            binding.captchaWebview.loadUrl(arguments?.getString(CAPTCHA_URL).orEmpty())
        }
        binding.captchaClose.setOnClickListener { dismiss() }

        with(binding.captchaWebview) {
            webView = this
            with(settings) {
                javaScriptEnabled = true
                userAgentString = wulkanowySdkFactory.createBase().userAgent
            }

            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    view?.evaluateJavascript(CAPTCHA_CHECK_JS) {
                        if (it == "true") {
                            onChallengeAccepted()
                        }
                    }
                }
            }

            loadUrl(arguments?.getString(CAPTCHA_URL).orEmpty())
        }
    }

    private fun onChallengeAccepted() {
        runCatching { parentFragmentManager.setFragmentResult(CAPTCHA_SUCCESS, bundleOf()) }
            .onFailure { Timber.e(it) }
        showMessage(getString(R.string.captcha_verified_message))
        dismissAllowingStateLoss()
    }

    override fun onDestroy() {
        webkitCookieManagerProxy.webkitCookieManager?.flush()
        webView?.destroy()
        super.onDestroy()
    }
}
