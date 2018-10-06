package io.github.wulkanowy.ui.main.attendance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Attendance
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.utils.setOnItemClickListener
import kotlinx.android.synthetic.main.fragment_attendance.*
import javax.inject.Inject

class AttendanceFragment : BaseFragment(), AttendanceView {

    @Inject
    lateinit var presenter: AttendancePresenter

    @Inject
    lateinit var attendanceAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    companion object {
        private const val SAVED_DATE_KEY = "CURRENT_DATE"
        fun newInstance() = AttendanceFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_attendance, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.run {
            attachView(this@AttendanceFragment)
            loadData(date = savedInstanceState?.getLong(SAVED_DATE_KEY))
        }
    }

    override fun initView() {
        attendanceAdapter.run {
            isAutoCollapseOnExpand = true
            isAutoScrollOnExpand = true
            setOnItemClickListener { presenter.onAttendanceItemSelected(getItem(it))}
        }
        attendanceRecycler.run {
            layoutManager = SmoothScrollLinearLayoutManager(context)
            adapter = attendanceAdapter
        }
        attendanceSwipe.setOnRefreshListener { presenter.loadData(date = null, forceRefresh = true) }
        attendancePreviousButton.setOnClickListener { presenter.loadAttendanceForPreviousDay() }
        attendanceNextButton.setOnClickListener { presenter.loadAttendanceForNextDay() }
    }

    override fun updateData(data: List<AttendanceItem>) {
        attendanceAdapter.updateDataSet(data, true)
    }

    override fun clearData() {
        attendanceAdapter.clear()
    }

    override fun updateNavigationDay(date: String) {
        attendanceNavDate.text = date
    }

    override fun isViewEmpty() = attendanceAdapter.isEmpty

    override fun showEmpty(show: Boolean) {
        attendanceEmpty.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showProgress(show: Boolean) {
        attendanceProgress.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showContent(show: Boolean) {
        attendanceRecycler.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showRefresh(show: Boolean) {
        attendanceSwipe.isRefreshing = show
    }

    override fun showPreButton(show: Boolean) {
        attendancePreviousButton.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    override fun showNextButton(show: Boolean) {
        attendanceNextButton.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    override fun showAttendanceDialog(lesson: Attendance) {
        AttendanceDialog.newInstance(lesson).show(fragmentManager, lesson.toString())
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

