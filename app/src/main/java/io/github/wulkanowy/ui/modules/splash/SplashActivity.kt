package io.github.wulkanowy.ui.modules.splash

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.viewbinding.ViewBinding
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.services.shortcuts.ShortcutsHelper
import io.github.wulkanowy.ui.base.BaseActivity
import io.github.wulkanowy.ui.modules.Destination
import io.github.wulkanowy.ui.modules.login.LoginActivity
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.utils.openInternetBrowser
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity<SplashPresenter, ViewBinding>(), SplashView {

    @Inject
    override lateinit var presenter: SplashPresenter

    @Inject
    lateinit var shortcutsHelper: ShortcutsHelper

    companion object {

        private const val EXTRA_START_DESTINATION = "start_destination_json"

        private const val EXTRA_EXTERNAL_URL = "external_url"

        fun getStartIntent(context: Context, destination: Destination? = null) =
            Intent(context, SplashActivity::class.java).apply {
                destination?.let { putExtra(EXTRA_START_DESTINATION, Json.encodeToString(it)) }
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition { true }
        shortcutsHelper.initializeShortcuts()

        val externalLink = intent?.getStringExtra(EXTRA_EXTERNAL_URL)
        val startDestinationJson = intent?.getStringExtra(EXTRA_START_DESTINATION)

        presenter.onAttachView(this, externalLink, startDestinationJson)
    }

    override fun openLoginView() {
        startActivity(LoginActivity.getStartIntent(this))
        finish()
    }

    override fun openMainView(destination: Destination?) {
        startActivity(MainActivity.getStartIntent(this, destination))
        finish()
    }

    override fun openExternalUrlAndFinish(url: String) {
        openInternetBrowser(url, ::showMessage)
        finish()
    }

    override fun showError(text: String, error: Throwable) {
        Toast.makeText(this, text, LENGTH_LONG).show()
    }
}
