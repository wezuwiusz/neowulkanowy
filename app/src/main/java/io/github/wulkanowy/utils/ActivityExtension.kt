package io.github.wulkanowy.utils

import android.app.Activity
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService

fun Activity.showSoftInput() {
    getSystemService<InputMethodManager>()?.let { manager ->
        currentFocus?.let { manager.showSoftInput(it, 0) }
    }
}

fun Activity.hideSoftInput() {
    getSystemService<InputMethodManager>()?.hideSoftInputFromWindow(window.decorView.applicationWindowToken, 0)
}
