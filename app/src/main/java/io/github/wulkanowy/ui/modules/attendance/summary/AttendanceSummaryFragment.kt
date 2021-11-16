package io.github.wulkanowy.ui.modules.attendance.summary

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.AttendanceSummary
import io.github.wulkanowy.databinding.FragmentAttendanceSummaryBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.dpToPx
import io.github.wulkanowy.utils.getThemeAttrColor
import io.github.wulkanowy.utils.setOnItemSelectedListener
import javax.inject.Inject

@AndroidEntryPoint
class AttendanceSummaryFragment :
    BaseFragment<FragmentAttendanceSummaryBinding>(R.layout.fragment_attendance_summary),
    AttendanceSummaryView, MainView.TitledView {

    @Inject
    lateinit var presenter: AttendanceSummaryPresenter

    @Inject
    lateinit var attendanceSummaryAdapter: AttendanceSummaryAdapter

    private lateinit var subjectsAdapter: ArrayAdapter<String>

    companion object {
        private const val SAVED_SUBJECT_KEY = "CURRENT_SUBJECT"

        fun newInstance() = AttendanceSummaryFragment()
    }

    override val titleStringId get() = R.string.attendance_title

    override val isViewEmpty get() = attendanceSummaryAdapter.items.isEmpty()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAttendanceSummaryBinding.bind(view)
        messageContainer = binding.attendanceSummaryRecycler
        presenter.onAttachView(this, savedInstanceState?.getInt(SAVED_SUBJECT_KEY))
    }

    override fun initView() {
        with(binding.attendanceSummaryRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = attendanceSummaryAdapter
        }

        with(binding) {
            attendanceSummarySwipe.setOnRefreshListener(presenter::onSwipeRefresh)
            attendanceSummarySwipe.setColorSchemeColors(requireContext().getThemeAttrColor(R.attr.colorPrimary))
            attendanceSummarySwipe.setProgressBackgroundColorSchemeColor(requireContext().getThemeAttrColor(R.attr.colorSwipeRefresh))
            attendanceSummaryErrorRetry.setOnClickListener { presenter.onRetry() }
            attendanceSummaryErrorDetails.setOnClickListener { presenter.onDetailsClick() }
        }

        subjectsAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mutableListOf())
        subjectsAdapter.setDropDownViewResource(R.layout.item_attendance_summary_subject)

        with(binding.attendanceSummarySubjects) {
            adapter = subjectsAdapter
            setOnItemSelectedListener<TextView> { presenter.onSubjectSelected(it?.text?.toString()) }
        }

        binding.attendanceSummarySubjectsContainer.elevation = requireContext().dpToPx(1f)
    }

    override fun updateSubjects(data: ArrayList<String>) {
        with(subjectsAdapter) {
            clear()
            addAll(data)
            notifyDataSetChanged()
        }
    }

    override fun updateDataSet(data: List<AttendanceSummary>) {
        with(attendanceSummaryAdapter) {
            items = data
            notifyDataSetChanged()
        }
    }

    override fun clearView() {
        with(attendanceSummaryAdapter) {
            items = emptyList()
            notifyDataSetChanged()
        }
    }

    override fun showEmpty(show: Boolean) {
        binding.attendanceSummaryEmpty.visibility = if (show) VISIBLE else GONE
    }

    override fun showErrorView(show: Boolean) {
        binding.attendanceSummaryError.visibility = if (show) VISIBLE else GONE
    }

    override fun setErrorDetails(message: String) {
        binding.attendanceSummaryErrorMessage.text = message
    }

    override fun showProgress(show: Boolean) {
        binding.attendanceSummaryProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun enableSwipe(enable: Boolean) {
        binding.attendanceSummarySwipe.isEnabled = enable
    }

    override fun showContent(show: Boolean) {
        binding.attendanceSummaryRecycler.visibility = if (show) VISIBLE else GONE
    }

    override fun showSubjects(show: Boolean) {
        binding.attendanceSummarySubjectsContainer.visibility = if (show) VISIBLE else INVISIBLE
    }

    override fun showRefresh(show: Boolean) {
        binding.attendanceSummarySwipe.isRefreshing = show
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SAVED_SUBJECT_KEY, presenter.currentSubjectId)
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
