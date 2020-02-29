package io.github.wulkanowy.utils

import android.view.inputmethod.EditorInfo
import android.widget.EditText

fun EditText.setOnEditorDoneSignIn(callback: () -> Boolean) {
    setOnEditorActionListener { _, id, _ ->
        if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) callback() else false
    }
}
