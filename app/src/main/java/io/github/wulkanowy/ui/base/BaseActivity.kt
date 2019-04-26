package io.github.wulkanowy.ui.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_LONG
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import io.github.wulkanowy.R
import io.github.wulkanowy.utils.FragmentLifecycleLogger
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity(), BaseView, HasSupportFragmentInjector {

    @Inject
    lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var fragmentLifecycleLogger: FragmentLifecycleLogger

    @Inject
    lateinit var themeManager: ThemeManager

    protected lateinit var messageContainer: View

    public override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        themeManager.applyTheme(this)
        super.onCreate(savedInstanceState)
        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleLogger, true)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    override fun showError(text: String, error: Throwable) {
        Snackbar.make(messageContainer, text, LENGTH_LONG).setAction(R.string.all_details) {
            ErrorDialog.newInstance(error).show(supportFragmentManager, error.toString())
        }.show()
    }

    override fun showMessage(text: String) {
        Snackbar.make(messageContainer, text, LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        invalidateOptionsMenu()
    }

    override fun supportFragmentInjector() = supportFragmentInjector
}
