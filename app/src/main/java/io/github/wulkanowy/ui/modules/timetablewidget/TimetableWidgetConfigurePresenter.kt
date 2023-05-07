package io.github.wulkanowy.ui.modules.timetablewidget

import io.github.wulkanowy.data.Resource
import io.github.wulkanowy.data.db.SharedPrefProvider
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.data.resourceFlow
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.ui.modules.timetablewidget.TimetableWidgetProvider.Companion.getStudentWidgetKey
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class TimetableWidgetConfigurePresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val sharedPref: SharedPrefProvider
) : BasePresenter<TimetableWidgetConfigureView>(errorHandler, studentRepository) {

    private var appWidgetId: Int? = null

    private var isFromProvider = false

    private var selectedStudent: Student? = null

    fun onAttachView(
        view: TimetableWidgetConfigureView,
        appWidgetId: Int?,
        isFromProvider: Boolean?
    ) {
        super.onAttachView(view)
        this.appWidgetId = appWidgetId
        this.isFromProvider = isFromProvider ?: false
        view.initView()
        loadData()
    }

    fun onItemSelect(student: Student) {
        selectedStudent = student
        registerStudent(selectedStudent)
    }

    private fun loadData() {
        resourceFlow { studentRepository.getSavedStudents(false) }.onEach {
            when (it) {
                is Resource.Loading -> Timber.d("Timetable widget configure students data load")
                is Resource.Success -> {
                    val selectedStudentId = appWidgetId?.let { id ->
                        sharedPref.getLong(getStudentWidgetKey(id), 0)
                    } ?: -1
                    when {
                        it.data.isEmpty() -> view?.openLoginView()
                        it.data.size == 1 && !isFromProvider -> onItemSelect(it.data.single().student)
                        else -> view?.updateData(it.data, selectedStudentId)
                    }
                }
                is Resource.Error -> errorHandler.dispatch(it.error)
            }
        }.launch()
    }

    private fun registerStudent(student: Student?) {
        requireNotNull(student)

        appWidgetId?.let { id ->
            sharedPref.putLong(getStudentWidgetKey(id), student.id)
            view?.run {
                updateTimetableWidget(id)
                setSuccessResult(id)
            }
        }
        view?.finishView()
    }
}
