package io.github.wulkanowy.ui.modules.homework.add

import io.github.wulkanowy.data.db.entities.Homework
import io.github.wulkanowy.data.logResourceStatus
import io.github.wulkanowy.data.onResourceError
import io.github.wulkanowy.data.onResourceSuccess
import io.github.wulkanowy.data.repositories.HomeworkRepository
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.data.resourceFlow
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.toLocalDate
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

class HomeworkAddPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val homeworkRepository: HomeworkRepository,
    private val semesterRepository: SemesterRepository
) : BasePresenter<HomeworkAddView>(errorHandler, studentRepository) {

    override fun onAttachView(view: HomeworkAddView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Homework details view was initialized")
    }

    fun showDatePicker(date: LocalDate?) {
        view?.showDatePickerDialog(date ?: LocalDate.now())
    }

    fun onAddHomeworkClicked(subject: String?, teacher: String?, date: String?, content: String?) {
        var isError = false

        if (subject.isNullOrBlank()) {
            view?.setErrorSubjectRequired()
            isError = true
        }

        if (date.isNullOrBlank()) {
            view?.setErrorDateRequired()
            isError = true
        }

        if (content.isNullOrBlank()) {
            view?.setErrorContentRequired()
            isError = true
        }

        if (!isError) {
            saveHomework(subject!!, teacher.orEmpty(), date!!.toLocalDate(), content!!)
        }
    }

    private fun saveHomework(subject: String, teacher: String, date: LocalDate, content: String) {
        resourceFlow {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)
            val entryDate = LocalDate.now()
            homeworkRepository.saveHomework(
                Homework(
                    semesterId = semester.semesterId,
                    studentId = student.studentId,
                    date = date,
                    entryDate = entryDate,
                    subject = subject,
                    content = content,
                    teacher = teacher,
                    teacherSymbol = "",
                    attachments = emptyList(),
                ).apply { isAddedByUser = true }
            )
        }
            .logResourceStatus("homework insert")
            .onResourceSuccess {
                view?.run {
                    showSuccessMessage()
                    closeDialog()
                }
            }
            .onResourceError(errorHandler::dispatch)
            .launch("add_homework")
    }
}
