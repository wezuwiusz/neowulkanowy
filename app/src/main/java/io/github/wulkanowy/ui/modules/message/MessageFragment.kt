package io.github.wulkanowy.ui.modules.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.repositories.message.MessageFolder.RECEIVED
import io.github.wulkanowy.data.repositories.message.MessageFolder.SENT
import io.github.wulkanowy.data.repositories.message.MessageFolder.TRASHED
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.base.BaseFragmentPagerAdapter
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.modules.message.send.SendMessageActivity
import io.github.wulkanowy.ui.modules.message.tab.MessageTabFragment
import io.github.wulkanowy.utils.dpToPx
import io.github.wulkanowy.utils.setOnSelectPageListener
import kotlinx.android.synthetic.main.fragment_message.*
import javax.inject.Inject

class MessageFragment : BaseFragment(), MessageView, MainView.TitledView {

    @Inject
    lateinit var presenter: MessagePresenter

    @Inject
    lateinit var pagerAdapter: BaseFragmentPagerAdapter

    companion object {
        fun newInstance() = MessageFragment()
    }

    override val titleStringId get() = R.string.message_title

    override val currentPageIndex get() = messageViewPager.currentItem

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_message, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun initView() {
        with(pagerAdapter) {
            containerId = messageViewPager.id
            addFragmentsWithTitle(mapOf(
                MessageTabFragment.newInstance(RECEIVED) to getString(R.string.message_inbox),
                MessageTabFragment.newInstance(SENT) to getString(R.string.message_sent),
                MessageTabFragment.newInstance(TRASHED) to getString(R.string.message_trash)
            ))
        }

        with(messageViewPager) {
            adapter = pagerAdapter
            offscreenPageLimit = 2
            setOnSelectPageListener(presenter::onPageSelected)
        }

        with(messageTabLayout) {
            setupWithViewPager(messageViewPager)
            setElevationCompat(context.dpToPx(4f))
        }

        openSendMessageButton.setOnClickListener { presenter.onSendMessageButtonClicked() }
    }

    override fun showContent(show: Boolean) {
        messageViewPager.visibility = if (show) VISIBLE else INVISIBLE
        messageTabLayout.visibility = if (show) VISIBLE else INVISIBLE
    }

    override fun showProgress(show: Boolean) {
        messageProgress.visibility = if (show) VISIBLE else INVISIBLE
    }

    fun onDeleteMessage(message: Message) {
        presenter.onDeleteMessage(message)
    }

    fun onChildFragmentLoaded() {
        presenter.onChildViewLoaded()
    }

    override fun notifyChildMessageDeleted(tabId: Int) {
        (pagerAdapter.getFragmentInstance(tabId) as? MessageTabFragment)?.onParentDeleteMessage()
    }

    override fun notifyChildLoadData(index: Int, forceRefresh: Boolean) {
        (pagerAdapter.getFragmentInstance(index) as? MessageTabFragment)?.onParentLoadData(forceRefresh)
    }

    override fun openSendMessage() {
        context?.let { it.startActivity(SendMessageActivity.getStartIntent(it)) }
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
