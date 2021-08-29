package io.github.wulkanowy.ui.modules.debug.logviewer

import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LogViewerAdapter : RecyclerView.Adapter<LogViewerAdapter.ViewHolder>() {

    var lines = emptyList<String>()

    class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(TextView(parent.context))
    }

    override fun getItemCount() = lines.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = lines[position]
    }
}
