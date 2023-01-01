package io.github.wulkanowy.ui.modules.login

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build.VERSION_CODES.TIRAMISU
import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import androidx.fragment.app.commit
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.pojos.RegisterUser
import io.github.wulkanowy.databinding.ActivityLoginBinding
import io.github.wulkanowy.ui.base.BaseActivity
import io.github.wulkanowy.ui.modules.login.advanced.LoginAdvancedFragment
import io.github.wulkanowy.ui.modules.login.form.LoginFormFragment
import io.github.wulkanowy.ui.modules.login.recover.LoginRecoverFragment
import io.github.wulkanowy.ui.modules.login.studentselect.LoginStudentSelectFragment
import io.github.wulkanowy.ui.modules.login.symbol.LoginSymbolFragment
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.notifications.NotificationsFragment
import io.github.wulkanowy.utils.AppInfo
import io.github.wulkanowy.utils.UpdateHelper
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : BaseActivity<LoginPresenter, ActivityLoginBinding>(), LoginView {

    @Inject
    override lateinit var presenter: LoginPresenter

    @Inject
    lateinit var updateHelper: UpdateHelper

    @Inject
    lateinit var appInfo: AppInfo

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

        if (savedInstanceState == null) {
            openFragment(LoginFormFragment.newInstance(), clearBackStack = true)
        }
    }

    override fun initView() {
        with(requireNotNull(supportActionBar)) {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) onBackPressedDispatcher.onBackPressed()
        return true
    }

    fun showActionBar(show: Boolean) {
        supportActionBar?.run { if (show) show() else hide() }
    }

    fun navigateToSymbolFragment(loginData: LoginData) {
        openFragment(LoginSymbolFragment.newInstance(loginData))
    }

    fun navigateToStudentSelect(loginData: LoginData, registerUser: RegisterUser) {
        openFragment(LoginStudentSelectFragment.newInstance(loginData, registerUser))
    }

    fun navigateToNotifications() {
        val isNotificationsPermissionRequired = appInfo.systemVersion >= TIRAMISU
        val isPermissionGranted = ContextCompat.checkSelfPermission(
            this, "android.permission.POST_NOTIFICATIONS"
        ) == PackageManager.PERMISSION_GRANTED

        if (isNotificationsPermissionRequired && !isPermissionGranted) {
            openFragment(NotificationsFragment.newInstance(), clearBackStack = true)
        } else navigateToFinish()
    }

    fun navigateToFinish() {
        startActivity(MainActivity.getStartIntent(this))
        finish()
    }

    fun onAdvancedLoginClick() {
        openFragment(LoginAdvancedFragment.newInstance())
    }

    fun onRecoverClick() {
        openFragment(LoginRecoverFragment.newInstance())
    }

    private fun openFragment(fragment: Fragment, clearBackStack: Boolean = false) {
        supportFragmentManager.popBackStack(fragment::class.java.name, POP_BACK_STACK_INCLUSIVE)

        supportFragmentManager.commit {
            replace(R.id.loginContainer, fragment)
            setReorderingAllowed(true)
            if (!clearBackStack) addToBackStack(fragment::class.java.name)
        }
    }

    override fun onResume() {
        super.onResume()
        updateHelper.onResume(this)
    }

    //https://developer.android.com/guide/playcore/in-app-updates#status_callback
    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        updateHelper.onActivityResult(requestCode, resultCode)
    }
}
