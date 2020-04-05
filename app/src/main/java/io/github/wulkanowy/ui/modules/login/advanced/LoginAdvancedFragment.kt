package io.github.wulkanowy.ui.modules.login.advanced

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
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.login.LoginActivity
import io.github.wulkanowy.ui.modules.login.form.LoginSymbolAdapter
import io.github.wulkanowy.utils.hideSoftInput
import io.github.wulkanowy.utils.setOnEditorDoneSignIn
import io.github.wulkanowy.utils.showSoftInput
import kotlinx.android.synthetic.main.fragment_login_advanced.*
import javax.inject.Inject

class LoginAdvancedFragment : BaseFragment(), LoginAdvancedView {

    @Inject
    lateinit var presenter: LoginAdvancedPresenter

    companion object {
        fun newInstance() = LoginAdvancedFragment()
    }

    override val formLoginType: String
        get() = when (loginTypeSwitch.checkedRadioButtonId) {
            R.id.loginTypeApi -> "API"
            R.id.loginTypeScrapper -> "SCRAPPER"
            else -> "HYBRID"
        }

    override val formUsernameValue: String
        get() = loginFormUsername.text.toString().trim()

    override val formPassValue: String
        get() = loginFormPass.text.toString().trim()

    private lateinit var hostKeys: Array<String>

    private lateinit var hostValues: Array<String>

    private lateinit var hostSymbols: Array<String>

    override val formHostValue: String
        get() = hostValues.getOrNull(hostKeys.indexOf(loginFormHost.text.toString())).orEmpty()

    override val formHostSymbol: String
        get() = hostSymbols.getOrNull(hostKeys.indexOf(loginFormHost.text.toString())).orEmpty()

    override val formPinValue: String
        get() = loginFormPin.text.toString().trim()

    override val formSymbolValue: String
        get() = loginFormSymbol.text.toString().trim()

    override val formTokenValue: String
        get() = loginFormToken.text.toString().trim()

    override val nicknameLabel: String
        get() = getString(R.string.login_nickname_hint)

    override val emailLabel: String
        get() = getString(R.string.login_email_hint)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login_advanced, container, false)
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
        loginFormPin.doOnTextChanged { _, _, _, _ -> presenter.onPinTextChanged() }
        loginFormSymbol.doOnTextChanged { _, _, _, _ -> presenter.onSymbolTextChanged() }
        loginFormToken.doOnTextChanged { _, _, _, _ -> presenter.onTokenTextChanged() }
        loginFormHost.setOnItemClickListener { _, _, _, _ -> presenter.onHostSelected() }
        loginFormSignIn.setOnClickListener { presenter.onSignInClick() }

        loginTypeSwitch.setOnCheckedChangeListener { _, checkedId ->
            presenter.onLoginModeSelected(when (checkedId) {
                R.id.loginTypeApi -> Sdk.Mode.API
                R.id.loginTypeScrapper -> Sdk.Mode.SCRAPPER
                else -> Sdk.Mode.HYBRID
            })
        }

        loginFormPin.setOnEditorDoneSignIn { loginFormSignIn.callOnClick() }
        loginFormPass.setOnEditorDoneSignIn { loginFormSignIn.callOnClick() }

        loginFormSymbol.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, resources.getStringArray(R.array.symbols_values)))

        with(loginFormHost) {
            setText(hostKeys.getOrNull(0).orEmpty())
            setAdapter(LoginSymbolAdapter(context, R.layout.support_simple_spinner_dropdown_item, hostKeys))
            setOnClickListener { if (loginFormContainer.visibility == GONE) dismissDropDown() }
        }
    }

    override fun showMobileApiWarningMessage() {
        loginFormAdvancedWarningInfo.text = getString(R.string.login_advanced_warning_mobile_api)
    }

    override fun showScraperWarningMessage() {
        loginFormAdvancedWarningInfo.text = getString(R.string.login_advanced_warning_scraper)
    }

    override fun showHybridWarningMessage() {
        loginFormAdvancedWarningInfo.text = getString(R.string.login_advanced_warning_hybrid)
    }

    override fun setDefaultCredentials(username: String, pass: String, symbol: String, token: String, pin: String) {
        loginFormUsername.setText(username)
        loginFormPass.setText(pass)
        loginFormToken.setText(token)
        loginFormSymbol.setText(symbol)
        loginFormPin.setText(pin)
    }

    override fun setUsernameLabel(label: String) {
        loginFormUsernameLayout.hint = label
    }

    override fun setSymbol(symbol: String) {
        loginFormSymbol.setText(symbol)
    }

    override fun setErrorUsernameRequired() {
        with(loginFormUsernameLayout) {
            requestFocus()
            error = getString(R.string.login_field_required)
        }
    }

    override fun setErrorLoginRequired() {
        with(loginFormUsernameLayout) {
            requestFocus()
            error = getString(R.string.login_invalid_login)
        }
    }

    override fun setErrorEmailRequired() {
        with(loginFormUsernameLayout) {
            requestFocus()
            error = getString(R.string.login_invalid_email)
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

    override fun setErrorPinRequired() {
        with(loginFormPinLayout) {
            requestFocus()
            error = getString(R.string.login_field_required)
        }
    }

    override fun setErrorPinInvalid(message: String) {
        with(loginFormPinLayout) {
            requestFocus()
            error = message
        }
    }

    override fun setErrorSymbolRequired() {
        with(loginFormSymbolLayout) {
            requestFocus()
            error = getString(R.string.login_field_required)
        }
    }

    override fun setErrorSymbolInvalid(message: String) {
        with(loginFormSymbolLayout) {
            requestFocus()
            error = message
        }
    }

    override fun setErrorTokenRequired() {
        with(loginFormTokenLayout) {
            requestFocus()
            error = getString(R.string.login_field_required)
        }
    }

    override fun setErrorTokenInvalid(message: String) {
        with(loginFormTokenLayout) {
            requestFocus()
            error = message
        }
    }

    override fun clearUsernameError() {
        loginFormUsernameLayout.error = null
    }

    override fun clearPassError() {
        loginFormPassLayout.error = null
    }

    override fun clearPinKeyError() {
        loginFormPinLayout.error = null
    }

    override fun clearSymbolError() {
        loginFormSymbolLayout.error = null
    }

    override fun clearTokenError() {
        loginFormTokenLayout.error = null
    }

    override fun showOnlyHybridModeInputs() {
        loginFormUsernameLayout.visibility = VISIBLE
        loginFormPassLayout.visibility = VISIBLE
        loginFormHostLayout.visibility = VISIBLE
        loginFormPinLayout.visibility = GONE
        loginFormSymbolLayout.visibility = VISIBLE
        loginFormTokenLayout.visibility = GONE
    }

    override fun showOnlyScrapperModeInputs() {
        loginFormUsernameLayout.visibility = VISIBLE
        loginFormPassLayout.visibility = VISIBLE
        loginFormHostLayout.visibility = VISIBLE
        loginFormPinLayout.visibility = GONE
        loginFormSymbolLayout.visibility = VISIBLE
        loginFormTokenLayout.visibility = GONE
    }

    override fun showOnlyMobileApiModeInputs() {
        loginFormUsernameLayout.visibility = GONE
        loginFormPassLayout.visibility = GONE
        loginFormHostLayout.visibility = GONE
        loginFormPinLayout.visibility = VISIBLE
        loginFormSymbolLayout.visibility = VISIBLE
        loginFormTokenLayout.visibility = VISIBLE
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

    override fun notifyParentAccountLogged(students: List<Student>) {
        (activity as? LoginActivity)?.onFormFragmentAccountLogged(students, Triple(
            loginFormUsername.text.toString(),
            loginFormPass.text.toString(),
            resources.getStringArray(R.array.hosts_values)[1]
        ))
    }

    override fun onResume() {
        super.onResume()
        presenter.updateUsernameLabel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDetachView()
    }
}
