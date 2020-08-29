package io.github.wulkanowy.ui.modules.about.contributor

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.pojos.Contributor
import io.github.wulkanowy.databinding.FragmentContributorBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.widgets.DividerItemDecoration
import io.github.wulkanowy.utils.openInternetBrowser
import javax.inject.Inject

@AndroidEntryPoint
class ContributorFragment : BaseFragment<FragmentContributorBinding>(R.layout.fragment_contributor),
    ContributorView, MainView.TitledView {

    @Inject
    lateinit var presenter: ContributorPresenter

    @Inject
    lateinit var creatorsAdapter: ContributorAdapter

    override val titleStringId get() = R.string.contributors_title

    companion object {
        fun newInstance() = ContributorFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentContributorBinding.bind(view)
        presenter.onAttachView(this)
    }

    override fun initView() {
        with(binding.creatorRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = creatorsAdapter
            addItemDecoration(DividerItemDecoration(context))
        }
        creatorsAdapter.onClickListener = presenter::onItemSelected
        binding.creatorSeeMore.setOnClickListener { presenter.onSeeMoreClick() }
    }

    override fun updateData(data: List<Contributor>) {
        with(creatorsAdapter) {
            items = data
            notifyDataSetChanged()
        }
    }

    override fun openUserGithubPage(username: String) {
        context?.openInternetBrowser("https://github.com/${username}", ::showMessage)
    }

    override fun openGithubContributorsPage() {
        context?.openInternetBrowser("https://github.com/wulkanowy/wulkanowy/graphs/contributors", ::showMessage)
    }

    override fun showProgress(show: Boolean) {
        binding.creatorProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
