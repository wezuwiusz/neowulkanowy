package io.github.wulkanowy.ui.modules.message.send

import android.annotation.SuppressLint
import android.app.AlertDialog
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
import androidx.core.widget.doOnTextChanged
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.ReportingUnit
import io.github.wulkanowy.databinding.ActivitySendMessageBinding
import io.github.wulkanowy.ui.base.BaseActivity
import io.github.wulkanowy.utils.dpToPx
import io.github.wulkanowy.utils.hideSoftInput
import io.github.wulkanowy.utils.showSoftInput
import kotlinx.coroutines.FlowPreview
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SendMessageActivity : BaseActivity<SendMessagePresenter, ActivitySendMessageBinding>(),
    SendMessageView {

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
        get() = binding.sendMessageTo.isDropdownListVisible

    @Suppress("UNCHECKED_CAST")
    override lateinit var formRecipientsData: List<RecipientChipItem>

    override lateinit var formSubjectValue: String

    override lateinit var formContentValue: String

    override val messageRequiredRecipients: String
        get() = getString(R.string.message_required_recipients)

    override val messageContentMinLength: String
        get() = getString(R.string.message_content_min_length)

    override val messageSuccess: String
        get() = getString(R.string.message_send_successful)

    @FlowPreview
    @Suppress("UNCHECKED_CAST")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivitySendMessageBinding.inflate(layoutInflater).apply { binding = this }.root)
        setSupportActionBar(binding.sendMessageToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        messageContainer = binding.sendMessageContainer

        formRecipientsData = binding.sendMessageTo.addedChipItems as List<RecipientChipItem>
        formSubjectValue = binding.sendMessageSubject.text.toString()
        formContentValue = binding.sendMessageMessageContent.text.toString()

        presenter.onAttachView(
            view = this,
            message = intent.getSerializableExtra(EXTRA_MESSAGE) as? Message,
            reply = intent.getSerializableExtra(EXTRA_REPLY) as? Boolean
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initView() {
        setUpExtendedHitArea()
        with(binding) {
            sendMessageScroll.setOnTouchListener { _, _ -> presenter.onTouchScroll() }
            sendMessageTo.onChipAddListener = { onRecipientChange() }
            sendMessageTo.onTextChangeListener = presenter::onRecipientsTextChange
            sendMessageSubject.doOnTextChanged { text, _, _, _ -> onMessageSubjectChange(text) }
            sendMessageMessageContent.doOnTextChanged { text, _, _, _ -> onMessageContentChange(text) }
        }
    }

    private fun onMessageSubjectChange(text: CharSequence?) {
        formSubjectValue = text.toString()
        presenter.onMessageContentChange()
    }

    private fun onMessageContentChange(text: CharSequence?) {
        formContentValue = text.toString()
        presenter.onMessageContentChange()
    }

    private fun onRecipientChange() {
        presenter.onMessageContentChange()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_menu_send_message, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.sendMessageMenuSend) presenter.onSend()
        else false
    }

    override fun onSupportNavigateUp(): Boolean {
        return presenter.onUpNavigate()
    }

    override fun setReportingUnit(unit: ReportingUnit) {
        binding.sendMessageFrom.text = unit.senderName
    }

    override fun setRecipients(recipients: List<RecipientChipItem>) {
        binding.sendMessageTo.filterableChipItems = recipients
    }

    override fun setSelectedRecipients(recipients: List<RecipientChipItem>) {
        binding.sendMessageTo.addChips(recipients)
    }

    override fun showProgress(show: Boolean) {
        binding.sendMessageProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun showContent(show: Boolean) {
        binding.sendMessageContent.visibility = if (show) VISIBLE else GONE
    }

    override fun showEmpty(show: Boolean) {
        binding.sendMessageEmpty.visibility = if (show) VISIBLE else GONE
    }

    override fun showActionBar(show: Boolean) {
        supportActionBar?.apply { if (show) show() else hide() }
    }

    override fun setSubject(subject: String) {
        binding.sendMessageSubject.setText(subject)
    }

    override fun setContent(content: String) {
        binding.sendMessageMessageContent.setText(content)
    }

    override fun showMessage(text: String) {
        Toast.makeText(this, text, LENGTH_LONG).show()
    }

    override fun showSoftInput(show: Boolean) {
        if (show) showSoftInput() else hideSoftInput()
    }

    override fun hideDropdownList() {
        binding.sendMessageTo.hideDropdownList()
    }

    override fun scrollToRecipients() {
        with(binding.sendMessageScroll) {
            post {
                scrollTo(0, binding.sendMessageTo.bottom - dpToPx(53f).toInt())
            }
        }
    }

    override fun popView() {
        finish()
    }

    private fun setUpExtendedHitArea() {
        fun extendHitArea() {
            val containerHitRect = Rect().apply {
                binding.sendMessageContent.getHitRect(this)
            }

            val contentHitRect = Rect().apply {
                binding.sendMessageMessageContent.getHitRect(this)
            }

            contentHitRect.top = contentHitRect.bottom
            contentHitRect.bottom = containerHitRect.bottom

            binding.sendMessageContent.touchDelegate =
                TouchDelegate(contentHitRect, binding.sendMessageMessageContent)
        }

        with(binding.sendMessageMessageContent) {
            post {
                addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ -> extendHitArea() }
                extendHitArea()
            }
        }
    }

    override fun showMessageBackupDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.message_title)
            .setMessage(presenter.getMessageBackupContent(presenter.getRecipientsNames()))
            .setPositiveButton(R.string.all_yes) { _, _ -> presenter.restoreMessageParts() }
            .setNegativeButton(R.string.all_no) { _, _ -> presenter.clearDraft() }
            .show()
    }

    override fun clearDraft() {
        formRecipientsData = binding.sendMessageTo.addedChipItems as List<RecipientChipItem>
        presenter.clearDraft()
    }

    override fun getMessageBackupDialogString() =
        resources.getString(R.string.message_restore_dialog)

    override fun getMessageBackupDialogStringWithRecipients(recipients: String) =
        resources.getString(R.string.message_restore_dialog_with_recipients, recipients)
}
