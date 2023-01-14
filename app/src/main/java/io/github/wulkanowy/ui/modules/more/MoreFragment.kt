package io.github.wulkanowy.ui.modules.more

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.databinding.FragmentMoreBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.Destination
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.modules.message.MessageFragment
import javax.inject.Inject

@AndroidEntryPoint
class MoreFragment : BaseFragment<FragmentMoreBinding>(R.layout.fragment_more), MoreView,
    MainView.TitledView, MainView.MainChildView {

    @Inject
    lateinit var presenter: MorePresenter

    @Inject
    lateinit var moreAdapter: MoreAdapter

    companion object {
        fun newInstance() = MoreFragment()
    }

    override val titleStringId: Int
        get() = R.string.more_title

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMoreBinding.bind(view)
        presenter.onAttachView(this)
    }

    override fun initView() {
        moreAdapter.onClickListener = presenter::onItemSelected

        with(binding.moreRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = moreAdapter
        }
    }

    override fun onFragmentReselected() {
        if (::presenter.isInitialized) presenter.onViewReselected()
    }

    override fun onFragmentChanged() {
        (parentFragmentManager.fragments.find { it is MessageFragment } as MessageFragment?)
            ?.onFragmentChanged()
    }

    override fun updateData(data: List<MoreItem>) {
        with(moreAdapter) {
            items = data
            notifyDataSetChanged()
        }
    }

    override fun popView(depth: Int) {
        (activity as? MainActivity)?.popView(depth)
    }

    override fun openView(destination: Destination) {
        (activity as? MainActivity)?.pushView(destination.destinationFragment)
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
