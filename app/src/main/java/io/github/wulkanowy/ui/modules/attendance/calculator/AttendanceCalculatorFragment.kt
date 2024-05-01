package io.github.wulkanowy.ui.modules.attendance.calculator

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.pojos.AttendanceData
import io.github.wulkanowy.databinding.FragmentAttendanceCalculatorBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.modules.settings.appearance.AppearanceFragment
import io.github.wulkanowy.ui.widgets.DividerItemDecoration
import io.github.wulkanowy.utils.getThemeAttrColor
import javax.inject.Inject

@AndroidEntryPoint
class AttendanceCalculatorFragment :
    BaseFragment<FragmentAttendanceCalculatorBinding>(R.layout.fragment_attendance_calculator),
    AttendanceCalculatorView, MainView.TitledView {

    @Inject
    lateinit var presenter: AttendanceCalculatorPresenter

    @Inject
    lateinit var attendanceCalculatorAdapter: AttendanceCalculatorAdapter

    override val titleStringId get() = R.string.attendance_title

    companion object {
        fun newInstance() = AttendanceCalculatorFragment()
    }

    override val isViewEmpty get() = attendanceCalculatorAdapter.items.isEmpty()

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAttendanceCalculatorBinding.bind(view)
        messageContainer = binding.attendanceCalculatorRecycler
        presenter.onAttachView(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.action_menu_attendance_calculator, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.attendance_calculator_menu_settings) presenter.onSettingsSelected()
        else false
    }

    override fun openSettingsView() {
        (activity as? MainActivity)?.pushView(AppearanceFragment.withFocusedPreference(getString(R.string.pref_key_attendance_target)))
    }

    override fun initView() {
        with(binding.attendanceCalculatorRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = attendanceCalculatorAdapter
            addItemDecoration(DividerItemDecoration(context))
        }

        with(binding) {
            attendanceCalculatorSwipe.setOnRefreshListener(presenter::onSwipeRefresh)
            attendanceCalculatorSwipe.setColorSchemeColors(requireContext().getThemeAttrColor(R.attr.colorPrimary))
            attendanceCalculatorSwipe.setProgressBackgroundColorSchemeColor(
                requireContext().getThemeAttrColor(
                    R.attr.colorSwipeRefresh
                )
            )
            attendanceCalculatorErrorRetry.setOnClickListener { presenter.onRetry() }
            attendanceCalculatorErrorDetails.setOnClickListener { presenter.onDetailsClick() }
        }
    }

    override fun updateData(data: List<AttendanceData>) {
        with(attendanceCalculatorAdapter) {
            items = data
            notifyDataSetChanged()
        }
    }

    override fun clearView() {
        with(attendanceCalculatorAdapter) {
            items = emptyList()
            notifyDataSetChanged()
        }
    }

    override fun showEmpty(show: Boolean) {
        binding.attendanceCalculatorEmpty.isVisible = show
    }

    override fun showErrorView(show: Boolean) {
        binding.attendanceCalculatorError.isVisible = show
    }

    override fun setErrorDetails(message: String) {
        binding.attendanceCalculatorErrorMessage.text = message
    }

    override fun showProgress(show: Boolean) {
        binding.attendanceCalculatorProgress.isVisible = show
    }

    override fun enableSwipe(enable: Boolean) {
        binding.attendanceCalculatorSwipe.isEnabled = enable
    }

    override fun showContent(show: Boolean) {
        binding.attendanceCalculatorRecycler.isVisible = show
    }

    override fun showRefresh(show: Boolean) {
        binding.attendanceCalculatorSwipe.isRefreshing = show
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
