package io.github.wulkanowy.ui.modules.splash

import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AlertDialog
import androidx.viewbinding.ViewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseActivity
import io.github.wulkanowy.ui.modules.login.LoginActivity
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.utils.AppInfo
import io.github.wulkanowy.utils.openInternetBrowser
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : BaseActivity<SplashPresenter, ViewBinding>(), SplashView {

    @Inject
    lateinit var appInfo: AppInfo

    @Inject
    override lateinit var presenter: SplashPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.onAttachView(this, intent?.getStringExtra("external_url"))
    }

    override fun openLoginView() {
        startActivity(LoginActivity.getStartIntent(this))
        finish()
    }

    override fun openMainView() {
        startActivity(MainActivity.getStartIntent(this))
        finish()
    }

    override fun openExternalUrlAndFinish(url: String) {
        openInternetBrowser(url, ::showMessage)
        finish()
    }

    override fun showError(text: String, error: Throwable) {
        Toast.makeText(this, text, LENGTH_LONG).show()
    }

    override fun showKitkatView() {
        AlertDialog.Builder(this)
            .setTitle(R.string.drop_kitkat_title)
            .setMessage(R.string.drop_kitkat_content)
            .setPositiveButton(android.R.string.ok, null)
            .setNeutralButton(R.string.drop_kitkat_again) { _, _ -> presenter.onNeutralButtonSelected() }
            .setOnDismissListener { presenter.onKitkatViewDismissed() }
            .show()
    }
}
