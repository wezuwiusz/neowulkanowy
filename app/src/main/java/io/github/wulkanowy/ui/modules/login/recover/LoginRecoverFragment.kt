package io.github.wulkanowy.ui.modules.login.recover

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.databinding.FragmentLoginRecoverBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.login.LoginActivity
import io.github.wulkanowy.ui.modules.login.form.LoginSymbolAdapter
import io.github.wulkanowy.utils.hideSoftInput
import io.github.wulkanowy.utils.showSoftInput
import javax.inject.Inject

@AndroidEntryPoint
class LoginRecoverFragment :
    BaseFragment<FragmentLoginRecoverBinding>(R.layout.fragment_login_recover), LoginRecoverView {

    private var _binding: FragmentLoginRecoverBinding? = null

    private val bindingLocal: FragmentLoginRecoverBinding get() = _binding!!

    @Inject
    lateinit var presenter: LoginRecoverPresenter

    companion object {
        fun newInstance() = LoginRecoverFragment()
    }

    private lateinit var hostKeys: Array<String>

    private lateinit var hostValues: Array<String>

    private lateinit var hostSymbols: Array<String>

    override val recoverHostValue: String
        get() = hostValues.getOrNull(hostKeys.indexOf(bindingLocal.loginRecoverHost.text.toString()))
            .orEmpty()

    override val formHostSymbol: String
        get() = hostSymbols.getOrNull(hostKeys.indexOf(bindingLocal.loginRecoverHost.text.toString()))
            .orEmpty()

    override val recoverNameValue: String
        get() = bindingLocal.loginRecoverName.text.toString().trim()

    override val emailHintString: String
        get() = getString(R.string.login_email_hint)

    override val loginPeselEmailHintString: String
        get() = getString(R.string.login_login_pesel_email_hint)

    override val invalidEmailString: String
        get() = getString(R.string.login_invalid_email)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginRecoverBinding.bind(view)
        presenter.onAttachView(this)
    }

    override fun initView() {
        hostKeys = resources.getStringArray(R.array.hosts_keys)
        hostValues = resources.getStringArray(R.array.hosts_values)
        hostSymbols = resources.getStringArray(R.array.hosts_symbols)

        with(bindingLocal) {
            loginRecoverWebView.setBackgroundColor(Color.TRANSPARENT)
            loginRecoverName.doOnTextChanged { _, _, _, _ -> presenter.onNameTextChanged() }
            loginRecoverHost.setOnItemClickListener { _, _, _, _ -> presenter.onHostSelected() }
            loginRecoverButton.setOnClickListener { presenter.onRecoverClick() }
            loginRecoverErrorRetry.setOnClickListener { presenter.onRecoverClick() }
            loginRecoverErrorDetails.setOnClickListener { presenter.onDetailsClick() }
            loginRecoverLogin.setOnClickListener { (activity as LoginActivity).switchView(0) }
        }

        with(bindingLocal.loginRecoverHost) {
            setText(hostKeys.getOrNull(0).orEmpty())
            setAdapter(
                LoginSymbolAdapter(context, R.layout.support_simple_spinner_dropdown_item, hostKeys)
            )
            setOnClickListener { if (bindingLocal.loginRecoverFormContainer.visibility == GONE) dismissDropDown() }
        }
    }

    override fun setDefaultCredentials(username: String) {
        bindingLocal.loginRecoverName.setText(username)
    }

    override fun setErrorNameRequired() {
        with(bindingLocal.loginRecoverNameLayout) {
            requestFocus()
            error = getString(R.string.error_field_required)
        }
    }

    override fun setUsernameHint(hint: String) {
        bindingLocal.loginRecoverNameLayout.hint = hint
    }

    override fun setUsernameError(message: String) {
        with(bindingLocal.loginRecoverNameLayout) {
            requestFocus()
            error = message
        }
    }

    override fun clearUsernameError() {
        bindingLocal.loginRecoverNameLayout.error = null
    }

    override fun showProgress(show: Boolean) {
        bindingLocal.loginRecoverProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun showRecoverForm(show: Boolean) {
        bindingLocal.loginRecoverFormContainer.visibility = if (show) VISIBLE else GONE
    }

    override fun showCaptcha(show: Boolean) {
        bindingLocal.loginRecoverCaptchaContainer.visibility = if (show) VISIBLE else GONE
    }

    override fun showErrorView(show: Boolean) {
        bindingLocal.loginRecoverError.visibility = if (show) VISIBLE else GONE
        bindingLocal.loginRecoverErrorDetails.isVisible = true
    }

    override fun setErrorDetails(message: String) {
        bindingLocal.loginRecoverErrorMessage.text = message
    }

    override fun showSuccessView(show: Boolean) {
        bindingLocal.loginRecoverSuccess.visibility = if (show) VISIBLE else GONE
    }

    override fun setSuccessTitle(title: String) {
        bindingLocal.loginRecoverSuccessTitle.text = title
    }

    override fun setSuccessMessage(message: String) {
        bindingLocal.loginRecoverSuccessMessage.text = message
    }

    override fun showSoftKeyboard() {
        activity?.showSoftInput()
    }

    override fun hideSoftKeyboard() {
        activity?.hideSoftInput()
    }

    @SuppressLint("SetJavaScriptEnabled", "AddJavascriptInterface")
    override fun loadReCaptcha(siteKey: String, url: String) {
        val html = """
            <div style="position: absolute; left: 50%; top: 50%; transform: translate(-50%, -50%);" id="recaptcha"></div>
            <script src="https://www.google.com/recaptcha/api.js?onload=cl&render=explicit&hl=pl" async defer></script>
            <script>var cl=()=>grecaptcha.render("recaptcha",{
            sitekey:'$siteKey',
            callback:e =>Android.captchaCallback(e)})</script>
        """.trimIndent()

        with(bindingLocal.loginRecoverWebView) {
            settings.javaScriptEnabled = true
            webViewClient = object : WebViewClient() {
                private var recoverWebViewSuccess = true

                override fun onPageFinished(view: WebView?, url: String?) {
                    if (recoverWebViewSuccess) {
                        showCaptcha(true)
                        showProgress(false)
                    } else {
                        showProgress(false)
                        showErrorView(true)
                        bindingLocal.loginRecoverErrorDetails.isVisible = false
                    }
                }

                override fun onReceivedError(
                    view: WebView,
                    errorCode: Int,
                    description: String,
                    failingUrl: String
                ) {
                    recoverWebViewSuccess = false
                }
            }

            loadDataWithBaseURL(url, html, "text/html", "UTF-8", null)
            addJavascriptInterface(object {

                @Suppress("UNUSED")
                @JavascriptInterface
                fun captchaCallback(reCaptchaResponse: String) {
                    activity?.runOnUiThread {
                        presenter.onReCaptchaVerified(reCaptchaResponse)
                    }
                }
            }, "Android")
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.updateFields()
    }

    override fun onDestroyView() {
        bindingLocal.loginRecoverWebView.destroy()
        _binding = null
        presenter.onDetachView()

        super.onDestroyView()
    }
}
