package io.github.wulkanowy.ui.base

import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_LONG
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.modules.login.LoginActivity
import io.github.wulkanowy.utils.FragmentLifecycleLogger
import javax.inject.Inject

abstract class BaseActivity<T : BasePresenter<out BaseView>> : AppCompatActivity(), BaseView, HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var fragmentLifecycleLogger: FragmentLifecycleLogger

    @Inject
    lateinit var themeManager: ThemeManager

    protected var messageContainer: View? = null

    abstract var presenter: T

    public override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        themeManager.applyTheme(this)
        super.onCreate(savedInstanceState)
        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleLogger, true)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    override fun showError(text: String, error: Throwable) {
        if (messageContainer != null) {
            Snackbar.make(messageContainer!!, text, LENGTH_LONG)
                .setAction(R.string.all_details) {
                    ErrorDialog.newInstance(error).show(supportFragmentManager, error.toString())
                }
                .show()
        } else showMessage(text)
    }

    override fun showMessage(text: String) {
        if (messageContainer != null) Snackbar.make(messageContainer!!, text, LENGTH_LONG).show()
        else Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    override fun showExpiredDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.main_session_expired)
            .setMessage(R.string.main_session_relogin)
            .setPositiveButton(R.string.main_log_in) { _, _ -> presenter.onExpiredLoginSelected() }
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .show()
    }

    override fun openClearLoginView() {
        startActivity(LoginActivity.getStartIntent(this)
            .apply { addFlags(FLAG_ACTIVITY_CLEAR_TASK or FLAG_ACTIVITY_NEW_TASK) })
    }

    override fun onDestroy() {
        super.onDestroy()
        invalidateOptionsMenu()
        presenter.onDetachView()
    }

    override fun androidInjector() = androidInjector
}
