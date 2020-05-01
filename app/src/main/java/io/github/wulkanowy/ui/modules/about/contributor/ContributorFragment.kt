package io.github.wulkanowy.ui.modules.about.contributor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.wulkanowy.R
import io.github.wulkanowy.data.pojos.Contributor
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.widgets.DividerItemDecoration
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.openInternetBrowser
import kotlinx.android.synthetic.main.fragment_creator.*
import javax.inject.Inject

class ContributorFragment : BaseFragment(), ContributorView, MainView.TitledView {

    @Inject
    lateinit var presenter: ContributorPresenter

    @Inject
    lateinit var creatorsAdapter: ContributorAdapter

    override val titleStringId get() = R.string.contributors_title

    companion object {
        fun newInstance() = ContributorFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_creator, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun initView() {
        with(creatorRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = creatorsAdapter
            addItemDecoration(DividerItemDecoration(context))
        }
        creatorsAdapter.onClickListener = presenter::onItemSelected
        creatorSeeMore.setOnClickListener { presenter.onSeeMoreClick() }
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
        creatorProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
