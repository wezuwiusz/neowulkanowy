package io.github.wulkanowy.ui.modules.homework.add

import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.db.entities.Homework
import io.github.wulkanowy.data.repositories.HomeworkRepository
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.flowWithResource
import io.github.wulkanowy.utils.toLocalDate
import kotlinx.coroutines.flow.onEach
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
        flowWithResource {
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
        }.onEach {
            when (it.status) {
                Status.LOADING -> Timber.i("Homework insert start")
                Status.SUCCESS -> {
                    Timber.i("Homework insert: Success")
                    view?.run {
                        showSuccessMessage()
                        closeDialog()
                    }
                }
                Status.ERROR -> {
                    Timber.i("Homework insert result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                }
            }
        }.launch("add_homework")
    }
}
