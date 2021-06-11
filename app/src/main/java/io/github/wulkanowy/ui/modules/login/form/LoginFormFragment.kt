package io.github.wulkanowy.ui.modules.login.form

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.widget.doOnTextChanged
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.databinding.FragmentLoginFormBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.login.LoginActivity
import io.github.wulkanowy.utils.AppInfo
import io.github.wulkanowy.utils.hideSoftInput
import io.github.wulkanowy.utils.openEmailClient
import io.github.wulkanowy.utils.openInternetBrowser
import io.github.wulkanowy.utils.setOnEditorDoneSignIn
import io.github.wulkanowy.utils.showSoftInput
import javax.inject.Inject

@AndroidEntryPoint
class LoginFormFragment : BaseFragment<FragmentLoginFormBinding>(R.layout.fragment_login_form),
    LoginFormView {

    @Inject
    lateinit var presenter: LoginFormPresenter

    @Inject
    lateinit var appInfo: AppInfo

    companion object {
        fun newInstance() = LoginFormFragment()
    }

    override val formUsernameValue: String
        get() = binding.loginFormUsername.text.toString()

    override val formPassValue: String
        get() = binding.loginFormPass.text.toString()

    override val formHostValue: String
        get() = hostValues.getOrNull(hostKeys.indexOf(binding.loginFormHost.text.toString())).orEmpty()

    override val formHostSymbol: String
        get() = hostSymbols.getOrNull(hostKeys.indexOf(binding.loginFormHost.text.toString())).orEmpty()

    override val nicknameLabel: String
        get() = getString(R.string.login_nickname_hint)

    override val emailLabel: String
        get() = getString(R.string.login_email_hint)

    private lateinit var hostKeys: Array<String>

    private lateinit var hostValues: Array<String>

    private lateinit var hostSymbols: Array<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginFormBinding.bind(view)
        presenter.onAttachView(this)
    }

    override fun initView() {
        hostKeys = resources.getStringArray(R.array.hosts_keys)
        hostValues = resources.getStringArray(R.array.hosts_values)
        hostSymbols = resources.getStringArray(R.array.hosts_symbols)

        with(binding) {
            loginFormUsername.doOnTextChanged { _, _, _, _ -> presenter.onUsernameTextChanged() }
            loginFormPass.doOnTextChanged { _, _, _, _ -> presenter.onPassTextChanged() }
            loginFormHost.setOnItemClickListener { _, _, _, _ -> presenter.onHostSelected() }
            loginFormSignIn.setOnClickListener { presenter.onSignInClick() }
            loginFormAdvancedButton.setOnClickListener { presenter.onAdvancedLoginClick() }
            loginFormPrivacyLink.setOnClickListener { presenter.onPrivacyLinkClick() }
            loginFormFaq.setOnClickListener { presenter.onFaqClick() }
            loginFormContactEmail.setOnClickListener { presenter.onEmailClick() }
            loginFormRecoverLink.setOnClickListener { presenter.onRecoverClick() }
            loginFormRecoverLinkSecond.setOnClickListener { presenter.onRecoverClick() }
            loginFormPass.setOnEditorDoneSignIn { loginFormSignIn.callOnClick() }
        }

        with(binding.loginFormHost) {
            setText(hostKeys.getOrNull(0).orEmpty())
            setAdapter(LoginSymbolAdapter(context, R.layout.support_simple_spinner_dropdown_item, hostKeys))
            setOnClickListener { if (binding.loginFormContainer.visibility == GONE) dismissDropDown() }
        }
    }

    override fun getHostsValues(): List<String> = hostValues.toList()

    override fun setCredentials(username: String, pass: String) {
        with(binding) {
            loginFormUsername.setText(username)
            loginFormPass.setText(pass)
        }
    }

    override fun setHost(host: String) {
        binding.loginFormHost.setText(
            hostKeys.getOrNull(hostValues.indexOf(host)).orEmpty()
        )
    }

    override fun setUsernameLabel(label: String) {
        binding.loginFormUsernameLayout.hint = label
    }

    override fun setErrorUsernameRequired() {
        with(binding.loginFormUsernameLayout) {
            error = getString(R.string.login_field_required)
        }
    }

    override fun setErrorLoginRequired() {
        with(binding.loginFormUsernameLayout) {
            error = getString(R.string.login_invalid_login)
        }
    }

    override fun setErrorEmailRequired() {
        with(binding.loginFormUsernameLayout) {
            error = getString(R.string.login_invalid_email)
        }
    }

    override fun setErrorPassRequired(focus: Boolean) {
        with(binding.loginFormPassLayout) {
            error = getString(R.string.login_field_required)
        }
    }

    override fun setErrorPassInvalid(focus: Boolean) {
        with(binding.loginFormPassLayout) {
            error = getString(R.string.login_invalid_password)
        }
    }

    override fun setErrorPassIncorrect() {
        with(binding.loginFormPassLayout) {
            error = getString(R.string.login_incorrect_password)
        }
    }

    override fun setErrorEmailInvalid(domain: String) {
        with(binding.loginFormUsernameLayout) {
            error = getString(R.string.login_invalid_custom_email,domain)
        }
    }

    override fun clearUsernameError() {
        binding.loginFormUsernameLayout.error = null
    }

    override fun clearPassError() {
        binding.loginFormPassLayout.error = null
    }

    override fun showSoftKeyboard() {
        activity?.showSoftInput()
    }

    override fun hideSoftKeyboard() {
        activity?.hideSoftInput()
    }

    override fun showProgress(show: Boolean) {
        binding.loginFormProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun showContent(show: Boolean) {
        binding.loginFormContainer.visibility = if (show) VISIBLE else GONE
    }

    @SuppressLint("SetTextI18n")
    override fun showVersion() {
        binding.loginFormVersion.text = "v${appInfo.versionName}"
    }

    override fun notifyParentAccountLogged(studentsWithSemesters: List<StudentWithSemesters>, loginData: Triple<String, String, String>) {
        (activity as? LoginActivity)?.onFormFragmentAccountLogged(studentsWithSemesters, loginData)
    }

    override fun openPrivacyPolicyPage() {
        context?.openInternetBrowser("https://wulkanowy.github.io/polityka-prywatnosci.html", ::showMessage)
    }

    override fun showContact(show: Boolean) {
        binding.loginFormContact.visibility = if (show) VISIBLE else GONE
        binding.loginFormRecoverLink.visibility = if (show) GONE else VISIBLE
    }

    override fun openAdvancedLogin() {
        (activity as? LoginActivity)?.onAdvancedLoginClick()
    }

    override fun onRecoverClick() {
        (activity as? LoginActivity)?.onRecoverClick()
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }

    override fun openFaqPage() {
        context?.openInternetBrowser("https://wulkanowy.github.io/czesto-zadawane-pytania/dlaczego-nie-moge-sie-zalogowac", ::showMessage)
    }

    override fun onResume() {
        super.onResume()
        presenter.updateUsernameLabel()
    }

    override fun openEmail(lastError: String) {
        context?.openEmailClient(
            chooserTitle = requireContext().getString(R.string.login_email_intent_title),
            email = "wulkanowyinc@gmail.com",
            subject = requireContext().getString(R.string.login_email_subject),
            body = requireContext().getString(R.string.login_email_text,
                "${appInfo.systemManufacturer} ${appInfo.systemModel}",
                appInfo.systemVersion.toString(),
                appInfo.versionName,
                "$formHostValue/$formHostSymbol",
                lastError
            )
        )
    }
}
