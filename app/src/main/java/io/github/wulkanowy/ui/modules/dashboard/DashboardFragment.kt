package io.github.wulkanowy.ui.modules.dashboard

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.databinding.FragmentDashboardBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.account.accountdetails.AccountDetailsFragment
import io.github.wulkanowy.ui.modules.attendance.summary.AttendanceSummaryFragment
import io.github.wulkanowy.ui.modules.conference.ConferenceFragment
import io.github.wulkanowy.ui.modules.exam.ExamFragment
import io.github.wulkanowy.ui.modules.grade.GradeFragment
import io.github.wulkanowy.ui.modules.homework.HomeworkFragment
import io.github.wulkanowy.ui.modules.luckynumber.LuckyNumberFragment
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.modules.message.MessageFragment
import io.github.wulkanowy.ui.modules.notificationscenter.NotificationsCenterFragment
import io.github.wulkanowy.ui.modules.schoolannouncement.SchoolAnnouncementFragment
import io.github.wulkanowy.ui.modules.timetable.TimetableFragment
import io.github.wulkanowy.utils.capitalise
import io.github.wulkanowy.utils.getThemeAttrColor
import io.github.wulkanowy.utils.openInternetBrowser
import io.github.wulkanowy.utils.toFormattedString
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class DashboardFragment : BaseFragment<FragmentDashboardBinding>(R.layout.fragment_dashboard),
    DashboardView, MainView.TitledView, MainView.MainChildView {

    @Inject
    lateinit var presenter: DashboardPresenter

    @Inject
    lateinit var dashboardAdapter: DashboardAdapter

    override val titleStringId get() = R.string.dashboard_title

    override var subtitleString =
        LocalDate.now().toFormattedString("EEEE, d MMMM yyyy").capitalise()

    companion object {

        fun newInstance() = DashboardFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDashboardBinding.bind(view)
        presenter.onAttachView(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.action_menu_dashboard, menu)
    }

    override fun initView() {
        val mainActivity = requireActivity() as MainActivity
        val itemTouchHelper = ItemTouchHelper(
            DashboardItemMoveCallback(dashboardAdapter, presenter::onDragAndDropEnd)
        )

        dashboardAdapter.apply {
            onAccountTileClickListener = {
                mainActivity.pushView(AccountDetailsFragment.newInstance(it))
            }
            onLuckyNumberTileClickListener = {
                mainActivity.pushView(LuckyNumberFragment.newInstance())
            }
            onMessageTileClickListener = { mainActivity.pushView(MessageFragment.newInstance()) }
            onAttendanceTileClickListener = {
                mainActivity.pushView(AttendanceSummaryFragment.newInstance())
            }
            onLessonsTileClickListener = {
                mainActivity.pushView(TimetableFragment.newInstance(it))
            }
            onGradeTileClickListener = { mainActivity.pushView(GradeFragment.newInstance()) }
            onHomeworkTileClickListener = { mainActivity.pushView(HomeworkFragment.newInstance()) }
            onAnnouncementsTileClickListener = {
                mainActivity.pushView(SchoolAnnouncementFragment.newInstance())
            }
            onExamsTileClickListener = { mainActivity.pushView(ExamFragment.newInstance()) }
            onConferencesTileClickListener = {
                mainActivity.pushView(ConferenceFragment.newInstance())
            }
            onAdminMessageClickListener = presenter::onAdminMessageSelected

            registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    binding.dashboardRecycler.scrollToPosition(0)
                }
            })
        }

        with(binding) {
            dashboardErrorRetry.setOnClickListener { presenter.onRetry() }
            dashboardErrorDetails.setOnClickListener { presenter.onDetailsClick() }
            dashboardSwipe.setOnRefreshListener(presenter::onSwipeRefresh)
            dashboardSwipe.setColorSchemeColors(requireContext().getThemeAttrColor(R.attr.colorPrimary))
            dashboardSwipe.setProgressBackgroundColorSchemeColor(
                requireContext().getThemeAttrColor(R.attr.colorSwipeRefresh)
            )

            with(dashboardRecycler) {
                layoutManager = LinearLayoutManager(context)
                adapter = dashboardAdapter
                (itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false
            }

            itemTouchHelper.attachToRecyclerView(dashboardRecycler)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.dashboard_menu_tiles -> presenter.onDashboardTileSettingsSelected()
            R.id.dashboard_menu_notifaction_list -> presenter.onNotificationsCenterSelected()
            else -> false
        }
    }

    override fun showDashboardTileSettings(selectedItems: List<DashboardItem.Tile>) {
        val entries = requireContext().resources.getStringArray(R.array.dashboard_tile_entries)
        val values = requireContext().resources.getStringArray(R.array.dashboard_tile_values)
        val selectedItemsState = values.map { value -> selectedItems.any { it.name == value } }

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.pref_dashboard_appearance_tiles_title)
            .setMultiChoiceItems(entries, selectedItemsState.toBooleanArray()) { _, _, _ -> }
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                val selectedState = (dialog as AlertDialog).listView.checkedItemPositions
                val selectedValues = values.filterIndexed { index, _ -> selectedState[index] }

                presenter.onDashboardTileSettingSelected(selectedValues)
            }
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .show()
    }

    override fun updateData(data: List<DashboardItem>) {
        dashboardAdapter.submitList(data.toMutableList())
    }

    override fun showMessage(text: String) {
        //Empty function to avoid message flood
    }

    override fun showRefresh(show: Boolean) {
        binding.dashboardSwipe.isRefreshing = show
    }

    override fun showProgress(show: Boolean) {
        binding.dashboardProgress.isVisible = show
    }

    override fun showContent(show: Boolean) {
        binding.dashboardRecycler.isVisible = show
    }

    override fun showErrorView(show: Boolean) {
        binding.dashboardErrorContainer.isVisible = show
    }

    override fun setErrorDetails(message: String) {
        binding.dashboardErrorMessage.text = message
    }

    override fun resetView() {
        binding.dashboardRecycler.smoothScrollToPosition(0)
    }

    override fun popViewToRoot() {
        (requireActivity() as MainActivity).popView(20)
    }

    override fun onFragmentReselected() {
        if (::presenter.isInitialized) presenter.onViewReselected()
    }

    override fun openNotificationsCenterView() {
        (requireActivity() as MainActivity).pushView(NotificationsCenterFragment.newInstance())
    }

    override fun openInternetBrowser(url: String) {
        requireContext().openInternetBrowser(url)
    }

    override fun onDestroyView() {
        dashboardAdapter.clearTimers()
        presenter.onDetachView()
        super.onDestroyView()
    }
}