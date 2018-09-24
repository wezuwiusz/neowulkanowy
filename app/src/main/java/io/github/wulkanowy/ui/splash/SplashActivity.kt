package io.github.wulkanowy.ui.splash

import android.os.Bundle
import io.github.wulkanowy.ui.base.BaseActivity
import io.github.wulkanowy.ui.login.LoginActivity
import io.github.wulkanowy.ui.main.MainActivity
import javax.inject.Inject

class SplashActivity : BaseActivity(), SplashView {

    @Inject
    lateinit var presenter: SplashPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun openLoginView() {
        startActivity(LoginActivity.getStartIntent(this))
        finish()
    }

    override fun openMainView() {
        startActivity(MainActivity.getStartIntent(this))
        finish()
    }
}
