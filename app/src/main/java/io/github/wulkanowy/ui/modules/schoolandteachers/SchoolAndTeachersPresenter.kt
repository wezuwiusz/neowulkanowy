package io.github.wulkanowy.ui.modules.schoolandteachers

import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.SchedulersProvider
import io.reactivex.Completable
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SchoolAndTeachersPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository
) : BasePresenter<SchoolAndTeachersView>(errorHandler, studentRepository, schedulers) {

    override fun onAttachView(view: SchoolAndTeachersView) {
        super.onAttachView(view)
        disposable.add(Completable.timer(150, TimeUnit.MILLISECONDS, schedulers.mainThread)
            .subscribe {
                view.initView()
                Timber.i("Message view was initialized")
                loadData()
            })
    }

    fun onPageSelected(index: Int) {
        loadChild(index)
    }

    private fun loadData() {
        view?.run { loadChild(currentPageIndex) }
    }

    private fun loadChild(index: Int, forceRefresh: Boolean = false) {
        Timber.i("Load schoolandteachers child view index: $index")
        view?.notifyChildLoadData(index, forceRefresh)
    }

    fun onChildViewLoaded() {
        view?.apply {
            showContent(true)
            showProgress(false)
        }
    }
}
