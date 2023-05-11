package io.github.wulkanowy.ui.modules.auth

import io.github.wulkanowy.ui.base.BaseView

interface AuthView : BaseView {

    fun enableAuthButton(isEnabled: Boolean)

    fun showProgress(show: Boolean)

    fun showPeselError(show: Boolean)

    fun showInvalidPeselError(show: Boolean)

    fun showSuccess(show: Boolean)

    fun showContent(show: Boolean)

    fun showDescriptionWithName(name: String)
}
