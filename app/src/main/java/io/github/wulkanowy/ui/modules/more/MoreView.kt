package io.github.wulkanowy.ui.modules.more

import android.graphics.drawable.Drawable
import io.github.wulkanowy.ui.base.BaseView

interface MoreView : BaseView {

    val noteRes: Pair<String, Drawable?>?

    val settingsRes: Pair<String, Drawable?>?

    val aboutRes: Pair<String, Drawable?>?

    fun initView()

    fun updateData(data: List<MoreItem>)

    fun openSettingsView()

    fun openAboutView()

    fun popView()
    fun openNoteView()
}
