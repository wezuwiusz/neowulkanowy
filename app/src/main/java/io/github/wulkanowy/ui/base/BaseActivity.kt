package io.github.wulkanowy.ui.base

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.Snackbar.LENGTH_LONG
import android.support.v7.app.AppCompatDelegate
import android.view.View
import dagger.android.support.DaggerAppCompatActivity

abstract class BaseActivity : DaggerAppCompatActivity(), BaseView {

    protected lateinit var messageContainer: View

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    override fun showMessage(text: String) {
        Snackbar.make(messageContainer, text, LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        invalidateOptionsMenu()
    }
}
