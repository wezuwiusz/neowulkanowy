package io.github.wulkanowy.ui.modules.conference

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Conference
import io.github.wulkanowy.databinding.FragmentConferenceBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.widgets.DividerItemDecoration
import javax.inject.Inject

@AndroidEntryPoint
class ConferenceFragment : BaseFragment<FragmentConferenceBinding>(R.layout.fragment_conference),
    ConferenceView, MainView.TitledView {

    @Inject
    lateinit var presenter: ConferencePresenter

    @Inject
    lateinit var conferencesAdapter: ConferenceAdapter

    companion object {
        fun newInstance() = ConferenceFragment()
    }

    override val isViewEmpty: Boolean
        get() = conferencesAdapter.items.isEmpty()

    override val titleStringId: Int
        get() = R.string.conferences_title

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentConferenceBinding.bind(view)
        messageContainer = binding.conferenceRecycler
        presenter.onAttachView(this)
    }

    override fun initView() {
        with(binding.conferenceRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = conferencesAdapter
            addItemDecoration(DividerItemDecoration(context))
        }

        with(binding) {
            conferenceSwipe.setOnRefreshListener { presenter.onSwipeRefresh() }
            conferenceErrorRetry.setOnClickListener { presenter.onRetry() }
            conferenceErrorDetails.setOnClickListener { presenter.onDetailsClick() }
        }
    }

    override fun updateData(data: List<Conference>) {
        with(conferencesAdapter) {
            items = data
            notifyDataSetChanged()
        }
    }

    override fun clearData() {
        with(conferencesAdapter) {
            items = emptyList()
            notifyDataSetChanged()
        }
    }

    override fun showRefresh(show: Boolean) {
        binding.conferenceSwipe.isRefreshing = show
    }

    override fun showProgress(show: Boolean) {
        binding.conferenceProgress.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showEmpty(show: Boolean) {
        binding.conferenceEmpty.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showErrorView(show: Boolean) {
        binding.conferenceError.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun setErrorDetails(message: String) {
        binding.conferenceErrorMessage.text = message
    }

    override fun enableSwipe(enable: Boolean) {
        binding.conferenceSwipe.isEnabled = enable
    }

    override fun showContent(show: Boolean) {
        binding.conferenceRecycler.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
