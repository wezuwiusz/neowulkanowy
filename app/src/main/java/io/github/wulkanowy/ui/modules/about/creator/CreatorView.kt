package io.github.wulkanowy.ui.modules.about.creator

import io.github.wulkanowy.ui.base.BaseView

interface CreatorView : BaseView {

    fun initView()

    fun updateData(data: List<CreatorItem>)

    fun openUserGithubPage(username: String)

    fun openGithubContributorsPage()

    fun showProgress(show: Boolean)
}
