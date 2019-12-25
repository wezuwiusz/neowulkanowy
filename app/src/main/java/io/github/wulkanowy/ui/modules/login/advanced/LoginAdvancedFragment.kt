package io.github.wulkanowy.ui.modules.login.advanced

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
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

    override val formNameValue: String
        get() = loginFormName.text.toString().trim()

    override val formPassValue: String
        get() = loginFormPass.text.toString().trim()

    private lateinit var hostKeys: Array<String>

    private lateinit var hostValues: Array<String>

    override val formHostValue: String?
        get() = hostValues.getOrNull(hostKeys.indexOf(loginFormHost.text.toString()))

    override val formPinValue: String
        get() = loginFormPin.text.toString().trim()

    override val formSymbolValue: String
        get() = loginFormSymbol.text.toString().trim()

    override val formTokenValue: String
        get() = loginFormToken.text.toString().trim()

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

        loginFormName.doOnTextChanged { _, _, _, _ -> presenter.onNameTextChanged() }
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

        loginFormSymbol.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, resources.getStringArray(R.array.symbols_values)))

        with(loginFormHost) {
            setText(hostKeys.getOrElse(0) { "" })
            setAdapter(LoginSymbolAdapter(context, R.layout.support_simple_spinner_dropdown_item, hostKeys))
        }
    }

    override fun setDefaultCredentials(name: String, pass: String, symbol: String, token: String, pin: String) {
        loginFormName.setText(name)
        loginFormPass.setText(pass)
        loginFormToken.setText(token)
        loginFormSymbol.setText(symbol)
        loginFormPin.setText(pin)
    }

    override fun setErrorNameRequired() {
        loginFormNameLayout.run {
            requestFocus()
            error = getString(R.string.login_field_required)
        }
    }

    override fun setErrorPassRequired(focus: Boolean) {
        loginFormPassLayout.run {
            if (focus) requestFocus()
            error = getString(R.string.login_field_required)
        }
    }

    override fun setErrorPassInvalid(focus: Boolean) {
        loginFormPassLayout.run {
            if (focus) requestFocus()
            error = getString(R.string.login_invalid_password)
        }
    }

    override fun setErrorPassIncorrect() {
        loginFormPassLayout.run {
            requestFocus()
            error = getString(R.string.login_incorrect_password)
        }
    }

    override fun setErrorPinRequired() {
        loginFormPinLayout.run {
            requestFocus()
            error = getString(R.string.login_field_required)
        }
    }

    override fun setErrorSymbolRequired() {
        loginFormSymbolLayout.run {
            requestFocus()
            error = getString(R.string.login_field_required)
        }
    }

    override fun setErrorTokenRequired() {
        loginFormTokenLayout.run {
            requestFocus()
            error = getString(R.string.login_field_required)
        }
    }

    override fun clearNameError() {
        loginFormNameLayout.error = null
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
        loginFormNameLayout.visibility = View.VISIBLE
        loginFormPassLayout.visibility = View.VISIBLE
        loginFormHostLayout.visibility = View.VISIBLE
        loginFormPinLayout.visibility = View.GONE
        loginFormSymbolLayout.visibility = View.VISIBLE
        loginFormTokenLayout.visibility = View.GONE
    }

    override fun showOnlyScrapperModeInputs() {
        loginFormNameLayout.visibility = View.VISIBLE
        loginFormPassLayout.visibility = View.VISIBLE
        loginFormHostLayout.visibility = View.VISIBLE
        loginFormPinLayout.visibility = View.GONE
        loginFormSymbolLayout.visibility = View.VISIBLE
        loginFormTokenLayout.visibility = View.GONE
    }

    override fun showOnlyMobileApiModeInputs() {
        loginFormNameLayout.visibility = View.GONE
        loginFormPassLayout.visibility = View.GONE
        loginFormHostLayout.visibility = View.GONE
        loginFormPinLayout.visibility = View.VISIBLE
        loginFormSymbolLayout.visibility = View.VISIBLE
        loginFormTokenLayout.visibility = View.VISIBLE
    }

    override fun showSoftKeyboard() {
        activity?.showSoftInput()
    }

    override fun hideSoftKeyboard() {
        activity?.hideSoftInput()
    }

    override fun showProgress(show: Boolean) {
        loginFormProgress.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showContent(show: Boolean) {
        loginFormContainer.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun notifyParentAccountLogged(students: List<Student>) {
        (activity as? LoginActivity)?.onFormFragmentAccountLogged(students, Triple(
            loginFormName.text.toString(),
            loginFormPass.text.toString(),
            resources.getStringArray(R.array.hosts_values)[1]
        ))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDetachView()
    }
}
