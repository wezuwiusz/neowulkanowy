package io.github.wulkanowy.ui.modules.about.contributor

import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.pojos.Contributor
import io.github.wulkanowy.data.repositories.AppCreatorRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.flowWithResource
import kotlinx.coroutines.flow.onEach
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
        flowWithResource { appCreatorRepository.getAppCreators() }.onEach {
            when (it.status) {
                Status.LOADING -> view?.showProgress(true)
                Status.SUCCESS -> view?.run {
                    showProgress(false)
                    updateData(it.data!!)
                }
                Status.ERROR -> errorHandler.dispatch(it.error!!)
            }
        }.launch()
    }
}
