package io.github.wulkanowy.ui.modules.message

import android.os.Bundle
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.enums.MessageFolder.RECEIVED
import io.github.wulkanowy.data.enums.MessageFolder.SENT
import io.github.wulkanowy.data.enums.MessageFolder.TRASHED
import io.github.wulkanowy.databinding.FragmentMessageBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.base.BaseFragmentPagerAdapter
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.modules.message.send.SendMessageActivity
import io.github.wulkanowy.ui.modules.message.tab.MessageTabFragment
import io.github.wulkanowy.utils.dpToPx
import io.github.wulkanowy.utils.setOnSelectPageListener
import javax.inject.Inject

@AndroidEntryPoint
class MessageFragment : BaseFragment<FragmentMessageBinding>(R.layout.fragment_message),
    MessageView, MainView.TitledView {

    @Inject
    lateinit var presenter: MessagePresenter

    private val pagerAdapter by lazy { BaseFragmentPagerAdapter(childFragmentManager) }

    companion object {
        fun newInstance() = MessageFragment()
    }

    override val titleStringId get() = R.string.message_title

    override val currentPageIndex get() = binding.messageViewPager.currentItem

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMessageBinding.bind(view)
        presenter.onAttachView(this)
    }

    override fun initView() {
        with(pagerAdapter) {
            containerId = binding.messageViewPager.id
            addFragmentsWithTitle(mapOf(
                MessageTabFragment.newInstance(RECEIVED) to getString(R.string.message_inbox),
                MessageTabFragment.newInstance(SENT) to getString(R.string.message_sent),
                MessageTabFragment.newInstance(TRASHED) to getString(R.string.message_trash)
            ))
        }

        with(binding.messageViewPager) {
            adapter = pagerAdapter
            offscreenPageLimit = 2
            setOnSelectPageListener(presenter::onPageSelected)
        }

        with(binding.messageTabLayout) {
            setupWithViewPager(binding.messageViewPager)
            setElevationCompat(context.dpToPx(4f))
        }

        binding.openSendMessageButton.setOnClickListener { presenter.onSendMessageButtonClicked() }
    }

    override fun showContent(show: Boolean) {
        with(binding) {
            messageViewPager.visibility = if (show) VISIBLE else INVISIBLE
            messageTabLayout.visibility = if (show) VISIBLE else INVISIBLE
        }
    }

    override fun showProgress(show: Boolean) {
        binding.messageProgress.visibility = if (show) VISIBLE else INVISIBLE
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
