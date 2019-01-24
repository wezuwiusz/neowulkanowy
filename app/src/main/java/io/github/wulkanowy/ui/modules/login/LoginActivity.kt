package io.github.wulkanowy.ui.modules.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseActivity
import io.github.wulkanowy.ui.base.BaseFragmentPagerAdapter
import io.github.wulkanowy.ui.modules.login.form.LoginFormFragment
import io.github.wulkanowy.ui.modules.login.options.LoginOptionsFragment
import io.github.wulkanowy.utils.setOnSelectPageListener
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

class LoginActivity : BaseActivity(), LoginView {

    @Inject
    lateinit var presenter: LoginPresenter

    @Inject
    lateinit var loginAdapter: BaseFragmentPagerAdapter

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

    override fun initAdapter() {
        loginAdapter.addFragments(listOf(
            LoginFormFragment.newInstance(),
            LoginOptionsFragment.newInstance()
        ))

        loginViewpager.run {
            adapter = loginAdapter
            setOnSelectPageListener { presenter.onPageSelected(it) }
        }
    }

    override fun switchView(index: Int) {
        loginViewpager.setCurrentItem(index, false)
    }

    override fun notifyOptionsViewLoadData() {
        (loginAdapter.getFragmentInstance(1) as? LoginOptionsFragment)?.onParentLoadData()
    }

    fun onChildFragmentSwitchOptions() {
        presenter.onChildViewSwitchOptions()
    }

    override fun hideActionBar() {
        supportActionBar?.hide()
    }

    override fun onBackPressed() {
        presenter.onBackPressed { super.onBackPressed() }
    }

    public override fun onDestroy() {
        presenter.onDetachView()
        super.onDestroy()
    }
}
