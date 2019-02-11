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
import io.github.wulkanowy.BuildConfig.VERSION_NAME
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.login.LoginActivity
import io.github.wulkanowy.utils.hideSoftInput
import io.github.wulkanowy.utils.showSoftInput
import kotlinx.android.synthetic.main.fragment_login_form.*
import javax.inject.Inject

class LoginFormFragment : BaseFragment(), LoginFormView {

    @Inject
    lateinit var presenter: LoginFormPresenter

    companion object {
        fun newInstance() = LoginFormFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login_form, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun initView() {
        loginFormSignIn.setOnClickListener {
            presenter.attemptLogin(
                loginFormName.text.toString(),
                loginFormPass.text.toString(),
                resources.getStringArray(R.array.endpoints_values)[loginFormHost.selectedItemPosition]
            )
        }

        loginFormPass.setOnEditorActionListener { _, id, _ ->
            if (id == IME_ACTION_DONE || id == IME_NULL) loginFormSignIn.callOnClick() else false
        }

        context?.let {
            loginFormHost.adapter = ArrayAdapter.createFromResource(it, R.array.endpoints_keys, android.R.layout.simple_spinner_item)
                .apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        }
    }

    override fun setErrorNameRequired() {
        loginFormName.run {
            requestFocus()
            error = getString(R.string.login_field_required)
        }
    }

    override fun setErrorPassRequired(focus: Boolean) {
        loginFormPass.run {
            if (focus) requestFocus()
            error = getString(R.string.login_field_required)
        }
    }

    override fun setErrorPassInvalid(focus: Boolean) {
        loginFormPass.run {
            if (focus) requestFocus()
            error = getString(R.string.login_invalid_password)
        }
    }

    override fun setErrorPassIncorrect() {
        loginFormPass.run {
            requestFocus()
            error = getString(R.string.login_incorrect_password)
        }
    }

    override fun showSoftKeyboard() {
        activity?.showSoftInput()
    }

    override fun hideSoftKeyboard() {
        activity?.hideSoftInput()
    }

    override fun showProgress(show: Boolean) {
        loginFormProgressContainer.visibility = if (show) VISIBLE else GONE
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

    override fun notifyParentAccountLogged(students: List<Student>) {
        (activity as? LoginActivity)?.onFormFragmentAccountLogged(students, Triple(
            loginFormName.text.toString(),
            loginFormPass.text.toString(),
            resources.getStringArray(R.array.endpoints_values)[loginFormHost.selectedItemPosition]
        ))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDetachView()
    }
}
