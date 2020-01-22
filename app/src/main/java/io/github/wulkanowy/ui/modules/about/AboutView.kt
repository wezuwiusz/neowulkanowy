package io.github.wulkanowy.ui.modules.about

import android.graphics.drawable.Drawable
import io.github.wulkanowy.ui.base.BaseView

interface AboutView : BaseView {

    val versionRes: Triple<String, String, Drawable?>?

    val creatorsRes: Triple<String, String, Drawable?>?

    val feedbackRes: Triple<String, String, Drawable?>?

    val faqRes: Triple<String, String, Drawable?>?

    val discordRes: Triple<String, String, Drawable?>?

    val homepageRes: Triple<String, String, Drawable?>?

    val licensesRes: Triple<String, String, Drawable?>?

    val privacyRes: Triple<String, String, Drawable?>?

    fun initView()

    fun updateData(header: AboutScrollableHeader, items: List<AboutItem>)

    fun openDiscordInvite()

    fun openEmailClient()

    fun openFaqPage()

    fun openHomepage()

    fun openLicenses()

    fun openCreators()

    fun openPrivacyPolicy()
}
