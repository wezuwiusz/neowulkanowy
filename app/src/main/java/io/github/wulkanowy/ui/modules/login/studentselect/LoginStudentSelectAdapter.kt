package io.github.wulkanowy.ui.modules.login.studentselect

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.databinding.ItemLoginStudentSelectBinding
import javax.inject.Inject

class LoginStudentSelectAdapter @Inject constructor() :
    RecyclerView.Adapter<LoginStudentSelectAdapter.ItemViewHolder>() {

    private val checkedList = mutableMapOf<Int, Boolean>()

    var items = emptyList<Pair<StudentWithSemesters, Boolean>>()
        set(value) {
            field = value
            checkedList.clear()
        }

    var onClickListener: (StudentWithSemesters, alreadySaved: Boolean) -> Unit = { _, _ -> }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ItemViewHolder(
        ItemLoginStudentSelectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val (studentAndSemesters, alreadySaved) = items[position]
        val student = studentAndSemesters.student
        val semesters = studentAndSemesters.semesters
        val diary = semesters.maxByOrNull { it.semesterId }

        with(holder.binding) {
            loginItemName.text = "${student.studentName} ${diary?.diaryName.orEmpty()}"
            loginItemSchool.text = student.schoolName
            loginItemName.isEnabled = !alreadySaved
            loginItemSchool.isEnabled = !alreadySaved
            loginItemSignedIn.visibility = if (alreadySaved) View.VISIBLE else View.GONE

            with(loginItemCheck) {
                isEnabled = !alreadySaved
                keyListener = null
                isChecked = checkedList[position] ?: false
            }

            root.setOnClickListener {
                onClickListener(studentAndSemesters, alreadySaved)

                with(loginItemCheck) {
                    if (isEnabled) {
                        isChecked = !isChecked
                        checkedList[position] = isChecked
                    }
                }
            }
        }
    }

    class ItemViewHolder(val binding: ItemLoginStudentSelectBinding) :
        RecyclerView.ViewHolder(binding.root)
}
