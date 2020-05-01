package io.github.wulkanowy.ui.modules.attendance

import android.content.DialogInterface.BUTTON_POSITIVE
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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.widget.LinearLayoutManager
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Attendance
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.widgets.DividerItemDecoration
import io.github.wulkanowy.ui.modules.attendance.summary.AttendanceSummaryFragment
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.SchooldaysRangeLimiter
import io.github.wulkanowy.utils.dpToPx
import kotlinx.android.synthetic.main.dialog_excuse.*
import kotlinx.android.synthetic.main.fragment_attendance.*
import org.threeten.bp.LocalDate
import javax.inject.Inject

class AttendanceFragment : BaseFragment(), AttendanceView, MainView.MainChildView,
    MainView.TitledView {

    @Inject
    lateinit var presenter: AttendancePresenter

    @Inject
    lateinit var attendanceAdapter: AttendanceAdapter

    override val excuseSuccessString: String
        get() = getString(R.string.attendance_excuse_success)

    override val excuseNoSelectionString: String
        get() = getString(R.string.attendance_excuse_no_selection)

    companion object {
        private const val SAVED_DATE_KEY = "CURRENT_DATE"

        fun newInstance() = AttendanceFragment()
    }

    override val titleStringId get() = R.string.attendance_title

    override val isViewEmpty get() = attendanceAdapter.items.isEmpty()

    override val currentStackSize get() = (activity as? MainActivity)?.currentStackSize

    override val excuseActionMode: Boolean get() = attendanceAdapter.excuseActionMode

    private var actionMode: ActionMode? = null
    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            val inflater = mode.menuInflater
            inflater.inflate(R.menu.context_menu_excuse, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.title = getString(R.string.attendance_excuse_title)
            return presenter.onPrepareActionMode()
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            presenter.onDestroyActionMode()
            actionMode = null
        }

        override fun onActionItemClicked(mode: ActionMode, menu: MenuItem): Boolean {
            return when (menu.itemId) {
                R.id.excuseMenuSubmit -> presenter.onExcuseSubmitButtonClick()
                else -> false
            }
        }
    }

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
        with(attendanceAdapter) {
            onClickListener = presenter::onAttendanceItemSelected
            onExcuseCheckboxSelect = presenter::onExcuseCheckboxSelect
        }

        with(attendanceRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = attendanceAdapter
            addItemDecoration(DividerItemDecoration(context))
        }

        attendanceSwipe.setOnRefreshListener(presenter::onSwipeRefresh)
        attendanceErrorRetry.setOnClickListener { presenter.onRetry() }
        attendanceErrorDetails.setOnClickListener { presenter.onDetailsClick() }

        attendancePreviousButton.setOnClickListener { presenter.onPreviousDay() }
        attendanceNavDate.setOnClickListener { presenter.onPickDate() }
        attendanceNextButton.setOnClickListener { presenter.onNextDay() }

        attendanceExcuseButton.setOnClickListener { presenter.onExcuseButtonClick() }

        attendanceNavContainer.setElevationCompat(requireContext().dpToPx(8f))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.action_menu_attendance, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.attendanceMenuSummary) presenter.onSummarySwitchSelected()
        else false
    }

    override fun updateData(data: List<Attendance>) {
        with(attendanceAdapter) {
            items = data
            notifyDataSetChanged()
        }
    }

    override fun updateNavigationDay(date: String) {
        attendanceNavDate.text = date
    }

    override fun clearData() {
        with(attendanceAdapter) {
            items = emptyList()
            notifyDataSetChanged()
        }
    }

    override fun resetView() {
        attendanceRecycler.smoothScrollToPosition(0)
    }

    override fun onFragmentReselected() {
        if (::presenter.isInitialized) presenter.onViewReselected()
    }

    override fun onFragmentChanged() {
        if (::presenter.isInitialized) presenter.onMainViewChanged()
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

    override fun showExcuseButton(show: Boolean) {
        attendanceExcuseButton.visibility = if (show) VISIBLE else GONE
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

    override fun showExcuseDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.attendance_excuse_title)
            .setView(R.layout.dialog_excuse)
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .create()
            .apply {
                setButton(BUTTON_POSITIVE, getString(R.string.attendance_excuse_dialog_submit)) { _, _ ->
                    presenter.onExcuseDialogSubmit(excuseReason.text?.toString().orEmpty())
                }
            }.show()
    }

    override fun openSummaryView() {
        (activity as? MainActivity)?.pushView(AttendanceSummaryFragment.newInstance())
    }

    override fun startActionMode() {
        actionMode = (activity as MainActivity?)?.startSupportActionMode(actionModeCallback)
    }

    override fun showExcuseCheckboxes(show: Boolean) {
        attendanceAdapter.apply {
            excuseActionMode = show
            notifyDataSetChanged()
        }
    }

    override fun finishActionMode() {
        actionMode?.finish()
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
