package io.github.wulkanowy.ui.base.session

import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainActivity

open class BaseSessionFragment : BaseFragment(), BaseSessionView {

    override fun showExpiredDialog() {
        (activity as? MainActivity)?.showExpiredDialog()
    }
}
