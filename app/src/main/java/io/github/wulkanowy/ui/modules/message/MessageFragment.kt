package io.github.wulkanowy.ui.modules.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import io.github.wulkanowy.R
import io.github.wulkanowy.data.repositories.MessagesRepository.MessageFolder.RECEIVED
import io.github.wulkanowy.data.repositories.MessagesRepository.MessageFolder.SENT
import io.github.wulkanowy.data.repositories.MessagesRepository.MessageFolder.TRASHED
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.base.BaseFragmentPagerAdapter
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.modules.message.tab.MessageTabFragment
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

    override val titleStringId: Int
        get() = R.string.message_title

    override val currentPageIndex: Int
        get() = messageViewPager.currentItem

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_message, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun initView() {
        pagerAdapter.apply {
            containerId = messageViewPager.id
            addFragmentsWithTitle(mapOf(
                MessageTabFragment.newInstance(RECEIVED) to getString(R.string.message_inbox),
                MessageTabFragment.newInstance(SENT) to getString(R.string.message_sent),
                MessageTabFragment.newInstance(TRASHED) to getString(R.string.message_trash)
            ))
        }

        messageViewPager.run {
            adapter = pagerAdapter
            offscreenPageLimit = 2
            setOnSelectPageListener { presenter.onPageSelected(it) }
        }
        messageTabLayout.setupWithViewPager(messageViewPager)
    }

    override fun showContent(show: Boolean) {
        messageViewPager.visibility = if (show) VISIBLE else INVISIBLE
        messageTabLayout.visibility = if (show) VISIBLE else INVISIBLE
    }

    override fun showProgress(show: Boolean) {
        messageProgress.visibility = if (show) VISIBLE else INVISIBLE
    }

    fun onChildFragmentLoaded() {
        presenter.onChildViewLoaded()
    }

    override fun notifyChildLoadData(index: Int, forceRefresh: Boolean) {
        (pagerAdapter.getFragmentInstance(index) as? MessageView.MessageChildView)?.onParentLoadData(forceRefresh)
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
