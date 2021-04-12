package io.github.wulkanowy.ui.modules.luckynumber.history

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.LuckyNumber
import io.github.wulkanowy.databinding.FragmentLuckyNumberHistoryBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.widgets.DividerItemDecoration
import io.github.wulkanowy.utils.SchoolDaysValidator
import io.github.wulkanowy.utils.dpToPx
import io.github.wulkanowy.utils.schoolYearStart
import io.github.wulkanowy.utils.toLocalDateTime
import io.github.wulkanowy.utils.toTimestamp
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class LuckyNumberHistoryFragment :
    BaseFragment<FragmentLuckyNumberHistoryBinding>(R.layout.fragment_lucky_number_history),
    LuckyNumberHistoryView,
    MainView.TitledView {

    @Inject
    lateinit var presenter: LuckyNumberHistoryPresenter

    @Inject
    lateinit var luckyNumberHistoryAdapter: LuckyNumberHistoryAdapter

    companion object {
        fun newInstance() = LuckyNumberHistoryFragment()
    }

    override val titleStringId: Int
        get() = R.string.lucky_number_history_title

    override val isViewEmpty get() = luckyNumberHistoryAdapter.items.isEmpty()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLuckyNumberHistoryBinding.bind(view)
        messageContainer = binding.luckyNumberHistoryRecycler
        presenter.onAttachView(this)
    }

    override fun initView() {
        with(binding.luckyNumberHistoryRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = luckyNumberHistoryAdapter
            addItemDecoration(DividerItemDecoration(context))
        }

        with(binding) {
            luckyNumberHistoryNavDate.setOnClickListener { presenter.onPickDate() }
            luckyNumberHistoryErrorRetry.setOnClickListener { presenter.onRetry() }
            luckyNumberHistoryErrorDetails.setOnClickListener { presenter.onDetailsClick() }

            luckyNumberHistoryPreviousButton.setOnClickListener { presenter.onPreviousWeek() }
            luckyNumberHistoryNextButton.setOnClickListener { presenter.onNextWeek() }

            luckyNumberHistoryNavContainer.setElevationCompat(requireContext().dpToPx(8f))
        }
    }

    override fun updateData(data: List<LuckyNumber>) {
        with(luckyNumberHistoryAdapter) {
            items = data
            notifyDataSetChanged()
        }
    }

    override fun clearData() {
        with(luckyNumberHistoryAdapter) {
            items = emptyList()
            notifyDataSetChanged()
        }
    }

    override fun showEmpty(show: Boolean) {
        binding.luckyNumberHistoryEmpty.visibility = if (show) VISIBLE else GONE
    }

    override fun showErrorView(show: Boolean) {
        binding.luckyNumberHistoryError.visibility = if (show) VISIBLE else GONE
    }

    override fun setErrorDetails(message: String) {
        binding.luckyNumberHistoryErrorMessage.text = message
    }

    override fun updateNavigationWeek(date: String) {
        binding.luckyNumberHistoryNavDate.text = date
    }

    override fun showProgress(show: Boolean) {
        binding.luckyNumberHistoryProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun showPreButton(show: Boolean) {
        binding.luckyNumberHistoryPreviousButton.visibility = if (show) VISIBLE else View.INVISIBLE
    }

    override fun showNextButton(show: Boolean) {
        binding.luckyNumberHistoryNextButton.visibility = if (show) VISIBLE else View.INVISIBLE
    }

    override fun showDatePickerDialog(currentDate: LocalDate) {
        val now = LocalDate.now()
        val startOfSchoolYear = now.schoolYearStart.toTimestamp()

        val constraintsBuilder = CalendarConstraints.Builder().apply {
            setValidator(SchoolDaysValidator(startOfSchoolYear, now.toTimestamp()))
            setStart(startOfSchoolYear)
            setEnd(now.toTimestamp())
        }
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setCalendarConstraints(constraintsBuilder.build())
                .setSelection(currentDate.toTimestamp())
                .build()

        datePicker.addOnPositiveButtonClickListener {
            val date = it.toLocalDateTime()
            presenter.onDateSet(date.year, date.monthValue, date.dayOfMonth)
        }

        datePicker.show(this@LuckyNumberHistoryFragment.parentFragmentManager, null)
    }

    override fun showContent(show: Boolean) {
        binding.luckyNumberHistoryRecycler.visibility = if (show) VISIBLE else GONE
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
