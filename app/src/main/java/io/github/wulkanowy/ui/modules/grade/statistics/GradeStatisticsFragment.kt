package io.github.wulkanowy.ui.modules.grade.statistics

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.pojos.GradeStatisticsItem
import io.github.wulkanowy.databinding.FragmentGradeStatisticsBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.grade.GradeFragment
import io.github.wulkanowy.ui.modules.grade.GradeView
import io.github.wulkanowy.utils.dpToPx
import io.github.wulkanowy.utils.getThemeAttrColor
import io.github.wulkanowy.utils.setOnItemSelectedListener
import javax.inject.Inject

@AndroidEntryPoint
class GradeStatisticsFragment :
    BaseFragment<FragmentGradeStatisticsBinding>(R.layout.fragment_grade_statistics),
    GradeStatisticsView, GradeView.GradeChildView {

    @Inject
    lateinit var presenter: GradeStatisticsPresenter

    @Inject
    lateinit var statisticsAdapter: GradeStatisticsAdapter

    private lateinit var subjectsAdapter: ArrayAdapter<String>

    companion object {
        private const val SAVED_CHART_TYPE = "CURRENT_TYPE"

        fun newInstance() = GradeStatisticsFragment()
    }

    override val isViewEmpty get() = statisticsAdapter.items.isEmpty()

    override val currentType get() = statisticsAdapter.currentDataType

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentGradeStatisticsBinding.bind(view)
        messageContainer = binding.gradeStatisticsSwipe
        presenter.onAttachView(
            this,
            savedInstanceState?.getSerializable(SAVED_CHART_TYPE) as? GradeStatisticsItem.DataType
        )
    }

    override fun initView() {
        statisticsAdapter.onDataTypeChangeListener = presenter::onTypeChange

        with(binding.gradeStatisticsRecycler) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = statisticsAdapter
        }

        subjectsAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mutableListOf())
        subjectsAdapter.setDropDownViewResource(R.layout.item_attendance_summary_subject)

        with(binding.gradeStatisticsSubjects) {
            adapter = subjectsAdapter
            setOnItemSelectedListener<TextView> { presenter.onSubjectSelected(it?.text?.toString()) }
        }

        with(binding) {
            gradeStatisticsSubjectsContainer.elevation = requireContext().dpToPx(1f)

            gradeStatisticsSwipe.setOnRefreshListener(presenter::onSwipeRefresh)
            gradeStatisticsSwipe.setColorSchemeColors(requireContext().getThemeAttrColor(R.attr.colorPrimary))
            gradeStatisticsSwipe.setProgressBackgroundColorSchemeColor(
                requireContext().getThemeAttrColor(R.attr.colorSwipeRefresh)
            )
            gradeStatisticsErrorRetry.setOnClickListener { presenter.onRetry() }
            gradeStatisticsErrorDetails.setOnClickListener { presenter.onDetailsClick() }
        }
    }

    override fun updateSubjects(data: ArrayList<String>) {
        with(subjectsAdapter) {
            clear()
            addAll(data)
            notifyDataSetChanged()
        }
    }

    override fun updateData(
        newItems: List<GradeStatisticsItem>,
        newTheme: String,
        showAllSubjectsOnStatisticsList: Boolean
    ) {
        with(statisticsAdapter) {
            showAllSubjectsOnList = showAllSubjectsOnStatisticsList
            theme = newTheme
            items = newItems
            notifyDataSetChanged()
        }
    }

    override fun showSubjects(show: Boolean) {
        binding.gradeStatisticsSubjectsContainer.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun clearView() {
        statisticsAdapter.items = emptyList()
    }

    override fun resetView() {
        binding.gradeStatisticsRecycler.scrollToPosition(0)
    }

    override fun showEmpty(show: Boolean) {
        binding.gradeStatisticsEmpty.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    override fun showErrorView(show: Boolean) {
        binding.gradeStatisticsError.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun setErrorDetails(message: String) {
        binding.gradeStatisticsErrorMessage.text = message
    }

    override fun showProgress(show: Boolean) {
        binding.gradeStatisticsProgress.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun enableSwipe(enable: Boolean) {
        binding.gradeStatisticsSwipe.isEnabled = enable
    }

    override fun showRefresh(show: Boolean) {
        binding.gradeStatisticsSwipe.isRefreshing = show
    }

    override fun onParentLoadData(semesterId: Int, forceRefresh: Boolean) {
        presenter.onParentViewLoadData(semesterId, forceRefresh)
    }

    override fun onParentReselected() {
        presenter.onParentViewReselected()
    }

    override fun onParentChangeSemester() {
        presenter.onParentViewChangeSemester()
    }

    override fun notifyParentDataLoaded(semesterId: Int) {
        (parentFragment as? GradeFragment)?.onChildFragmentLoaded(semesterId)
    }

    override fun notifyParentRefresh() {
        (parentFragment as? GradeFragment)?.onChildRefresh()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(SAVED_CHART_TYPE, presenter.currentType)
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
