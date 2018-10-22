package io.github.wulkanowy.ui.main.more

import android.graphics.drawable.Drawable
import io.github.wulkanowy.ui.base.BaseView

interface MoreView : BaseView {

    val settingsRes: Pair<String, Drawable?>?

    val aboutRes: Pair<String, Drawable?>?

    fun initView()

    fun updateData(data: List<MoreItem>)

    fun openSettingsView()

    fun openAboutView()

    fun popView()
}
