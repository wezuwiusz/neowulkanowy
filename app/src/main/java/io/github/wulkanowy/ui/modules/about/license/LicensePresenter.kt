package io.github.wulkanowy.ui.modules.about.license

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.SchedulersProvider
import io.reactivex.Single
import javax.inject.Inject

class LicensePresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository
) : BasePresenter<LicenseView>(errorHandler, studentRepository, schedulers) {

    override fun onAttachView(view: LicenseView) {
        super.onAttachView(view)
        view.initView()
        loadData()
    }

    fun onItemSelected(item: AbstractFlexibleItem<*>) {
        if (item !is LicenseItem) return
        view?.run { item.library.license?.licenseDescription?.let { openLicense(it) } }
    }

    private fun loadData() {
        disposable.add(Single.fromCallable { view?.appLibraries }
            .map {
                val exclude = listOf("Android-Iconics", "CircleImageView", "FastAdapter", "Jsoup", "okio", "Retrofit")
                it.filter { library -> !exclude.contains(library.libraryName) }
            }
            .map { it.map { library -> LicenseItem(library) } }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .doOnEvent { _, _ -> view?.showProgress(false) }
            .subscribe({ view?.run { updateData(it) } }, { errorHandler.dispatch(it) }))
    }
}
