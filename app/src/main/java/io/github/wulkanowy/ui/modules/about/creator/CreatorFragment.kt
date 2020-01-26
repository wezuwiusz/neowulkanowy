package io.github.wulkanowy.ui.modules.about.creator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.openInternetBrowser
import io.github.wulkanowy.utils.setOnItemClickListener
import kotlinx.android.synthetic.main.fragment_creator.*
import javax.inject.Inject

class CreatorFragment : BaseFragment(), CreatorView, MainView.TitledView {

    @Inject
    lateinit var presenter: CreatorPresenter

    @Inject
    lateinit var creatorsAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    override val titleStringId get() = R.string.creators_title

    companion object {
        fun newInstance() = CreatorFragment()
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
            layoutManager = SmoothScrollLinearLayoutManager(context)
            adapter = creatorsAdapter
            addItemDecoration(FlexibleItemDecoration(context)
                .withDefaultDivider()
                .withDrawDividerOnLastItem(false))
        }
        creatorsAdapter.setOnItemClickListener(presenter::onItemSelected)
        creatorSeeMore.setOnClickListener { presenter.onSeeMoreClick() }
    }

    override fun updateData(data: List<CreatorItem>) {
        creatorsAdapter.updateDataSet(data)
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
