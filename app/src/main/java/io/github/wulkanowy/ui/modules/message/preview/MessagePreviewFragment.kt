package io.github.wulkanowy.ui.modules.message.preview

import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.MessageWithAttachment
import io.github.wulkanowy.databinding.FragmentMessagePreviewBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.modules.message.send.SendMessageActivity
import io.github.wulkanowy.utils.shareText
import javax.inject.Inject

@AndroidEntryPoint
class MessagePreviewFragment :
    BaseFragment<FragmentMessagePreviewBinding>(R.layout.fragment_message_preview),
    MessagePreviewView, MainView.TitledView {

    @Inject
    lateinit var presenter: MessagePreviewPresenter

    @Inject
    lateinit var previewAdapter: MessagePreviewAdapter

    private var menuReplyButton: MenuItem? = null

    private var menuForwardButton: MenuItem? = null

    private var menuDeleteButton: MenuItem? = null

    private var menuShareButton: MenuItem? = null

    private var menuPrintButton: MenuItem? = null

    override val titleStringId: Int
        get() = R.string.message_title

    override val deleteMessageSuccessString: String
        get() = getString(R.string.message_delete_success)

    override val messageNoSubjectString: String
        get() = getString(R.string.message_no_subject)

    override val printHTML: String
        get() = requireContext().assets.open("message-print-page.html").bufferedReader().use { it.readText() }

    override val messageNotExists: String
        get() = getString(R.string.message_not_exists)

    companion object {
        const val MESSAGE_ID_KEY = "message_id"

        fun newInstance(message: Message): MessagePreviewFragment {
            return MessagePreviewFragment().apply {
                arguments = Bundle().apply { putSerializable(MESSAGE_ID_KEY, message) }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMessagePreviewBinding.bind(view)
        messageContainer = binding.messagePreviewContainer
        presenter.onAttachView(this, (savedInstanceState ?: arguments)?.getSerializable(MESSAGE_ID_KEY) as? Message)
    }

    override fun initView() {
        binding.messagePreviewErrorDetails.setOnClickListener { presenter.onDetailsClick() }

        with(binding.messagePreviewRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = previewAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.action_menu_message_preview, menu)
        menuReplyButton = menu.findItem(R.id.messagePreviewMenuReply)
        menuForwardButton = menu.findItem(R.id.messagePreviewMenuForward)
        menuDeleteButton = menu.findItem(R.id.messagePreviewMenuDelete)
        menuShareButton = menu.findItem(R.id.messagePreviewMenuShare)
        menuPrintButton = menu.findItem(R.id.messagePreviewMenuPrint)
        presenter.onCreateOptionsMenu()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.messagePreviewMenuReply -> presenter.onReply()
            R.id.messagePreviewMenuForward -> presenter.onForward()
            R.id.messagePreviewMenuDelete -> presenter.onMessageDelete()
            R.id.messagePreviewMenuShare -> presenter.onShare()
            R.id.messagePreviewMenuPrint -> presenter.onPrint()
            else -> false
        }
    }

    override fun setMessageWithAttachment(item: MessageWithAttachment) {
        with(previewAdapter) {
            messageWithAttachment = item
            notifyDataSetChanged()
        }
    }

    override fun showProgress(show: Boolean) {
        binding.messagePreviewProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun showContent(show: Boolean) {
        binding.messagePreviewRecycler.visibility = if (show) VISIBLE else GONE
    }

    override fun showOptions(show: Boolean) {
        menuReplyButton?.isVisible = show
        menuForwardButton?.isVisible = show
        menuDeleteButton?.isVisible = show
        menuShareButton?.isVisible = show
        menuPrintButton?.isVisible = show
    }

    override fun setDeletedOptionsLabels() {
        menuDeleteButton?.setTitle(R.string.message_delete_forever)
    }

    override fun setNotDeletedOptionsLabels() {
        menuDeleteButton?.setTitle(R.string.message_move_to_bin)
    }

    override fun showErrorView(show: Boolean) {
        binding.messagePreviewError.visibility = if (show) VISIBLE else GONE
    }

    override fun setErrorDetails(message: String) {
        binding.messagePreviewErrorMessage.text = message
    }

    override fun setErrorRetryCallback(callback: () -> Unit) {
        binding.messagePreviewErrorRetry.setOnClickListener { callback() }
    }

    override fun openMessageReply(message: Message?) {
        context?.let { it.startActivity(SendMessageActivity.getStartIntent(it, message, true)) }
    }

    override fun openMessageForward(message: Message?) {
        context?.let { it.startActivity(SendMessageActivity.getStartIntent(it, message)) }
    }

    override fun shareText(text: String, subject: String) {
        context?.shareText(text, subject)
    }

    override fun printDocument(html: String, jobName: String) {
        val webView = WebView(requireContext())
        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest) = false

            override fun onPageFinished(view: WebView, url: String) {
                createWebPrintJob(view, jobName)
            }
        }

        webView.loadDataWithBaseURL("file:///android_asset/", html, "text/HTML", "UTF-8", null)
    }

    private fun createWebPrintJob(webView: WebView, jobName: String) {
        activity?.getSystemService<PrintManager>()?.let { printManager ->
            val printAdapter = webView.createPrintDocumentAdapter(jobName)

            printManager.print(
                jobName,
                printAdapter,
                PrintAttributes.Builder().build()
            )
        }
    }

    override fun popView() {
        (activity as MainActivity).popView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(MESSAGE_ID_KEY, presenter.message)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
