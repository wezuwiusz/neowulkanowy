package io.github.wulkanowy.utils

import android.view.View
import android.widget.AdapterView
import android.widget.Spinner

/**
 * @see <a href="https://stackoverflow.com/a/29602298">How to keep onItemSelected from firing off on a newly instantiated Spinner?</a>
 */
inline fun Spinner.setOnItemSelectedListener(crossinline listener: (view: View?) -> Unit) {
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    listener(view)
                }
            }
        }
    }
}
