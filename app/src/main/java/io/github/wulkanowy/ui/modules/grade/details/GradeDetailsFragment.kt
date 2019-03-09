package io.github.wulkanowy.ui.modules.grade.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IExpandable
import eu.davidea.flexibleadapter.items.IFlexible
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.ui.base.session.BaseSessionFragment
import io.github.wulkanowy.ui.modules.grade.GradeFragment
import io.github.wulkanowy.ui.modules.grade.GradeView
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.utils.setOnItemClickListener
import kotlinx.android.synthetic.main.fragment_grade_details.*
import javax.inject.Inject

class GradeDetailsFragment : BaseSessionFragment(), GradeDetailsView, GradeView.GradeChildView {

    @Inject
    lateinit var presenter: GradeDetailsPresenter

    @Inject
    lateinit var gradeDetailsAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    companion object {
        fun newInstance() = GradeDetailsFragment()
    }

    override val emptyAverageString: String
        get() = getString(R.string.grade_no_average)

    override val averageString: String
        get() = getString(R.string.grade_average)

    override val weightString: String
        get() = getString(R.string.grade_weight)

    override val noDescriptionString: String
        get() = getString(R.string.all_no_description)

    override val isViewEmpty
        get() = gradeDetailsAdapter.isEmpty

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_grade_details, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        messageContainer = gradeDetailsRecycler
        presenter.onAttachView(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.action_menu_grade_details, menu)
    }

    override fun initView() {
        gradeDetailsAdapter.run {
            isAutoCollapseOnExpand = true
            isAutoScrollOnExpand = true
            setOnItemClickListener { presenter.onGradeItemSelected(it) }
        }

        gradeDetailsRecycler.run {
            layoutManager = SmoothScrollLinearLayoutManager(context)
            adapter = gradeDetailsAdapter
            addItemDecoration(GradeDetailsHeaderItemDecoration(context)
                .withDefaultDivider(R.layout.header_grade_details)
            )
        }
        gradeDetailsSwipe.setOnRefreshListener { presenter.onSwipeRefresh() }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == R.id.gradeDetailsMenuRead) presenter.onMarkAsReadSelected()
        else false
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

    override fun collapseAllItems() {
        gradeDetailsAdapter.collapseAll()
    }

    override fun scrollToStart() {
        gradeDetailsRecycler.scrollToPosition(0)
    }

    override fun getHeaderOfItem(item: AbstractFlexibleItem<*>): IExpandable<*, out IFlexible<*>>? {
        return gradeDetailsAdapter.getExpandableOf(item)
    }

    override fun getGradeNumberString(number: Int): String {
        return resources.getQuantityString(R.plurals.grade_number_item, number, number)
    }

    override fun showProgress(show: Boolean) {
        gradeDetailsProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun enableSwipe(enable: Boolean) {
        gradeDetailsSwipe.isEnabled = enable
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

    override fun showGradeDialog(grade: Grade, colorScheme: String) {
        (activity as? MainActivity)?.showDialogFragment(GradeDetailsDialog.newInstance(grade, colorScheme))
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

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDetachView()
    }
}
