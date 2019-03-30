package io.github.wulkanowy.ui.base.session

import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.login.LoginActivity
import io.github.wulkanowy.ui.modules.main.MainActivity

open class BaseSessionFragment : BaseFragment(), BaseSessionView {

    override fun showExpiredDialog() {
        (activity as? MainActivity)?.showExpiredDialog()
    }

    override fun openLoginView() {
        activity?.also {
            startActivity(LoginActivity.getStartIntent(it)
                .apply { addFlags(FLAG_ACTIVITY_CLEAR_TASK or FLAG_ACTIVITY_NEW_TASK) })
        }
    }
}
