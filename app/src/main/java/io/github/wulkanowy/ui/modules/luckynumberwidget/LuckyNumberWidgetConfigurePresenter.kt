package io.github.wulkanowy.ui.modules.luckynumberwidget

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.db.SharedPrefHelper
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.ui.modules.luckynumberwidget.LuckyNumberWidgetProvider.Companion.getStudentWidgetKey
import io.github.wulkanowy.utils.SchedulersProvider
import javax.inject.Inject

class LuckyNumberWidgetConfigurePresenter @Inject constructor(
    private val errorHandler: ErrorHandler,
    private val schedulers: SchedulersProvider,
    private val studentRepository: StudentRepository,
    private val sharedPref: SharedPrefHelper
) : BasePresenter<LuckyNumberWidgetConfigureView>(errorHandler) {

    private var appWidgetId: Int? = null

    fun onAttachView(view: LuckyNumberWidgetConfigureView, appWidgetId: Int?) {
        super.onAttachView(view)
        this.appWidgetId = appWidgetId
        view.initView()
        loadData()
    }

    fun onItemSelect(item: AbstractFlexibleItem<*>) {
        if (item is LuckyNumberWidgetConfigureItem) {
            registerStudent(item.student)
        }
    }

    private fun loadData() {
        disposable.add(studentRepository.getSavedStudents(false)
            .map { it to appWidgetId?.let { id -> sharedPref.getLong(getStudentWidgetKey(id), 0) } }
            .map { (students, currentStudentId) ->
                students.map { student -> LuckyNumberWidgetConfigureItem(student, student.id == currentStudentId) }
            }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .subscribe({
                when {
                    it.isEmpty() -> view?.openLoginView()
                    it.size == 1 -> registerStudent(it.single().student)
                    else -> view?.updateData(it)
                }
            }, { errorHandler.dispatch(it) }))
    }

    private fun registerStudent(student: Student) {
        appWidgetId?.also {
            sharedPref.putLong(getStudentWidgetKey(it), student.id)
            view?.apply {
                updateLuckyNumberWidget(it)
                setSuccessResult(it)
            }
        }
        view?.finishView()
    }
}
