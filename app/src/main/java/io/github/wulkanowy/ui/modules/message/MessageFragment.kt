package io.github.wulkanowy.ui.modules.message

import android.os.Bundle
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.enums.MessageFolder.*
import io.github.wulkanowy.databinding.FragmentMessageBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.base.BaseFragmentPagerAdapter
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.modules.message.send.SendMessageActivity
import io.github.wulkanowy.ui.modules.message.tab.MessageTabFragment
import io.github.wulkanowy.utils.dpToPx
import io.github.wulkanowy.utils.setOnSelectPageListener
import javax.inject.Inject

@AndroidEntryPoint
class MessageFragment : BaseFragment<FragmentMessageBinding>(R.layout.fragment_message),
    MessageView, MainView.TitledView, MainView.MainChildView {

    @Inject
    lateinit var presenter: MessagePresenter

    private val pagerAdapter by lazy {
        BaseFragmentPagerAdapter(
            fragmentManager = childFragmentManager,
            pagesCount = 3,
            lifecycle = lifecycle,
        )
    }

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
        with(binding.messageViewPager) {
            adapter = pagerAdapter
            offscreenPageLimit = 2
            setOnSelectPageListener(presenter::onPageSelected)
        }

        with(pagerAdapter) {
            containerId = binding.messageViewPager.id
            titleFactory = {
                when (it) {
                    0 -> getString(R.string.message_inbox)
                    1 -> getString(R.string.message_sent)
                    2 -> getString(R.string.message_trash)
                    else -> throw IllegalStateException()
                }
            }
            itemFactory = {
                when (it) {
                    0 -> MessageTabFragment.newInstance(RECEIVED)
                    1 -> MessageTabFragment.newInstance(SENT)
                    2 -> MessageTabFragment.newInstance(TRASHED)
                    else -> throw IllegalStateException()
                }
            }
            TabLayoutMediator(binding.messageTabLayout, binding.messageViewPager, this).attach()
        }

        binding.messageTabLayout.elevation = requireContext().dpToPx(4f)
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

    override fun showNewMessage(show: Boolean) {
        binding.openSendMessageButton.run {
            if (show) show() else hide()
        }
    }

    override fun showTabLayout(show: Boolean) {
        binding.messageTabLayout.isVisible = show

        with(binding.messageViewPager) {
            isUserInputEnabled = show
            updateLayoutParams<ViewGroup.MarginLayoutParams> {
                updateMargins(top = if (show) requireContext().dpToPx(48f).toInt() else 0)
            }
        }
    }

    fun onChildFragmentShowActionMode(show: Boolean) {
        presenter.onChildViewShowActionMode(show)
    }

    fun onChildFragmentLoaded() {
        presenter.onChildViewLoaded()
    }

    fun onChildFragmentShowNewMessage(show: Boolean) {
        presenter.onChildViewShowNewMessage(show)
    }

    override fun onFragmentReselected() {
        if (::presenter.isInitialized) presenter.onFragmentReselected()
    }

    override fun onFragmentChanged() {
        if (::presenter.isInitialized) presenter.onFragmentChanged()
    }

    override fun notifyChildLoadData(index: Int, forceRefresh: Boolean) {
        (pagerAdapter.getFragmentInstance(index) as? MessageTabFragment)
            ?.onParentLoadData(forceRefresh)
    }

    override fun notifyChildrenFinishActionMode() {
        repeat(3) {
            (pagerAdapter.getFragmentInstance(it) as? MessageTabFragment)
                ?.onParentFinishActionMode()
        }
    }

    override fun notifyChildParentReselected(index: Int) {
        (pagerAdapter.getFragmentInstance(index) as? MessageTabFragment)
            ?.onParentReselected()
    }

    override fun openSendMessage() {
        context?.let { it.startActivity(SendMessageActivity.getStartIntent(it)) }
    }

    override fun popView() {
        (activity as? MainActivity)?.popView()
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
