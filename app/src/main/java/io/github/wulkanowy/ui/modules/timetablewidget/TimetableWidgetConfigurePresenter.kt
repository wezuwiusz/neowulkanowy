package io.github.wulkanowy.ui.modules.timetablewidget

import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.db.SharedPrefProvider
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.ui.modules.timetablewidget.TimetableWidgetProvider.Companion.getStudentWidgetKey
import io.github.wulkanowy.ui.modules.timetablewidget.TimetableWidgetProvider.Companion.getThemeWidgetKey
import io.github.wulkanowy.utils.flowWithResource
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

    fun onAttachView(view: TimetableWidgetConfigureView, appWidgetId: Int?, isFromProvider: Boolean?) {
        super.onAttachView(view)
        this.appWidgetId = appWidgetId
        this.isFromProvider = isFromProvider ?: false
        view.initView()
        loadData()
    }

    fun onItemSelect(student: Student) {
        selectedStudent = student

        if (isFromProvider) registerStudent(selectedStudent)
        else view?.showThemeDialog()
    }

    fun onThemeSelect(index: Int) {
        appWidgetId?.let {
            sharedPref.putLong(getThemeWidgetKey(it), index.toLong())
        }
        registerStudent(selectedStudent)
    }

    fun onDismissThemeView() {
        view?.finishView()
    }

    private fun loadData() {
        flowWithResource { studentRepository.getSavedStudents(false) }.onEach {
            when (it.status) {
                Status.LOADING -> Timber.d("Timetable widget configure students data load")
                Status.SUCCESS -> {
                    val widgetId = appWidgetId?.let { id -> sharedPref.getLong(getStudentWidgetKey(id), 0) }
                    when {
                        it.data!!.isEmpty() -> view?.openLoginView()
                        it.data.size == 1 && !isFromProvider -> {
                            selectedStudent = it.data.single().student
                            view?.showThemeDialog()
                        }
                        else -> view?.updateData(it.data.map { entity ->
                            entity.student to (entity.student.id == widgetId)
                        })
                    }
                }
                Status.ERROR -> errorHandler.dispatch(it.error!!)
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
