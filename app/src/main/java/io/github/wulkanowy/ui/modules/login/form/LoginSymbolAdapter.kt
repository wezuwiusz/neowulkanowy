package io.github.wulkanowy.ui.modules.login.form

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter

class LoginSymbolAdapter(context: Context, resource: Int, objects: Array<out String>) :
    ArrayAdapter<String>(context, resource, objects) {

    override fun getFilter() = object : Filter() {

        override fun performFiltering(constraint: CharSequence?) = null

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {}
    }
}
