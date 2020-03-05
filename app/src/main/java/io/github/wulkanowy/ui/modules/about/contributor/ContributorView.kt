package io.github.wulkanowy.ui.modules.about.contributor

import io.github.wulkanowy.ui.base.BaseView

interface ContributorView : BaseView {

    fun initView()

    fun updateData(data: List<ContributorItem>)

    fun openUserGithubPage(username: String)

    fun openGithubContributorsPage()

    fun showProgress(show: Boolean)
}
