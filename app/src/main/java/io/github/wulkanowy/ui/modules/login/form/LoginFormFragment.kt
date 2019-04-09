package io.github.wulkanowy.ui.modules.login.form

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.view.inputmethod.EditorInfo.IME_NULL
import android.widget.ArrayAdapter
import io.github.wulkanowy.BuildConfig.VERSION_NAME
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.login.LoginActivity
import io.github.wulkanowy.utils.hideSoftInput
import io.github.wulkanowy.utils.setOnItemSelectedListener
import io.github.wulkanowy.utils.setOnTextChangedListener
import io.github.wulkanowy.utils.showSoftInput
import kotlinx.android.synthetic.main.fragment_login_form.*
import javax.inject.Inject

class LoginFormFragment : BaseFragment(), LoginFormView {

    @Inject
    lateinit var presenter: LoginFormPresenter

    companion object {
        fun newInstance() = LoginFormFragment()
    }

    override val formNameValue: String
        get() = loginFormName.text.toString()

    override val formPassValue: String
        get() = loginFormPass.text.toString()

    override val formHostValue: String?
        get() = resources.getStringArray(R.array.endpoints_values)[loginFormHost.selectedItemPosition]

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login_form, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun initView() {
        loginFormName.setOnTextChangedListener { presenter.onNameTextChanged() }
        loginFormPass.setOnTextChangedListener { presenter.onPassTextChanged() }
        loginFormHost.setOnItemSelectedListener { presenter.onHostSelected() }
        loginFormSignIn.setOnClickListener { presenter.onSignInClick() }
        loginFormPrivacyLink.setOnClickListener { presenter.onPrivacyLinkClick() }

        loginFormPass.setOnEditorActionListener { _, id, _ ->
            if (id == IME_ACTION_DONE || id == IME_NULL) loginFormSignIn.callOnClick() else false
        }

        context?.let {
            loginFormHost.adapter = ArrayAdapter.createFromResource(it, R.array.endpoints_keys, android.R.layout.simple_spinner_item)
                .apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        }
    }

    override fun setDefaultCredentials(name: String, pass: String) {
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
            text = "${getString(R.string.app_name)} $VERSION_NAME"
        }
    }

    override fun showPrivacyPolicy() {
        loginFormPrivacyLink.visibility = VISIBLE
    }

    override fun notifyParentAccountLogged(students: List<Student>) {
        (activity as? LoginActivity)?.onFormFragmentAccountLogged(students, Triple(
            loginFormName.text.toString(),
            loginFormPass.text.toString(),
            resources.getStringArray(R.array.endpoints_values)[loginFormHost.selectedItemPosition]
        ))
    }

    override fun openPrivacyPolicyPage() {
        startActivity(Intent.parseUri("https://wulkanowy.github.io/polityka-prywatnosci.html", 0))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDetachView()
    }
}
