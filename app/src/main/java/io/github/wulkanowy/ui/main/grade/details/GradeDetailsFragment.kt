package io.github.wulkanowy.ui.main.grade.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IExpandable
import eu.davidea.flexibleadapter.items.IFlexible
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.main.grade.GradeFragment
import io.github.wulkanowy.ui.main.grade.GradeView
import io.github.wulkanowy.utils.setOnItemClickListener
import kotlinx.android.synthetic.main.fragment_grade_details.*
import javax.inject.Inject

class GradeDetailsFragment : BaseFragment(), GradeDetailsView, GradeView.GradeChildView {

    @Inject
    lateinit var presenter: GradeDetailsPresenter

    @Inject
    lateinit var gradeDetailsAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    companion object {
        fun newInstance() = GradeDetailsFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_grade_details, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        messageContainer = gradeDetailsRecycler
        presenter.onAttachView(this)
    }

    override fun initView() {
        gradeDetailsAdapter.run {
            isAutoCollapseOnExpand = true
            isAutoScrollOnExpand = true
            setOnItemClickListener { presenter.onGradeItemSelected(getItem(it)) }
        }

        gradeDetailsAdapter.getItemCountOfTypes()

        gradeDetailsRecycler.run {
            layoutManager = SmoothScrollLinearLayoutManager(context)
            adapter = gradeDetailsAdapter
        }
        gradeDetailsSwipe.setOnRefreshListener { presenter.onSwipeRefresh() }
    }

    override fun updateData(data: List<GradeDetailsHeader>) {
        gradeDetailsAdapter.updateDataSet(data, true)
    }

    override fun updateItem(item: AbstractFlexibleItem<*>) {
        gradeDetailsAdapter.updateItem(item)
    }

    override fun clearView() {
        gradeDetailsAdapter.clear()
    }

    override fun resetView() {
        gradeDetailsAdapter.apply {
            smoothScrollToPosition(0)
            collapseAll()
        }
    }

    override fun getHeaderOfItem(item: AbstractFlexibleItem<*>): IExpandable<*, out IFlexible<*>>? {
        return gradeDetailsAdapter.getExpandableOf(item)
    }

    override fun isViewEmpty() = gradeDetailsAdapter.isEmpty

    override fun showProgress(show: Boolean) {
        gradeDetailsProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun showContent(show: Boolean) {
        gradeDetailsRecycler.visibility = if (show) VISIBLE else INVISIBLE
    }

    override fun showEmpty(show: Boolean) {
        gradeDetailsEmpty.visibility = if (show) VISIBLE else INVISIBLE
    }

    override fun showRefresh(show: Boolean) {
        gradeDetailsSwipe.isRefreshing = show
    }

    override fun showGradeDialog(grade: Grade) {
        GradeDetailsDialog.newInstance(grade).show(fragmentManager, grade.toString())
    }

    override fun onParentLoadData(semesterId: Int, forceRefresh: Boolean) {
        presenter.onParentViewLoadData(semesterId, forceRefresh)
    }

    override fun onParentReselected() {
        presenter.onParentViewReselected()
    }

    override fun onParentChangeSemester() {
        presenter.onParentViewChangeSemester()
    }

    override fun notifyParentDataLoaded(semesterId: Int) {
        (parentFragment as? GradeFragment)?.onChildFragmentLoaded(semesterId)
    }

    override fun notifyParentRefresh() {
        (parentFragment as? GradeFragment)?.onChildRefresh()
    }

    override fun emptyAverageString(): String = getString(R.string.grade_no_average)

    override fun averageString(): String = getString(R.string.grade_average)

    override fun gradeNumberString(number: Int): String = resources.getQuantityString(R.plurals.grade_number_item, number, number)

    override fun weightString(): String = getString(R.string.grade_weight)

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDetachView()
    }
}
