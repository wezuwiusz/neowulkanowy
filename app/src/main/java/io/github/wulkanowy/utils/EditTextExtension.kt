package io.github.wulkanowy.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

inline fun EditText.setOnTextChangedListener(crossinline listener: () -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            listener()
        }

        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    })
}
