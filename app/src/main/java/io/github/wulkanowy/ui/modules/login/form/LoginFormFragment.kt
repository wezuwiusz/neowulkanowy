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
import io.github.wulkanowy.BuildConfig.DEBUG
import io.github.wulkanowy.BuildConfig.VERSION_NAME
import io.github.wulkanowy.R
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

    override val isDebug = DEBUG

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login_form, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun initView() {
        loginSignButton.setOnClickListener {
            presenter.attemptLogin(
                loginNicknameEdit.text.toString(),
                loginPassEdit.text.toString(),
                loginSymbolEdit.text.toString(),
                resources.getStringArray(R.array.endpoints_values)[loginHostEdit.selectedItemPosition]
            )
        }

        loginPassEdit.setOnEditorActionListener { _, id, _ -> onEditAction(id) }

        loginHostEdit.apply {
            adapter = ArrayAdapter.createFromResource(context, R.array.endpoints_keys, android.R.layout.simple_spinner_item)
                .apply { setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        }

        loginSymbolEdit.run {
            setAdapter(ArrayAdapter(context, android.R.layout.simple_list_item_1, resources.getStringArray(R.array.symbols_values)))
            setOnEditorActionListener { _, id, _ -> onEditAction(id) }
        }
    }

    override fun showSymbolInput() {
        loginHeader.text = getString(R.string.login_header_symbol)
        loginMainForm.visibility = GONE
        loginSymbolInput.visibility = VISIBLE
        loginSymbolEdit.requestFocus()
        showSoftKeyboard()
    }

    @SuppressLint("SetTextI18n")
    override fun showVersion() {
        loginVersion.apply {
            visibility = VISIBLE
            text = "${getString(R.string.app_name)} $VERSION_NAME"
        }
    }

    override fun switchOptionsView() {
        (activity as? LoginActivity)?.onChildFragmentSwitchOptions()
    }

    override fun setErrorNicknameRequired() {
        loginNicknameEdit.run {
            requestFocus()
            error = getString(R.string.login_field_required)
        }
    }

    override fun setErrorPassRequired(focus: Boolean) {
        loginPassEdit.run {
            if (focus) requestFocus()
            error = getString(R.string.login_field_required)
        }
    }

    override fun setErrorPassInvalid(focus: Boolean) {
        loginPassEdit.run {
            if (focus) requestFocus()
            error = getString(R.string.login_invalid_password)
        }
    }

    override fun setErrorSymbolRequire() {
        loginSymbolEdit.run {
            requestFocus()
            error = getString(R.string.login_field_required)
        }
    }

    override fun setErrorPassIncorrect() {
        loginPassEdit.run {
            requestFocus()
            error = getString(R.string.login_incorrect_password)
        }
    }

    override fun setErrorSymbolIncorrect() {
        loginSymbolEdit.run {
            requestFocus()
            error = getString(R.string.login_incorrect_symbol)
        }
    }

    override fun resetViewErrors() {
        loginNicknameEdit.error = null
        loginPassEdit.error = null
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

    private fun onEditAction(actionId: Int): Boolean {
        return when (actionId) {
            IME_ACTION_DONE, IME_NULL -> loginSignButton.callOnClick()
            else -> false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDetachView()
    }
}
