package io.github.wulkanowy.ui.modules.account

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.databinding.ItemAccountBinding
import io.github.wulkanowy.utils.getThemeAttrColor
import javax.inject.Inject

class AccountAdapter @Inject constructor() : RecyclerView.Adapter<AccountAdapter.ItemViewHolder>() {

    var items = emptyList<Student>()

    var onClickListener: (Student) -> Unit = {}

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        ItemAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val student = items[position]

        with(holder.binding) {
            accountItemName.text = "${student.studentName} ${student.className}"
            accountItemSchool.text = student.schoolName

            with(accountItemImage) {
                val colorImage = if (student.isCurrent) context.getThemeAttrColor(R.attr.colorPrimary)
                else context.getThemeAttrColor(R.attr.colorOnSurface, 153)

                setColorFilter(colorImage, PorterDuff.Mode.SRC_IN)
            }

            root.setOnClickListener { onClickListener(student) }
        }
    }

    class ItemViewHolder(val binding: ItemAccountBinding) : RecyclerView.ViewHolder(binding.root)
}
