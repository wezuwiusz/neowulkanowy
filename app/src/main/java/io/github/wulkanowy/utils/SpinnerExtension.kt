package io.github.wulkanowy.utils

import android.view.View
import android.widget.AdapterView
import android.widget.Spinner

inline fun Spinner.setOnItemSelectedListener(crossinline listener: (view: View?) -> Unit) {
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            listener(view)
        }
    }
}
