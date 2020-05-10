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
import io.github.wulkanowy.databinding.ActivitySendMessageBinding
import io.github.wulkanowy.ui.base.BaseActivity
import io.github.wulkanowy.utils.dpToPx
import io.github.wulkanowy.utils.hideSoftInput
import io.github.wulkanowy.utils.showSoftInput
import javax.inject.Inject

class SendMessageActivity : BaseActivity<SendMessagePresenter, ActivitySendMessageBinding>(), SendMessageView {

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
    override val formRecipientsData: List<RecipientChipItem>
        get() = binding.sendMessageTo.addedChipItems as List<RecipientChipItem>

    override val formSubjectValue: String
        get() = binding.sendMessageSubject.text.toString()

    override val formContentValue: String
        get() = binding.sendMessageMessageContent.text.toString()

    override val messageRequiredRecipients: String
        get() = getString(R.string.message_required_recipients)

    override val messageContentMinLength: String
        get() = getString(R.string.message_content_min_length)

    override val messageSuccess: String
        get() = getString(R.string.message_send_successful)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivitySendMessageBinding.inflate(layoutInflater).apply { binding = this }.root)
        setSupportActionBar(binding.sendMessageToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        messageContainer = binding.sendMessageContainer

        presenter.onAttachView(this, intent.getSerializableExtra(EXTRA_MESSAGE) as? Message, intent.getSerializableExtra(EXTRA_REPLY) as? Boolean)
    }

    override fun initView() {
        setUpExtendedHitArea()
        with(binding) {
            sendMessageScroll.setOnTouchListener { _, _ -> presenter.onTouchScroll() }
            sendMessageTo.onTextChangeListener = presenter::onRecipientsTextChange
        }
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

            binding.sendMessageContent.touchDelegate = TouchDelegate(contentHitRect, binding.sendMessageMessageContent)
        }

        with(binding.sendMessageMessageContent) {
            post {
                addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ -> extendHitArea() }
                extendHitArea()
            }
        }
    }
}
