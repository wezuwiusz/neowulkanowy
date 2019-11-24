package io.github.wulkanowy.ui.modules.attendance

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
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Attendance
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.attendance.summary.AttendanceSummaryFragment
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.SchooldaysRangeLimiter
import io.github.wulkanowy.utils.dpToPx
import io.github.wulkanowy.utils.setOnItemClickListener
import kotlinx.android.synthetic.main.fragment_attendance.*
import org.threeten.bp.LocalDate
import javax.inject.Inject

class AttendanceFragment : BaseFragment(), AttendanceView, MainView.MainChildView,
    MainView.TitledView {

    @Inject
    lateinit var presenter: AttendancePresenter

    @Inject
    lateinit var attendanceAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    companion object {
        private const val SAVED_DATE_KEY = "CURRENT_DATE"

        fun newInstance() = AttendanceFragment()
    }

    override val titleStringId get() = R.string.attendance_title

    override val isViewEmpty get() = attendanceAdapter.isEmpty

    override val currentStackSize get() = (activity as? MainActivity)?.currentStackSize

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
        attendanceAdapter.setOnItemClickListener(presenter::onAttendanceItemSelected)

        with(attendanceRecycler) {
            layoutManager = SmoothScrollLinearLayoutManager(context)
            adapter = attendanceAdapter
            addItemDecoration(FlexibleItemDecoration(context)
                .withDefaultDivider()
                .withDrawDividerOnLastItem(false))
        }

        attendanceSwipe.setOnRefreshListener(presenter::onSwipeRefresh)
        attendanceErrorRetry.setOnClickListener { presenter.onRetry() }
        attendanceErrorDetails.setOnClickListener { presenter.onDetailsClick() }

        attendancePreviousButton.setOnClickListener { presenter.onPreviousDay() }
        attendanceNavDate.setOnClickListener { presenter.onPickDate() }
        attendanceNextButton.setOnClickListener { presenter.onNextDay() }

        attendanceNavContainer.setElevationCompat(requireContext().dpToPx(8f))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.action_menu_attendance, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.attendanceMenuSummary) presenter.onSummarySwitchSelected()
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
        if (::presenter.isInitialized) presenter.onViewReselected()
    }

    override fun popView() {
        (activity as? MainActivity)?.popView()
    }

    override fun showEmpty(show: Boolean) {
        attendanceEmpty.visibility = if (show) VISIBLE else GONE
    }

    override fun showErrorView(show: Boolean) {
        attendanceError.visibility = if (show) VISIBLE else GONE
    }

    override fun setErrorDetails(message: String) {
        attendanceErrorMessage.text = message
    }

    override fun showProgress(show: Boolean) {
        attendanceProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun enableSwipe(enable: Boolean) {
        attendanceSwipe.isEnabled = enable
    }

    override fun showContent(show: Boolean) {
        attendanceRecycler.visibility = if (show) VISIBLE else GONE
    }

    override fun hideRefresh() {
        attendanceSwipe.isRefreshing = false
    }

    override fun showPreButton(show: Boolean) {
        attendancePreviousButton.visibility = if (show) VISIBLE else INVISIBLE
    }

    override fun showNextButton(show: Boolean) {
        attendanceNextButton.visibility = if (show) VISIBLE else INVISIBLE
    }

    override fun showAttendanceDialog(lesson: Attendance) {
        (activity as? MainActivity)?.showDialogFragment(AttendanceDialog.newInstance(lesson))
    }

    override fun showDatePickerDialog(currentDate: LocalDate) {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            presenter.onDateSet(year, month + 1, dayOfMonth)
        }
        val datePickerDialog = DatePickerDialog.newInstance(dateSetListener,
            currentDate.year, currentDate.monthValue - 1, currentDate.dayOfMonth)

        with(datePickerDialog) {
            setDateRangeLimiter(SchooldaysRangeLimiter())
            version = DatePickerDialog.Version.VERSION_2
            scrollOrientation = DatePickerDialog.ScrollOrientation.VERTICAL
            show(this@AttendanceFragment.parentFragmentManager, null)
        }
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
