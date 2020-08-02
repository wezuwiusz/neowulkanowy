package io.github.wulkanowy.ui.base

import android.app.ActivityManager
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.LOLLIPOP
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_LONG
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.modules.login.LoginActivity
import io.github.wulkanowy.utils.FragmentLifecycleLogger
import io.github.wulkanowy.utils.getThemeAttrColor
import io.github.wulkanowy.utils.lifecycleAwareVariable
import javax.inject.Inject

abstract class BaseActivity<T : BasePresenter<out BaseView>, VB : ViewBinding> :
    AppCompatActivity(), BaseView {

    protected var binding: VB by lifecycleAwareVariable()

    @Inject
    lateinit var fragmentLifecycleLogger: FragmentLifecycleLogger

    @Inject
    lateinit var themeManager: ThemeManager

    protected var messageContainer: View? = null

    abstract var presenter: T

    override fun onCreate(savedInstanceState: Bundle?) {
        themeManager.applyActivityTheme(this)
        super.onCreate(savedInstanceState)
        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleLogger, true)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        if (SDK_INT >= LOLLIPOP) {
            @Suppress("DEPRECATION")
            setTaskDescription(ActivityManager.TaskDescription(null, null, getThemeAttrColor(R.attr.colorSurface)))
        }
    }

    override fun showError(text: String, error: Throwable) {
        if (messageContainer != null) {
            Snackbar.make(messageContainer!!, text, LENGTH_LONG)
                .setAction(R.string.all_details) { showErrorDetailsDialog(error) }
                .show()
        } else showMessage(text)
    }

    override fun showErrorDetailsDialog(error: Throwable) {
        ErrorDialog.newInstance(error).show(supportFragmentManager, error.toString())
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
}
