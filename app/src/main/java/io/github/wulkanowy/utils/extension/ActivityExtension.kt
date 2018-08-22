package io.github.wulkanowy.utils.extension

import android.app.Activity
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.inputmethod.InputMethodManager


fun Activity.showSoftInput() {
    (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?)?.run {
        if (currentFocus != null) showSoftInput(currentFocus, 0)
    }
}

fun Activity.hideSoftInput() {
    (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?)?.run {
        hideSoftInputFromWindow(window.decorView.applicationWindowToken, 0)
    }
}
