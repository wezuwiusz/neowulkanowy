package io.github.wulkanowy.ui.modules.splash

import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import io.github.wulkanowy.ui.base.BaseActivity
import io.github.wulkanowy.ui.modules.login.LoginActivity
import io.github.wulkanowy.ui.modules.main.MainActivity
import javax.inject.Inject

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
    }

    override fun showError(text: String, error: Throwable) {
        Toast.makeText(this, text, LENGTH_LONG).show()
    }

    override fun onDestroy() {
        presenter.onDetachView()
        super.onDestroy()
    }
}
