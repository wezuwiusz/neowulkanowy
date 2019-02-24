package io.github.wulkanowy.ui.modules.message.send

import io.github.wulkanowy.data.db.entities.Recipient
import io.github.wulkanowy.data.db.entities.ReportingUnit
import io.github.wulkanowy.ui.base.session.BaseSessionView

interface SendMessageView : BaseSessionView {

    val formRecipientsData: List<Recipient>

    val formSubjectValue: String

    val formContentValue: String

    val messageRequiredRecipients: String

    val messageContentMinLength: String

    val messageSuccess: String

    fun initView()

    fun setReportingUnit(unit: ReportingUnit)

    fun setRecipients(recipients: List<Recipient>)

    fun refreshRecipientsAdapter()

    fun showProgress(show: Boolean)

    fun showContent(show: Boolean)

    fun showEmpty(show: Boolean)

    fun popView()

    fun hideSoftInput()

    fun showBottomNav(show: Boolean)
}
