package io.github.wulkanowy.ui.modules.login.advanced

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.databinding.FragmentLoginAdvancedBinding
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.login.LoginActivity
import io.github.wulkanowy.ui.modules.login.form.LoginSymbolAdapter
import io.github.wulkanowy.utils.hideSoftInput
import io.github.wulkanowy.utils.setOnEditorDoneSignIn
import io.github.wulkanowy.utils.showSoftInput
import javax.inject.Inject

@AndroidEntryPoint
class LoginAdvancedFragment :
    BaseFragment<FragmentLoginAdvancedBinding>(R.layout.fragment_login_advanced),
    LoginAdvancedView {

    @Inject
    lateinit var presenter: LoginAdvancedPresenter

    companion object {
        fun newInstance() = LoginAdvancedFragment()
    }

    override val formLoginType: String
        get() = when (binding.loginTypeSwitch.checkedRadioButtonId) {
            R.id.loginTypeApi -> "API"
            R.id.loginTypeScrapper -> "SCRAPPER"
            else -> "HYBRID"
        }

    override val formUsernameValue: String
        get() = binding.loginFormUsername.text.toString().trim()

    override val formPassValue: String
        get() = binding.loginFormPass.text.toString().trim()

    private lateinit var hostKeys: Array<String>

    private lateinit var hostValues: Array<String>

    private lateinit var hostSymbols: Array<String>

    override val formHostValue: String
        get() = hostValues.getOrNull(hostKeys.indexOf(binding.loginFormHost.text.toString()))
            .orEmpty()

    override val formHostSymbol: String
        get() = hostSymbols.getOrNull(hostKeys.indexOf(binding.loginFormHost.text.toString()))
            .orEmpty()

    override val formPinValue: String
        get() = binding.loginFormPin.text.toString().trim()

    override val formSymbolValue: String
        get() = binding.loginFormSymbol.text.toString().trim()

    override val formTokenValue: String
        get() = binding.loginFormToken.text.toString().trim()

    override val nicknameLabel: String
        get() = getString(R.string.login_nickname_hint)

    override val emailLabel: String
        get() = getString(R.string.login_email_hint)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginAdvancedBinding.bind(view)
        presenter.onAttachView(this)
    }

    override fun initView() {
        hostKeys = resources.getStringArray(R.array.hosts_keys)
        hostValues = resources.getStringArray(R.array.hosts_values)
        hostSymbols = resources.getStringArray(R.array.hosts_symbols)

        with(binding) {
            loginFormUsername.doOnTextChanged { _, _, _, _ -> presenter.onUsernameTextChanged() }
            loginFormPass.doOnTextChanged { _, _, _, _ -> presenter.onPassTextChanged() }
            loginFormPin.doOnTextChanged { _, _, _, _ -> presenter.onPinTextChanged() }
            loginFormSymbol.doOnTextChanged { _, _, _, _ -> presenter.onSymbolTextChanged() }
            loginFormToken.doOnTextChanged { _, _, _, _ -> presenter.onTokenTextChanged() }
            loginFormHost.setOnItemClickListener { _, _, _, _ -> presenter.onHostSelected() }
            loginFormSignIn.setOnClickListener { presenter.onSignInClick() }

            loginTypeSwitch.setOnCheckedChangeListener { _, checkedId ->
                presenter.onLoginModeSelected(
                    when (checkedId) {
                        R.id.loginTypeApi -> Sdk.Mode.API
                        R.id.loginTypeScrapper -> Sdk.Mode.SCRAPPER
                        else -> Sdk.Mode.HYBRID
                    }
                )
            }

            loginFormPin.setOnEditorDoneSignIn { loginFormSignIn.callOnClick() }
            loginFormPass.setOnEditorDoneSignIn { loginFormSignIn.callOnClick() }

            loginFormSymbol.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    resources.getStringArray(R.array.symbols_values)
                )
            )
        }

        with(binding.loginFormHost) {
            setText(hostKeys.getOrNull(0).orEmpty())
            setAdapter(
                LoginSymbolAdapter(
                    context,
                    R.layout.support_simple_spinner_dropdown_item,
                    hostKeys
                )
            )
            setOnClickListener { if (binding.loginFormContainer.visibility == GONE) dismissDropDown() }
        }
    }

    override fun showMobileApiWarningMessage() {
        binding.loginFormAdvancedWarningInfo.text =
            getString(R.string.login_advanced_warning_mobile_api)
    }

    override fun showScraperWarningMessage() {
        binding.loginFormAdvancedWarningInfo.text =
            getString(R.string.login_advanced_warning_scraper)
    }

    override fun showHybridWarningMessage() {
        binding.loginFormAdvancedWarningInfo.text =
            getString(R.string.login_advanced_warning_hybrid)
    }

    override fun setDefaultCredentials(
        username: String,
        pass: String,
        symbol: String,
        token: String,
        pin: String
    ) {
        with(binding) {
            loginFormUsername.setText(username)
            loginFormPass.setText(pass)
            loginFormToken.setText(token)
            loginFormSymbol.setText(symbol)
            loginFormPin.setText(pin)
        }
    }

    override fun setUsernameLabel(label: String) {
        binding.loginFormUsernameLayout.hint = label
    }

    override fun setSymbol(symbol: String) {
        binding.loginFormSymbol.setText(symbol)
    }

    override fun setErrorUsernameRequired() {
        with(binding.loginFormUsernameLayout) {
            requestFocus()
            error = getString(R.string.error_field_required)
        }
    }

    override fun setErrorLoginRequired() {
        with(binding.loginFormUsernameLayout) {
            requestFocus()
            error = getString(R.string.login_invalid_login)
        }
    }

    override fun setErrorEmailRequired() {
        with(binding.loginFormUsernameLayout) {
            requestFocus()
            error = getString(R.string.login_invalid_email)
        }
    }

    override fun setErrorPassRequired(focus: Boolean) {
        with(binding.loginFormPassLayout) {
            if (focus) requestFocus()
            error = getString(R.string.error_field_required)
        }
    }

    override fun setErrorPassInvalid(focus: Boolean) {
        with(binding.loginFormPassLayout) {
            if (focus) requestFocus()
            error = getString(R.string.login_invalid_password)
        }
    }

    override fun setErrorPassIncorrect(message: String?) {
        with(binding.loginFormPassLayout) {
            requestFocus()
            error = message ?: getString(R.string.login_incorrect_password)
        }
    }

    override fun setErrorPinRequired() {
        with(binding.loginFormPinLayout) {
            requestFocus()
            error = getString(R.string.error_field_required)
        }
    }

    override fun setErrorPinInvalid(message: String) {
        with(binding.loginFormPinLayout) {
            requestFocus()
            error = message
        }
    }

    override fun setErrorSymbolRequired() {
        with(binding.loginFormSymbolLayout) {
            requestFocus()
            error = getString(R.string.error_field_required)
        }
    }

    override fun setErrorSymbolInvalid(message: String) {
        with(binding.loginFormSymbolLayout) {
            requestFocus()
            error = message
        }
    }

    override fun setErrorTokenRequired() {
        with(binding.loginFormTokenLayout) {
            requestFocus()
            error = getString(R.string.error_field_required)
        }
    }

    override fun setErrorTokenInvalid(message: String) {
        with(binding.loginFormTokenLayout) {
            requestFocus()
            error = message
        }
    }

    override fun clearUsernameError() {
        binding.loginFormUsernameLayout.error = null
    }

    override fun clearPassError() {
        binding.loginFormPassLayout.error = null
    }

    override fun clearPinKeyError() {
        binding.loginFormPinLayout.error = null
    }

    override fun clearSymbolError() {
        binding.loginFormSymbolLayout.error = null
    }

    override fun clearTokenError() {
        binding.loginFormTokenLayout.error = null
    }

    override fun showOnlyHybridModeInputs() {
        with(binding) {
            loginFormUsernameLayout.visibility = VISIBLE
            loginFormPassLayout.visibility = VISIBLE
            loginFormHostLayout.visibility = VISIBLE
            loginFormPinLayout.visibility = GONE
            loginFormSymbolLayout.visibility = VISIBLE
            loginFormTokenLayout.visibility = GONE
        }
    }

    override fun showOnlyScrapperModeInputs() {
        with(binding) {
            loginFormUsernameLayout.visibility = VISIBLE
            loginFormPassLayout.visibility = VISIBLE
            loginFormHostLayout.visibility = VISIBLE
            loginFormPinLayout.visibility = GONE
            loginFormSymbolLayout.visibility = VISIBLE
            loginFormTokenLayout.visibility = GONE
        }
    }

    override fun showOnlyMobileApiModeInputs() {
        with(binding) {
            loginFormUsernameLayout.visibility = GONE
            loginFormPassLayout.visibility = GONE
            loginFormHostLayout.visibility = GONE
            loginFormPinLayout.visibility = VISIBLE
            loginFormSymbolLayout.visibility = VISIBLE
            loginFormTokenLayout.visibility = VISIBLE
        }
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

    override fun notifyParentAccountLogged(studentsWithSemesters: List<StudentWithSemesters>) {
        (activity as? LoginActivity)?.onFormFragmentAccountLogged(
            studentsWithSemesters, Triple(
                binding.loginFormUsername.text.toString(),
                binding.loginFormPass.text.toString(),
                resources.getStringArray(R.array.hosts_values)[1]
            )
        )
    }

    override fun onResume() {
        super.onResume()
        presenter.updateUsernameLabel()
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
