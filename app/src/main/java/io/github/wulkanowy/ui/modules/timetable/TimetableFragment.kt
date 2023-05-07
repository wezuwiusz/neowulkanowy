package io.github.wulkanowy.ui.modules.timetable

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.os.bundleOf
import androidx.core.text.parseAsHtml
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.databinding.FragmentTimetableBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.modules.timetable.additional.AdditionalLessonsFragment
import io.github.wulkanowy.ui.modules.timetable.completed.CompletedLessonsFragment
import io.github.wulkanowy.ui.widgets.DividerItemDecoration
import io.github.wulkanowy.utils.*
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class TimetableFragment : BaseFragment<FragmentTimetableBinding>(R.layout.fragment_timetable),
    TimetableView, MainView.MainChildView, MainView.TitledView {

    @Inject
    lateinit var presenter: TimetablePresenter

    @Inject
    lateinit var timetableAdapter: TimetableAdapter

    companion object {
        private const val SAVED_DATE_KEY = "CURRENT_DATE"

        private const val ARGUMENT_DATE_KEY = "ARGUMENT_DATE"

        fun newInstance(date: LocalDate? = null) = TimetableFragment().apply {
            arguments = date?.let { bundleOf(ARGUMENT_DATE_KEY to it.toEpochDay()) }
        }
    }

    override val titleStringId get() = R.string.timetable_title

    override val isViewEmpty get() = timetableAdapter.itemCount == 0

    override val currentStackSize get() = (activity as? MainActivity)?.currentStackSize

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTimetableBinding.bind(view)
        messageContainer = binding.timetableRecycler

        val initDate = savedInstanceState?.getLong(SAVED_DATE_KEY)
            ?: arguments?.getLong(ARGUMENT_DATE_KEY)?.takeUnless { it == 0L }

        presenter.onAttachView(this, initDate)
    }

    override fun initView() {
        with(binding.timetableRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = timetableAdapter
            addItemDecoration(DividerItemDecoration(context))
        }

        with(binding) {
            timetableSwipe.setOnRefreshListener(presenter::onSwipeRefresh)
            timetableSwipe.setColorSchemeColors(requireContext().getThemeAttrColor(R.attr.colorPrimary))
            timetableSwipe.setProgressBackgroundColorSchemeColor(
                requireContext().getThemeAttrColor(R.attr.colorSwipeRefresh)
            )
            timetableErrorRetry.setOnClickListener { presenter.onRetry() }
            timetableErrorDetails.setOnClickListener { presenter.onDetailsClick() }

            timetablePreviousButton.setOnClickListener { presenter.onPreviousDay() }
            timetableNavDate.setOnClickListener { presenter.onPickDate() }
            timetableNextButton.setOnClickListener { presenter.onNextDay() }

            timetableNavContainer.elevation = requireContext().dpToPx(3f)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.action_menu_timetable, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.timetableMenuAdditionalLessons -> presenter.onAdditionalLessonsSwitchSelected()
            R.id.timetableMenuCompletedLessons -> presenter.onCompletedLessonsSwitchSelected()
            else -> false
        }
    }

    override fun updateData(data: List<TimetableItem>) {
        timetableAdapter.submitList(data)
    }

    override fun clearData() {
        timetableAdapter.submitList(listOf())
    }

    override fun updateNavigationDay(date: String) {
        binding.timetableNavDate.text = date
    }

    override fun showRefresh(show: Boolean) {
        binding.timetableSwipe.isRefreshing = show
    }

    override fun resetView() {
        binding.timetableRecycler.smoothScrollToPosition(0)
    }

    override fun onFragmentReselected() {
        if (::presenter.isInitialized) presenter.onViewReselected()
    }

    override fun popView() {
        (activity as? MainActivity)?.popView()
    }

    override fun showEmpty(show: Boolean) {
        binding.timetableEmpty.visibility = if (show) VISIBLE else GONE
    }

    override fun setDayHeaderMessage(message: String?) {
        binding.timetableEmptyMessage.visibility = if (message.isNullOrEmpty()) GONE else VISIBLE
        binding.timetableEmptyMessage.text = message.orEmpty().parseAsHtml()
    }

    override fun showErrorView(show: Boolean) {
        binding.timetableError.visibility = if (show) VISIBLE else GONE
    }

    override fun setErrorDetails(message: String) {
        binding.timetableErrorMessage.text = message
    }

    override fun showProgress(show: Boolean) {
        binding.timetableProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun enableSwipe(enable: Boolean) {
        binding.timetableSwipe.isEnabled = enable
    }

    override fun showContent(show: Boolean) {
        binding.timetableRecycler.visibility = if (show) VISIBLE else GONE
    }

    override fun showPreButton(show: Boolean) {
        binding.timetablePreviousButton.visibility = if (show) VISIBLE else View.INVISIBLE
    }

    override fun showNextButton(show: Boolean) {
        binding.timetableNextButton.visibility = if (show) VISIBLE else View.INVISIBLE
    }

    override fun showTimetableDialog(lesson: Timetable) {
        (activity as? MainActivity)?.showDialogFragment(TimetableDialog.newInstance(lesson))
    }

    override fun showDatePickerDialog(selectedDate: LocalDate) {
        openMaterialDatePicker(
            selected = selectedDate,
            rangeStart = selectedDate.firstSchoolDayInSchoolYear,
            rangeEnd = LocalDate.now().lastSchoolDayInSchoolYear,
            onDateSelected = {
                presenter.onDateSet(it.year, it.monthValue, it.dayOfMonth)
            }
        )
    }

    override fun openAdditionalLessonsView() {
        (activity as? MainActivity)?.pushView(AdditionalLessonsFragment.newInstance())
    }

    override fun openCompletedLessonsView() {
        (activity as? MainActivity)?.pushView(CompletedLessonsFragment.newInstance())
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
