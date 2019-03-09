package io.github.wulkanowy.ui.modules.attendance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Attendance
import io.github.wulkanowy.ui.base.session.BaseSessionFragment
import io.github.wulkanowy.ui.modules.attendance.summary.AttendanceSummaryFragment
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.setOnItemClickListener
import kotlinx.android.synthetic.main.fragment_attendance.*
import javax.inject.Inject

class AttendanceFragment : BaseSessionFragment(), AttendanceView, MainView.MainChildView, MainView.TitledView {

    @Inject
    lateinit var presenter: AttendancePresenter

    @Inject
    lateinit var attendanceAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    companion object {
        private const val SAVED_DATE_KEY = "CURRENT_DATE"

        fun newInstance() = AttendanceFragment()
    }

    override val titleStringId: Int
        get() = R.string.attendance_title

    override val isViewEmpty: Boolean
        get() = attendanceAdapter.isEmpty

    override val currentStackSize: Int?
        get() = (activity as? MainActivity)?.currentStackSize

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_attendance, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        messageContainer = attendanceRecycler
        presenter.onAttachView(this, savedInstanceState?.getLong(SAVED_DATE_KEY))
    }

    override fun initView() {
        attendanceAdapter.apply {
            setOnItemClickListener { presenter.onAttendanceItemSelected(it) }
        }

        attendanceRecycler.run {
            layoutManager = SmoothScrollLinearLayoutManager(context)
            adapter = attendanceAdapter
            addItemDecoration(FlexibleItemDecoration(context)
                .withDefaultDivider()
                .withDrawDividerOnLastItem(false)
            )
        }
        attendanceSwipe.setOnRefreshListener { presenter.onSwipeRefresh() }
        attendancePreviousButton.setOnClickListener { presenter.onPreviousDay() }
        attendanceNextButton.setOnClickListener { presenter.onNextDay() }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.action_menu_attendance, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == R.id.attendanceMenuSummary) presenter.onSummarySwitchSelected()
        else false
    }

    override fun updateData(data: List<AttendanceItem>) {
        attendanceAdapter.updateDataSet(data, true)
    }

    override fun updateNavigationDay(date: String) {
        attendanceNavDate.text = date
    }

    override fun clearData() {
        attendanceAdapter.clear()
    }

    override fun resetView() {
        attendanceRecycler.smoothScrollToPosition(0)
    }

    override fun onFragmentReselected() {
        presenter.onViewReselected()
    }

    override fun popView() {
        (activity as? MainActivity)?.popView()
    }

    override fun showEmpty(show: Boolean) {
        attendanceEmpty.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showProgress(show: Boolean) {
        attendanceProgress.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun enableSwipe(enable: Boolean) {
        attendanceSwipe.isEnabled = enable
    }

    override fun showContent(show: Boolean) {
        attendanceRecycler.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun hideRefresh() {
        attendanceSwipe.isRefreshing = false
    }

    override fun showPreButton(show: Boolean) {
        attendancePreviousButton.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    override fun showNextButton(show: Boolean) {
        attendanceNextButton.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    override fun showAttendanceDialog(lesson: Attendance) {
        (activity as? MainActivity)?.showDialogFragment(AttendanceDialog.newInstance(lesson))
    }

    override fun openSummaryView() {
        (activity as? MainActivity)?.pushView(AttendanceSummaryFragment.newInstance())
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(SAVED_DATE_KEY, presenter.currentDate.toEpochDay())
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
