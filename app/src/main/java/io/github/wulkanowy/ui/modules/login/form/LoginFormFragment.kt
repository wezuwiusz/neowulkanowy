package io.github.wulkanowy.ui.modules.login.form

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.login.LoginActivity
import io.github.wulkanowy.utils.AppInfo
import io.github.wulkanowy.utils.hideSoftInput
import io.github.wulkanowy.utils.openEmailClient
import io.github.wulkanowy.utils.openInternetBrowser
import io.github.wulkanowy.utils.setOnEditorDoneSignIn
import io.github.wulkanowy.utils.showSoftInput
import kotlinx.android.synthetic.main.fragment_login_form.*
import javax.inject.Inject

class LoginFormFragment : BaseFragment(), LoginFormView {

    @Inject
    lateinit var presenter: LoginFormPresenter

    @Inject
    lateinit var appInfo: AppInfo

    companion object {
        fun newInstance() = LoginFormFragment()
    }

    override val formUsernameValue: String
        get() = loginFormUsername.text.toString()

    override val formPassValue: String
        get() = loginFormPass.text.toString()

    override val formHostValue: String
        get() = hostValues.getOrNull(hostKeys.indexOf(loginFormHost.text.toString())).orEmpty()

    override val formHostSymbol: String
        get() = hostSymbols.getOrNull(hostKeys.indexOf(loginFormHost.text.toString())).orEmpty()

    override val formSymbolValue: String
        get() = loginFormSymbol.text.toString()

    override val nicknameLabel: String
        get() = getString(R.string.login_nickname_hint)

    override val emailLabel: String
        get() = getString(R.string.login_email_hint)

    private lateinit var hostKeys: Array<String>

    private lateinit var hostValues: Array<String>

    private lateinit var hostSymbols: Array<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login_form, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun initView() {
        hostKeys = resources.getStringArray(R.array.hosts_keys)
        hostValues = resources.getStringArray(R.array.hosts_values)
        hostSymbols = resources.getStringArray(R.array.hosts_symbols)

        loginFormUsername.doOnTextChanged { _, _, _, _ -> presenter.onUsernameTextChanged() }
        loginFormPass.doOnTextChanged { _, _, _, _ -> presenter.onPassTextChanged() }
        loginFormSymbol.doOnTextChanged { _, _, _, _ -> presenter.onSymbolTextChanged() }
        loginFormHost.setOnItemClickListener { _, _, _, _ -> presenter.onHostSelected() }
        loginFormSignIn.setOnClickListener { presenter.onSignInClick() }
        loginFormAdvancedButton.setOnClickListener { presenter.onAdvancedLoginClick() }
        loginFormPrivacyLink.setOnClickListener { presenter.onPrivacyLinkClick() }
        loginFormFaq.setOnClickListener { presenter.onFaqClick() }
        loginFormContactEmail.setOnClickListener { presenter.onEmailClick() }
        loginFormRecoverLink.setOnClickListener { presenter.onRecoverClick() }
        loginFormPass.setOnEditorDoneSignIn { loginFormSignIn.callOnClick() }
        loginFormSymbol.setOnEditorDoneSignIn { loginFormSignIn.callOnClick() }

        loginFormSymbol.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, resources.getStringArray(R.array.symbols_values)))

        with(loginFormHost) {
            setText(hostKeys.getOrNull(0).orEmpty())
            setAdapter(LoginSymbolAdapter(context, R.layout.support_simple_spinner_dropdown_item, hostKeys))
            setOnClickListener { if (loginFormContainer.visibility == GONE) dismissDropDown() }
        }
    }

    override fun setCredentials(username: String, pass: String) {
        loginFormUsername.setText(username)
        loginFormPass.setText(pass)
    }

    override fun setSymbol(symbol: String) {
        loginFormSymbol.setText(symbol)
    }

    override fun setUsernameLabel(label: String) {
        loginFormUsernameLayout.hint = label
    }

    override fun showSymbol(show: Boolean) {
        loginFormSymbolLayout.visibility = if (show) VISIBLE else GONE
    }

    override fun setErrorUsernameRequired() {
        with(loginFormUsernameLayout) {
            requestFocus()
            error = getString(R.string.login_field_required)
        }
    }

    override fun setErrorSymbolRequired(focus: Boolean) {
        with(loginFormSymbolLayout) {
            if (focus) requestFocus()
            error = getString(R.string.login_field_required)
        }
    }

    override fun setErrorPassRequired(focus: Boolean) {
        with(loginFormPassLayout) {
            if (focus) requestFocus()
            error = getString(R.string.login_field_required)
        }
    }

    override fun setErrorPassInvalid(focus: Boolean) {
        with(loginFormPassLayout) {
            if (focus) requestFocus()
            error = getString(R.string.login_invalid_password)
        }
    }

    override fun setErrorPassIncorrect() {
        with(loginFormPassLayout) {
            requestFocus()
            error = getString(R.string.login_incorrect_password)
        }
    }

    override fun clearUsernameError() {
        loginFormUsernameLayout.error = null
    }

    override fun clearPassError() {
        loginFormPassLayout.error = null
    }

    override fun clearSymbolError() {
        loginFormSymbolLayout.error = null
    }

    override fun showSoftKeyboard() {
        activity?.showSoftInput()
    }

    override fun hideSoftKeyboard() {
        activity?.hideSoftInput()
    }

    override fun showProgress(show: Boolean) {
        loginFormProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun showContent(show: Boolean) {
        loginFormContainer.visibility = if (show) VISIBLE else GONE
    }

    @SuppressLint("SetTextI18n")
    override fun showVersion() {
        loginFormVersion.text = "v${appInfo.versionName}"
    }

    override fun notifyParentAccountLogged(students: List<Student>, loginData: Triple<String, String, String>) {
        (activity as? LoginActivity)?.onFormFragmentAccountLogged(students, loginData)
    }

    override fun openPrivacyPolicyPage() {
        context?.openInternetBrowser("https://wulkanowy.github.io/polityka-prywatnosci.html", ::showMessage)
    }

    override fun showContact(show: Boolean) {
        loginFormContact.visibility = if (show) VISIBLE else GONE
    }

    override fun openAdvancedLogin() {
        (activity as? LoginActivity)?.onAdvancedLoginClick()
    }

    override fun onRecoverClick() {
        (activity as? LoginActivity)?.onRecoverClick()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDetachView()
    }

    override fun openFaqPage() {
        context?.openInternetBrowser("https://wulkanowy.github.io/czesto-zadawane-pytania/dlaczego-nie-moge-sie-zalogowac", ::showMessage)
    }

    override fun onResume() {
        super.onResume()
        with(presenter) {
            updateUsernameLabel()
            updateSymbolInputVisibility()
        }
    }

    override fun openEmail() {
        context?.openEmailClient(
            requireContext().getString(R.string.login_email_intent_title),
            "wulkanowyinc@gmail.com",
            requireContext().getString(R.string.login_email_subject),
            requireContext().getString(R.string.login_email_text, appInfo.systemModel, appInfo.systemVersion.toString(), appInfo.versionName)
        )
    }
}
