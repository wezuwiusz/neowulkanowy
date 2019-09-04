package io.github.wulkanowy.ui.modules.timetablewidget

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.db.SharedPrefProvider
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.ui.modules.timetablewidget.TimetableWidgetProvider.Companion.getStudentWidgetKey
import io.github.wulkanowy.utils.SchedulersProvider
import javax.inject.Inject

class TimetableWidgetConfigurePresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val sharedPref: SharedPrefProvider
) : BasePresenter<TimetableWidgetConfigureView>(errorHandler, studentRepository, schedulers) {

    private var appWidgetId: Int? = null

    private var isFromProvider = false

    fun onAttachView(view: TimetableWidgetConfigureView, appWidgetId: Int?, isFromProvider: Boolean?) {
        super.onAttachView(view)
        this.appWidgetId = appWidgetId
        this.isFromProvider = isFromProvider ?: false
        view.initView()
        loadData()
    }

    fun onItemSelect(item: AbstractFlexibleItem<*>) {
        if (item is TimetableWidgetConfigureItem) {
            registerStudent(item.student)
        }
    }

    private fun loadData() {
        disposable.add(studentRepository.getSavedStudents(false)
            .map { it to appWidgetId?.let { id -> sharedPref.getLong(getStudentWidgetKey(id), 0) } }
            .map { (students, currentStudentId) ->
                students.map { student -> TimetableWidgetConfigureItem(student, student.id == currentStudentId) }
            }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .subscribe({
                when {
                    it.isEmpty() -> view?.openLoginView()
                    it.size == 1 && !isFromProvider -> registerStudent(it.single().student)
                    else -> view?.updateData(it)
                }
            }, { errorHandler.dispatch(it) }))
    }

    private fun registerStudent(student: Student) {
        appWidgetId?.also {
            sharedPref.putLong(getStudentWidgetKey(it), student.id)
            view?.apply {
                updateTimetableWidget(it)
                setSuccessResult(it)
            }
        }
        view?.finishView()
    }
}
