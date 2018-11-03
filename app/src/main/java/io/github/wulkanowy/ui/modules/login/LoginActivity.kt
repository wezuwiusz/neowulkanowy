package io.github.wulkanowy.ui.modules.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseActivity
import io.github.wulkanowy.ui.base.BasePagerAdapter
import io.github.wulkanowy.ui.modules.login.form.LoginFormFragment
import io.github.wulkanowy.ui.modules.login.options.LoginOptionsFragment
import io.github.wulkanowy.utils.setOnSelectPageListener
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

class LoginActivity : BaseActivity(), LoginView, LoginSwitchListener {

    @Inject
    lateinit var presenter: LoginPresenter

    @Inject
    lateinit var loginAdapter: BasePagerAdapter

    companion object {
        fun getStartIntent(context: Context) = Intent(context, LoginActivity::class.java)
    }

    override val currentViewIndex: Int
        get() = loginViewpager.currentItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        messageContainer = loginContainer

        presenter.onAttachView(this)
    }

    override fun onBackPressed() {
        presenter.onBackPressed { super.onBackPressed() }
    }

    override fun initAdapter() {
        loginAdapter.fragments.putAll(mapOf(
                "1" to LoginFormFragment.newInstance(),
                "2" to LoginOptionsFragment.newInstance()
        ))
        loginViewpager.run {
            adapter = loginAdapter
            setOnSelectPageListener { presenter.onPageSelected(it) }
        }
    }

    override fun switchFragment(position: Int) {
        presenter.onSwitchFragment(position)
    }

    override fun switchView(position: Int) {
        loginViewpager.setCurrentItem(position, false)
    }

    override fun hideActionBar() {
        supportActionBar?.hide()
    }

    override fun loadOptionsView(index: Int) {
        (loginAdapter.getItem(index) as LoginOptionsFragment).loadData()
    }

    public override fun onDestroy() {
        presenter.onDetachView()
        super.onDestroy()
    }
}
