package io.github.wulkanowy.ui.modules.luckynumberwidget

import io.github.wulkanowy.data.db.SharedPrefProvider
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.ui.modules.luckynumberwidget.LuckyNumberWidgetProvider.Companion.getStudentWidgetKey
import io.github.wulkanowy.ui.modules.luckynumberwidget.LuckyNumberWidgetProvider.Companion.getThemeWidgetKey
import io.github.wulkanowy.utils.SchedulersProvider
import javax.inject.Inject

class LuckyNumberWidgetConfigurePresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val sharedPref: SharedPrefProvider
) : BasePresenter<LuckyNumberWidgetConfigureView>(errorHandler, studentRepository, schedulers) {

    private var appWidgetId: Int? = null

    private var selectedStudent: Student? = null

    fun onAttachView(view: LuckyNumberWidgetConfigureView, appWidgetId: Int?) {
        super.onAttachView(view)
        this.appWidgetId = appWidgetId
        view.initView()
        loadData()
    }

    fun onItemSelect(student: Student) {
        selectedStudent = student
        view?.showThemeDialog()
    }

    fun onThemeSelect(index: Int) {
        appWidgetId?.let {
            sharedPref.putLong(getThemeWidgetKey(it), index.toLong())
        }
        registerStudent(selectedStudent)
    }

    fun onDismissThemeView(){
        view?.finishView()
    }

    private fun loadData() {
        disposable.add(studentRepository.getSavedStudents(false)
            .map { it to appWidgetId?.let { id -> sharedPref.getLong(getStudentWidgetKey(id), 0) } }
            .map { (students, currentStudentId) ->
                students.map { student -> student to (student.id == currentStudentId) }
            }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .subscribe({
                when {
                    it.isEmpty() -> view?.openLoginView()
                    it.size == 1 -> {
                        selectedStudent = it.single().first
                        view?.showThemeDialog()
                    }
                    else -> view?.updateData(it)
                }
            }, { errorHandler.dispatch(it) }))
    }

    private fun registerStudent(student: Student?) {
        requireNotNull(student)

        appWidgetId?.let { id ->
            sharedPref.putLong(getStudentWidgetKey(id), student.id)
            view?.run {
                updateLuckyNumberWidget(id)
                setSuccessResult(id)
            }
        }
        view?.finishView()
    }
}
