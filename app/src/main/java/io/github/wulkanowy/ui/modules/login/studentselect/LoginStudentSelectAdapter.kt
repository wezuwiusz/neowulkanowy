package io.github.wulkanowy.ui.modules.login.studentselect

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.databinding.ItemLoginStudentSelectBinding
import javax.inject.Inject

class LoginStudentSelectAdapter @Inject constructor() :
    RecyclerView.Adapter<LoginStudentSelectAdapter.ItemViewHolder>() {

    var items = emptyList<Pair<Student, Boolean>>()

    var onClickListener: (Student, alreadySaved: Boolean) -> Unit = { _, _ -> }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        ItemLoginStudentSelectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val (student, alreadySaved) = items[position]

        with(holder.binding) {
            loginItemName.text = "${student.studentName} ${student.className}"
            loginItemSchool.text = student.schoolName
            loginItemName.isEnabled = !alreadySaved
            loginItemSchool.isEnabled = !alreadySaved
            loginItemSignedIn.visibility = if (alreadySaved) View.VISIBLE else View.GONE

            with(loginItemCheck) {
                isEnabled = !alreadySaved
                keyListener = null
                isChecked = false
            }

            root.setOnClickListener {
                onClickListener(student, alreadySaved)

                with(loginItemCheck) {
                    if (isEnabled) {
                        isChecked = !isChecked
                    }
                }
            }
        }
    }

    class ItemViewHolder(val binding: ItemLoginStudentSelectBinding) :
        RecyclerView.ViewHolder(binding.root)
}
