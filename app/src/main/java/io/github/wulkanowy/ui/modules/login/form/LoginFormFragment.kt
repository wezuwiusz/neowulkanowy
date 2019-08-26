package io.github.wulkanowy.ui.modules.login.form

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.view.inputmethod.EditorInfo.IME_NULL
import android.widget.ArrayAdapter
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.login.LoginActivity
import io.github.wulkanowy.utils.AppInfo
import io.github.wulkanowy.utils.hideSoftInput
import io.github.wulkanowy.utils.openInternetBrowser
import io.github.wulkanowy.utils.setOnTextChangedListener
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

    override val formNameValue: String
        get() = loginFormName.text.toString()

    override val formPassValue: String
        get() = loginFormPass.text.toString()

    override val formHostValue: String?
        get() = hostValues.getOrNull(hostKeys.indexOf(loginFormHost.text.toString()))

    private lateinit var hostKeys: Array<String>

    private lateinit var hostValues: Array<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login_form, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun initView() {
        hostKeys = resources.getStringArray(R.array.endpoints_keys)
        hostValues = resources.getStringArray(R.array.endpoints_values)

        loginFormName.setOnTextChangedListener { presenter.onNameTextChanged() }
        loginFormPass.setOnTextChangedListener { presenter.onPassTextChanged() }
        loginFormHost.setOnItemClickListener { _, _, _, _ -> presenter.onHostSelected() }
        loginFormSignIn.setOnClickListener { presenter.onSignInClick() }
        loginFormPrivacyLink.setOnClickListener { presenter.onPrivacyLinkClick() }

        loginFormPass.setOnEditorActionListener { _, id, _ ->
            if (id == IME_ACTION_DONE || id == IME_NULL) loginFormSignIn.callOnClick() else false
        }

        with(loginFormHost) {
            setText(hostKeys.getOrElse(0) { "" })
            setAdapter(ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, hostKeys))
            keyListener = null
        }
    }

    override fun setCredentials(name: String, pass: String) {
        loginFormName.setText(name)
        loginFormPass.setText(pass)
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

    override fun clearNameError() {
        loginFormNameLayout.error = null
    }

    override fun clearPassError() {
        loginFormPassLayout.error = null
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
        loginFormVersion.apply {
            visibility = VISIBLE
            text = "${getString(R.string.app_name)} ${appInfo.versionName}"
        }
    }

    override fun showPrivacyPolicy() {
        loginFormPrivacyLink.visibility = VISIBLE
    }

    override fun notifyParentAccountLogged(students: List<Student>) {
        (activity as? LoginActivity)?.onFormFragmentAccountLogged(students, Triple(
            loginFormName.text.toString(),
            loginFormPass.text.toString(),
            resources.getStringArray(R.array.endpoints_values)[1]
        ))
    }

    override fun openPrivacyPolicyPage() {
        context?.openInternetBrowser("https://wulkanowy.github.io/polityka-prywatnosci.html", ::showMessage)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDetachView()
    }
}
