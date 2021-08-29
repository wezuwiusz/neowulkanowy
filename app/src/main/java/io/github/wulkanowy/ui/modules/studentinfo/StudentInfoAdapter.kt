package io.github.wulkanowy.ui.modules.studentinfo

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.databinding.ItemStudentInfoBinding
import javax.inject.Inject

class StudentInfoAdapter @Inject constructor() :
    RecyclerView.Adapter<StudentInfoAdapter.ViewHolder>() {

    var items = listOf<StudentInfoItem>()

    var onItemClickListener: (position: Int) -> Unit = {}

    var onItemLongClickListener: (text: String) -> Unit = {}

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemStudentInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        with(holder.binding) {
            studentInfoItemTitle.text = item.title
            studentInfoItemSubtitle.text = item.subtitle
            studentInfoItemArrow.visibility = if (item.showArrow) VISIBLE else GONE

            with(root) {
                setOnClickListener { onItemClickListener(position) }
                setOnLongClickListener {
                    onItemLongClickListener(studentInfoItemSubtitle.text.toString())
                    true
                }
            }
        }
    }

    class ViewHolder(val binding: ItemStudentInfoBinding) : RecyclerView.ViewHolder(binding.root)
}
