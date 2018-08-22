package io.github.wulkanowy.ui.base

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.Snackbar.LENGTH_LONG
import android.support.v7.app.AppCompatDelegate
import android.view.View
import dagger.android.support.DaggerAppCompatActivity
import io.github.wulkanowy.R

abstract class BaseActivity : DaggerAppCompatActivity(), BaseView {

    protected lateinit var messageView: View

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    override fun showMessage(text: String) {
        Snackbar.make(messageView, text, LENGTH_LONG).show()

    }

    override fun showNoNetworkMessage() {
        showMessage(getString(R.string.noInternet_text))
    }

    override fun onDestroy() {
        super.onDestroy()
        invalidateOptionsMenu()
    }
}
