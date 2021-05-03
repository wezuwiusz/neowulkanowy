package io.github.wulkanowy.ui.modules.more

import android.graphics.drawable.Drawable
import io.github.wulkanowy.ui.base.BaseView

interface MoreView : BaseView {

    val messagesRes: Pair<String, Drawable?>?

    val homeworkRes: Pair<String, Drawable?>?

    val noteRes: Pair<String, Drawable?>?

    val luckyNumberRes: Pair<String, Drawable?>?

    val conferencesRes: Pair<String, Drawable?>?

    val schoolAnnouncementRes: Pair<String, Drawable?>?

    val schoolAndTeachersRes: Pair<String, Drawable?>?

    val mobileDevicesRes: Pair<String, Drawable?>?

    val settingsRes: Pair<String, Drawable?>?

    fun initView()

    fun updateData(data: List<Pair<String, Drawable?>>)

    fun openSettingsView()

    fun popView(depth: Int)

    fun openMessagesView()

    fun openHomeworkView()

    fun openNoteView()

    fun openLuckyNumberView()

    fun openSchoolAnnouncementView()

    fun openConferencesView()

    fun openSchoolAndTeachersView()

    fun openMobileDevicesView()
}
