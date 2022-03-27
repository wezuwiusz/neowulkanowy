package io.github.wulkanowy.ui.modules.about.contributor

import io.github.wulkanowy.data.*
import io.github.wulkanowy.data.pojos.Contributor
import io.github.wulkanowy.data.repositories.AppCreatorRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import javax.inject.Inject

class ContributorPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val appCreatorRepository: AppCreatorRepository
) : BasePresenter<ContributorView>(errorHandler, studentRepository) {

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
        resourceFlow { appCreatorRepository.getAppCreators() }
            .onResourceLoading { view?.showProgress(true) }
            .onResourceSuccess { view?.updateData(it) }
            .onResourceNotLoading { view?.showProgress(false) }
            .onResourceError { errorHandler.dispatch(it) }
            .launch()
    }
}
