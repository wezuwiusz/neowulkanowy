package io.github.wulkanowy.ui.modules.login.symbol

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.view.inputmethod.EditorInfo.IME_NULL
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.databinding.FragmentLoginSymbolBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.login.LoginActivity
import io.github.wulkanowy.utils.AppInfo
import io.github.wulkanowy.utils.hideSoftInput
import io.github.wulkanowy.utils.openEmailClient
import io.github.wulkanowy.utils.openInternetBrowser
import io.github.wulkanowy.utils.showSoftInput
import javax.inject.Inject

@AndroidEntryPoint
class LoginSymbolFragment :
    BaseFragment<FragmentLoginSymbolBinding>(R.layout.fragment_login_symbol), LoginSymbolView {

    @Inject
    lateinit var presenter: LoginSymbolPresenter

    @Inject
    lateinit var appInfo: AppInfo

    companion object {
        private const val SAVED_LOGIN_DATA = "LOGIN_DATA"

        fun newInstance() = LoginSymbolFragment()
    }

    override val symbolNameError: CharSequence?
        get() = binding.loginSymbolNameLayout.error

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginSymbolBinding.bind(view)
        presenter.onAttachView(this, savedInstanceState?.getSerializable(SAVED_LOGIN_DATA))
    }

    override fun initView() {
        with(binding) {
            loginSymbolSignIn.setOnClickListener { presenter.attemptLogin(loginSymbolName.text.toString()) }
            loginSymbolFaq.setOnClickListener { presenter.onFaqClick() }
            loginSymbolContactEmail.setOnClickListener { presenter.onEmailClick() }

            loginSymbolName.doOnTextChanged { _, _, _, _ -> presenter.onSymbolTextChanged() }

            loginSymbolName.apply {
                setOnEditorActionListener { _, id, _ ->
                    if (id == IME_ACTION_DONE || id == IME_NULL) loginSymbolSignIn.callOnClick() else false
                }
                setAdapter(ArrayAdapter(context, android.R.layout.simple_list_item_1, resources.getStringArray(R.array.symbols_values)))
            }
        }
    }

    fun onParentInitSymbolFragment(loginData: Triple<String, String, String>) {
        presenter.onParentInitSymbolView(loginData)
    }

    override fun setErrorSymbolIncorrect() {
        binding.loginSymbolNameLayout.apply {
            requestFocus()
            error = getString(R.string.login_incorrect_symbol)
        }
    }

    override fun setErrorSymbolRequire() {
        binding.loginSymbolNameLayout.apply {
            requestFocus()
            error = getString(R.string.error_field_required)
        }
    }

    override fun clearSymbolError() {
        binding.loginSymbolNameLayout.error = null
    }

    override fun clearAndFocusSymbol() {
        binding.loginSymbolNameLayout.apply {
            editText?.text = null
            requestFocus()
        }
    }

    override fun showSoftKeyboard() {
        activity?.showSoftInput()
    }

    override fun hideSoftKeyboard() {
        activity?.hideSoftInput()
    }

    override fun showProgress(show: Boolean) {
        binding.loginSymbolProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun showContent(show: Boolean) {
        binding.loginSymbolContainer.visibility = if (show) VISIBLE else GONE
    }

    override fun notifyParentAccountLogged(studentsWithSemesters: List<StudentWithSemesters>) {
        (activity as? LoginActivity)?.onSymbolFragmentAccountLogged(studentsWithSemesters)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(SAVED_LOGIN_DATA, presenter.loginData)
    }

    override fun showContact(show: Boolean) {
        binding.loginSymbolContact.visibility = if (show) VISIBLE else GONE
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }

    override fun openFaqPage() {
        context?.openInternetBrowser("https://wulkanowy.github.io/czesto-zadawane-pytania/co-to-jest-symbol", ::showMessage)
    }

    override fun openEmail(host: String, lastError: String) {
        context?.openEmailClient(
            chooserTitle = requireContext().getString(R.string.login_email_intent_title),
            email = "wulkanowyinc@gmail.com",
            subject = requireContext().getString(R.string.login_email_subject),
            body = requireContext().getString(R.string.login_email_text,
                "${appInfo.systemManufacturer} ${appInfo.systemModel}",
                appInfo.systemVersion.toString(),
                appInfo.versionName,
                "$host/${binding.loginSymbolName.text}",
                lastError
            )
        )
    }
}
