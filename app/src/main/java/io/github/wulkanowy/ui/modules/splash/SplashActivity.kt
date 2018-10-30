package io.github.wulkanowy.ui.modules.splash

import android.os.Bundle
import io.github.wulkanowy.ui.base.BaseActivity
import io.github.wulkanowy.ui.modules.login.LoginActivity
import io.github.wulkanowy.ui.modules.main.MainActivity
import javax.inject.Inject
import androidx.appcompat.app.AppCompatDelegate

class SplashActivity : BaseActivity(), SplashView {

    @Inject
    lateinit var presenter: SplashPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun openLoginView() {
        startActivity(LoginActivity.getStartIntent(this))
        finish()
    }

    override fun openMainView() {
        startActivity(MainActivity.getStartIntent(this))
        finish()
    }

    override fun setCurrentThemeMode(mode: Int) {
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    override fun onDestroy() {
        presenter.onDetachView()
        super.onDestroy()
    }
}
