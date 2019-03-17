package io.github.wulkanowy.ui.modules.message.send

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.Recipient
import io.github.wulkanowy.data.db.entities.ReportingUnit
import io.github.wulkanowy.ui.base.BaseActivity
import io.github.wulkanowy.utils.hideSoftInput
import io.github.wulkanowy.utils.showSoftInput
import kotlinx.android.synthetic.main.activity_send_message.*
import javax.inject.Inject

class SendMessageActivity : BaseActivity(), SendMessageView {

    @Inject
    lateinit var presenter: SendMessagePresenter

    companion object {
        private const val EXTRA_MESSAGE = "EXTRA_MESSAGE"

        fun getStartIntent(context: Context) = Intent(context, SendMessageActivity::class.java)

        fun getStartIntent(context: Context, message: Message?): Intent {
            return getStartIntent(context).putExtra(EXTRA_MESSAGE, message)
        }
    }

    override val formRecipientsData: List<Recipient>
        get() = (sendMessageRecipientsInput.selectedChipList).map { (it as RecipientChip).recipient }

    override val formSubjectValue: String
        get() = sendMessageSubjectInput.text.toString()

    override val formContentValue: String
        get() = sendMessageContentInput.text.toString()

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

        presenter.onAttachView(this, intent.getSerializableExtra(EXTRA_MESSAGE) as? Message)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_menu_send_message, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == R.id.sendMessageMenuSend) presenter.onSend()
        else false
    }

    override fun setReportingUnit(unit: ReportingUnit) {
        sendMessageFromTextView.setText(unit.senderName)
    }

    override fun setRecipients(recipients: List<Recipient>) {
        sendMessageRecipientsInput.filterableList = recipients.map { RecipientChip(it) }
    }

    override fun setSelectedRecipients(recipients: List<Recipient>) {
        recipients.map { sendMessageRecipientsInput.addChip(RecipientChip(it)) }
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
        sendMessageSubjectInput.setText(subject)
    }

    override fun setContent(content: String) {
        sendMessageContentInput.setText(content)
    }

    override fun showMessage(text: String) {
        Toast.makeText(this, text, LENGTH_LONG).show()
    }

    override fun showSoftInput(show: Boolean) {
        if (show) showSoftInput() else hideSoftInput()
    }

    override fun popView() {
        onBackPressed()
    }

    override fun onDestroy() {
        presenter.onDetachView()
        super.onDestroy()
    }
}
