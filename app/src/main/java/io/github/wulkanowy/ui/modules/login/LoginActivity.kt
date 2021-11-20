package io.github.wulkanowy.ui.modules.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.databinding.ActivityLoginBinding
import io.github.wulkanowy.ui.base.BaseActivity
import io.github.wulkanowy.ui.base.BaseFragmentPagerAdapter
import io.github.wulkanowy.ui.modules.login.advanced.LoginAdvancedFragment
import io.github.wulkanowy.ui.modules.login.form.LoginFormFragment
import io.github.wulkanowy.ui.modules.login.recover.LoginRecoverFragment
import io.github.wulkanowy.ui.modules.login.studentselect.LoginStudentSelectFragment
import io.github.wulkanowy.ui.modules.login.symbol.LoginSymbolFragment
import io.github.wulkanowy.utils.UpdateHelper
import io.github.wulkanowy.utils.setOnSelectPageListener
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : BaseActivity<LoginPresenter, ActivityLoginBinding>(), LoginView {

    @Inject
    override lateinit var presenter: LoginPresenter

    private val pagerAdapter by lazy {
        BaseFragmentPagerAdapter(
            fragmentManager = supportFragmentManager,
            pagesCount = 5,
            lifecycle = lifecycle,
        )
    }

    @Inject
    lateinit var updateHelper: UpdateHelper

    override val currentViewIndex get() = binding.loginViewpager.currentItem

    companion object {

        fun getStartIntent(context: Context) = Intent(context, LoginActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityLoginBinding.inflate(layoutInflater).apply { binding = this }.root)
        setSupportActionBar(binding.loginToolbar)
        messageContainer = binding.loginContainer
        updateHelper.messageContainer = binding.loginContainer

        presenter.onAttachView(this)
        updateHelper.checkAndInstallUpdates(this)
    }

    override fun onResume() {
        super.onResume()
        updateHelper.onResume(this)
    }

    //https://developer.android.com/guide/playcore/in-app-updates#status_callback
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        updateHelper.onActivityResult(requestCode, resultCode)
    }

    override fun initView() {
        with(requireNotNull(supportActionBar)) {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        with(binding.loginViewpager) {
            adapter = pagerAdapter
            isUserInputEnabled = false
            setOnSelectPageListener(presenter::onViewSelected)
        }

        with(pagerAdapter) {
            containerId = binding.loginViewpager.id
            itemFactory = {
                when (it) {
                    0 -> LoginFormFragment.newInstance()
                    1 -> LoginSymbolFragment.newInstance()
                    2 -> LoginStudentSelectFragment.newInstance()
                    3 -> LoginAdvancedFragment.newInstance()
                    4 -> LoginRecoverFragment.newInstance()
                    else -> throw IllegalStateException()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) onBackPressed()
        return true
    }

    override fun switchView(index: Int) {
        binding.loginViewpager.setCurrentItem(index, false)
    }

    override fun showActionBar(show: Boolean) {
        supportActionBar?.run { if (show) show() else hide() }
    }

    override fun onBackPressed() {
        presenter.onBackPressed { super.onBackPressed() }
    }

    override fun notifyInitSymbolFragment(loginData: Triple<String, String, String>) {
        (pagerAdapter.getFragmentInstance(1) as? LoginSymbolFragment)
            ?.onParentInitSymbolFragment(loginData)
    }

    override fun notifyInitStudentSelectFragment(studentsWithSemesters: List<StudentWithSemesters>) {
        (pagerAdapter.getFragmentInstance(2) as? LoginStudentSelectFragment)
            ?.onParentInitStudentSelectFragment(studentsWithSemesters)
    }

    fun onFormFragmentAccountLogged(
        studentsWithSemesters: List<StudentWithSemesters>,
        loginData: Triple<String, String, String>
    ) {
        presenter.onFormViewAccountLogged(studentsWithSemesters, loginData)
    }

    fun onSymbolFragmentAccountLogged(studentsWithSemesters: List<StudentWithSemesters>) {
        presenter.onSymbolViewAccountLogged(studentsWithSemesters)
    }

    fun onAdvancedLoginClick() {
        presenter.onAdvancedLoginClick()
    }

    fun onRecoverClick() {
        presenter.onRecoverClick()
    }
}
