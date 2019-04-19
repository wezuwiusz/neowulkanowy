package io.github.wulkanowy.ui.modules.splash

import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import dagger.android.support.DaggerAppCompatActivity
import io.github.wulkanowy.ui.modules.login.LoginActivity
import io.github.wulkanowy.ui.modules.main.MainActivity
import javax.inject.Inject

class SplashActivity : DaggerAppCompatActivity(), SplashView {

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

    override fun showError(text: String, error: Throwable) {
        showMessage(text)
    }

    override fun showMessage(text: String) {
        Toast.makeText(this, text, LENGTH_LONG).show()
    }

    override fun onDestroy() {
        presenter.onDetachView()
        super.onDestroy()
    }
}
