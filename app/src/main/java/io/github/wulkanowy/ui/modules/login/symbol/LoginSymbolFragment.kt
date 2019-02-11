package io.github.wulkanowy.ui.modules.login.symbol

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
import io.github.wulkanowy.utils.hideSoftInput
import io.github.wulkanowy.utils.showSoftInput
import kotlinx.android.synthetic.main.fragment_login_symbol.*
import javax.inject.Inject

class LoginSymbolFragment : BaseFragment(), LoginSymbolView {

    @Inject
    lateinit var presenter: LoginSymbolPresenter

    companion object {
        private const val SAVED_LOGIN_DATA = "LOGIN_DATA"

        fun newInstance() = LoginSymbolFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login_symbol, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.onAttachView(this, savedInstanceState?.getSerializable(SAVED_LOGIN_DATA))
    }

    override fun initView() {
        loginSymbolSignIn.setOnClickListener { presenter.attemptLogin(loginSymbolName.text.toString()) }

        loginSymbolName.apply {
            setOnEditorActionListener { _, id, _ ->
                if (id == IME_ACTION_DONE || id == IME_NULL) loginSymbolSignIn.callOnClick() else false
            }
            setAdapter(ArrayAdapter(context, android.R.layout.simple_list_item_1, resources.getStringArray(R.array.symbols_values)))
        }
    }

    fun onParentInitSymbolFragment(loginData: Triple<String, String, String>) {
        presenter.onParentInitSymbolView(loginData)
    }

    override fun setErrorSymbolIncorrect() {
        loginSymbolName.apply {
            requestFocus()
            error = getString(R.string.login_incorrect_symbol)
        }
    }

    override fun setErrorSymbolRequire() {
        loginSymbolName.apply {
            requestFocus()
            error = getString(R.string.login_field_required)
        }
    }

    override fun clearAndFocusSymbol() {
        loginSymbolName.apply {
            text = null
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
        loginSymbolProgressContainer.visibility = if (show) VISIBLE else GONE
    }

    override fun showContent(show: Boolean) {
        loginSymbolContainer.visibility = if (show) VISIBLE else GONE
    }

    override fun notifyParentAccountLogged(students: List<Student>) {
        (activity as? LoginActivity)?.onSymbolFragmentAccountLogged(students)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(SAVED_LOGIN_DATA, presenter.loginData)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDetachView()
    }
}
