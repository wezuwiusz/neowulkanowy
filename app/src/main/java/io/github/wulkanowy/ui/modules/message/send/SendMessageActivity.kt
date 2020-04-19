package io.github.wulkanowy.ui.modules.message.send

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.TouchDelegate
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.ReportingUnit
import io.github.wulkanowy.ui.base.BaseActivity
import io.github.wulkanowy.utils.dpToPx
import io.github.wulkanowy.utils.hideSoftInput
import io.github.wulkanowy.utils.showSoftInput
import kotlinx.android.synthetic.main.activity_send_message.*
import javax.inject.Inject

class SendMessageActivity : BaseActivity<SendMessagePresenter>(), SendMessageView {

    @Inject
    override lateinit var presenter: SendMessagePresenter

    companion object {
        private const val EXTRA_MESSAGE = "EXTRA_MESSAGE"

        private const val EXTRA_REPLY = "EXTRA_REPLY"

        fun getStartIntent(context: Context) = Intent(context, SendMessageActivity::class.java)

        fun getStartIntent(context: Context, message: Message?, reply: Boolean = false): Intent {
            return getStartIntent(context)
                .putExtra(EXTRA_MESSAGE, message)
                .putExtra(EXTRA_REPLY, reply)
        }
    }

    override val isDropdownListVisible: Boolean
        get() = sendMessageTo.isDropdownListVisible

    @Suppress("UNCHECKED_CAST")
    override val formRecipientsData: List<RecipientChipItem>
        get() = sendMessageTo.addedChipItems as List<RecipientChipItem>

    override val formSubjectValue: String
        get() = sendMessageSubject.text.toString()

    override val formContentValue: String
        get() = sendMessageMessageContent.text.toString()

    override val messageRequiredRecipients: String
        get() = getString(R.string.message_required_recipients)

    override val messageContentMinLength: String
        get() = getString(R.string.message_content_min_length)

    override val messageSuccess: String
        get() = getString(R.string.message_send_successful)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_message)
        setSupportActionBar(sendMessageToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        messageContainer = sendMessageContainer

        presenter.onAttachView(this, intent.getSerializableExtra(EXTRA_MESSAGE) as? Message, intent.getSerializableExtra(EXTRA_REPLY) as? Boolean)
    }

    override fun initView() {
        setUpExtendedHitArea()
        sendMessageScroll.setOnTouchListener { _, _ -> presenter.onTouchScroll() }
        sendMessageTo.onTextChangeListener = presenter::onRecipientsTextChange
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_menu_send_message, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == R.id.sendMessageMenuSend) presenter.onSend()
        else false
    }

    override fun onSupportNavigateUp(): Boolean {
        return presenter.onUpNavigate()
    }

    override fun setReportingUnit(unit: ReportingUnit) {
        sendMessageFrom.text = unit.senderName
    }

    override fun setRecipients(recipients: List<RecipientChipItem>) {
        sendMessageTo.filterableChipItems = recipients
    }

    override fun setSelectedRecipients(recipients: List<RecipientChipItem>) {
        sendMessageTo.addChips(recipients)
    }

    override fun showProgress(show: Boolean) {
        sendMessageProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun showContent(show: Boolean) {
        sendMessageContent.visibility = if (show) VISIBLE else GONE
    }

    override fun showEmpty(show: Boolean) {
        sendMessageEmpty.visibility = if (show) VISIBLE else GONE
    }

    override fun showActionBar(show: Boolean) {
        supportActionBar?.apply { if (show) show() else hide() }
    }

    override fun setSubject(subject: String) {
        sendMessageSubject.setText(subject)
    }

    override fun setContent(content: String) {
        sendMessageMessageContent.setText(content)
    }

    override fun showMessage(text: String) {
        Toast.makeText(this, text, LENGTH_LONG).show()
    }

    override fun showSoftInput(show: Boolean) {
        if (show) showSoftInput() else hideSoftInput()
    }

    override fun hideDropdownList() {
        sendMessageTo.hideDropdownList()
    }

    override fun scrollToRecipients() {
        sendMessageScroll.post {
            sendMessageScroll.scrollTo(0, sendMessageTo.bottom - dpToPx(53f).toInt())
        }
    }

    override fun popView() {
        finish()
    }

    private fun setUpExtendedHitArea() {
        fun extendHitArea() {
            val containerHitRect = Rect().apply {
                sendMessageContent.getHitRect(this)
            }

            val contentHitRect = Rect().apply {
                sendMessageMessageContent.getHitRect(this)
            }

            contentHitRect.top = contentHitRect.bottom
            contentHitRect.bottom = containerHitRect.bottom

            sendMessageContent.touchDelegate = TouchDelegate(contentHitRect, sendMessageMessageContent)
        }

        sendMessageMessageContent.post {
            sendMessageMessageContent.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                extendHitArea()
            }
            extendHitArea()
        }
    }
}
