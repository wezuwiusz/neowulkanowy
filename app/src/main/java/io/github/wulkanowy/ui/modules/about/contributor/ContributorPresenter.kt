package io.github.wulkanowy.ui.modules.about.contributor

import io.github.wulkanowy.data.pojos.Contributor
import io.github.wulkanowy.data.repositories.appcreator.AppCreatorRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.SchedulersProvider
import kotlinx.coroutines.rx2.rxSingle
import javax.inject.Inject

class ContributorPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val appCreatorRepository: AppCreatorRepository
) : BasePresenter<ContributorView>(errorHandler, studentRepository, schedulers) {

    override fun onAttachView(view: ContributorView) {
        super.onAttachView(view)
        view.initView()
        loadData()
    }

    fun onItemSelected(contributor: Contributor) {
        view?.openUserGithubPage(contributor.githubUsername)
    }

    fun onSeeMoreClick() {
        view?.openGithubContributorsPage()
    }

    private fun loadData() {
        disposable.add(rxSingle { appCreatorRepository.getAppCreators() }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .doFinally { view?.showProgress(false) }
            .subscribe({ view?.run { updateData(it) } }, { errorHandler.dispatch(it) }))
    }
}
