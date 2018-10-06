package io.github.wulkanowy.ui.main.timetable

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.utils.setOnItemClickListener
import kotlinx.android.synthetic.main.fragment_timetable.*
import javax.inject.Inject

class TimetableFragment : BaseFragment(), TimetableView {

    @Inject
    lateinit var presenter: TimetablePresenter

    @Inject
    lateinit var timetableAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    companion object {
        private const val SAVED_DATE_KEY = "CURRENT_DATE"
        fun newInstance() = TimetableFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_timetable, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.run {
            attachView(this@TimetableFragment)
            loadData(date = savedInstanceState?.getLong(SAVED_DATE_KEY))
        }
    }

    override fun initView() {
        timetableAdapter.run {
            isAutoCollapseOnExpand = true
            isAutoScrollOnExpand = true
            setOnItemClickListener { presenter.onTimetableItemSelected(getItem(it))}
        }
        timetableRecycler.run {
            layoutManager = SmoothScrollLinearLayoutManager(context)
            adapter = timetableAdapter
        }
        timetableSwipe.setOnRefreshListener { presenter.loadData(date = null, forceRefresh = true) }
        timetablePreviousButton.setOnClickListener { presenter.loadTimetableForPreviousDay() }
        timetableNextButton.setOnClickListener { presenter.loadTimetableForNextDay() }
    }

    override fun updateData(data: List<TimetableItem>) {
        timetableAdapter.updateDataSet(data, true)
    }

    override fun clearData() {
        timetableAdapter.clear()
    }

    override fun updateNavigationDay(date: String) {
        timetableNavDate.text = date
    }

    override fun isViewEmpty() = timetableAdapter.isEmpty

    override fun showEmpty(show: Boolean) {
        timetableEmpty.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showProgress(show: Boolean) {
        timetableProgress.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showContent(show: Boolean) {
        timetableRecycler.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showRefresh(show: Boolean) {
        timetableSwipe.isRefreshing = show
    }

    override fun showPreButton(show: Boolean) {
        timetablePreviousButton.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    override fun showNextButton(show: Boolean) {
        timetableNextButton.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    override fun showTimetableDialog(lesson: Timetable) {
        TimetableDialog.newInstance(lesson).show(fragmentManager, lesson.toString())
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(SAVED_DATE_KEY, presenter.currentDate.toEpochDay())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
    }
}
