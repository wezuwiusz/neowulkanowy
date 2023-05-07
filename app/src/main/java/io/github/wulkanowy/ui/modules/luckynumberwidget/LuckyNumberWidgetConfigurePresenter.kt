package io.github.wulkanowy.ui.modules.luckynumberwidget

import io.github.wulkanowy.data.Resource
import io.github.wulkanowy.data.db.SharedPrefProvider
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.data.resourceFlow
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.ui.modules.luckynumberwidget.LuckyNumberWidgetProvider.Companion.getStudentWidgetKey
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class LuckyNumberWidgetConfigurePresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val sharedPref: SharedPrefProvider
) : BasePresenter<LuckyNumberWidgetConfigureView>(errorHandler, studentRepository) {

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
        registerStudent(selectedStudent)
    }

    private fun loadData() {
        resourceFlow { studentRepository.getSavedStudents(false) }.onEach {
            when (it) {
                is Resource.Loading -> Timber.d("Lucky number widget configure students data load")
                is Resource.Success -> {
                    val selectedStudentId = appWidgetId?.let { id ->
                        sharedPref.getLong(getStudentWidgetKey(id), 0)
                    } ?: -1
                    when {
                        it.data.isEmpty() -> view?.openLoginView()
                        it.data.size == 1 -> onItemSelect(it.data.single().student)
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
                updateLuckyNumberWidget(id)
                setSuccessResult(id)
            }
        }
        view?.finishView()
    }
}
