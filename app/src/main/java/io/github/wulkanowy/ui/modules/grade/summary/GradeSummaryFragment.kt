package io.github.wulkanowy.ui.modules.grade.summary

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.databinding.FragmentGradeSummaryBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.grade.GradeFragment
import io.github.wulkanowy.ui.modules.grade.GradeView
import io.github.wulkanowy.utils.getThemeAttrColor
import javax.inject.Inject

@AndroidEntryPoint
class GradeSummaryFragment :
    BaseFragment<FragmentGradeSummaryBinding>(R.layout.fragment_grade_summary), GradeSummaryView,
    GradeView.GradeChildView {

    @Inject
    lateinit var presenter: GradeSummaryPresenter

    @Inject
    lateinit var gradeSummaryAdapter: GradeSummaryAdapter

    companion object {
        fun newInstance() = GradeSummaryFragment()
    }

    override val isViewEmpty
        get() = gradeSummaryAdapter.items.isEmpty()

    override val predictedString
        get() = getString(R.string.grade_summary_predicted_grade)

    override val finalString
        get() = getString(R.string.grade_summary_final_grade)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentGradeSummaryBinding.bind(view)
        messageContainer = binding.gradeSummaryRecycler
        presenter.onAttachView(this)
    }

    override fun initView() {
        with(gradeSummaryAdapter) {
            onCalculatedHelpClickListener = presenter::onCalculatedAverageHelpClick
            onFinalHelpClickListener = presenter::onFinalAverageHelpClick
        }

        with(binding.gradeSummaryRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = gradeSummaryAdapter
        }
        with(binding) {
            gradeSummarySwipe.setOnRefreshListener(presenter::onSwipeRefresh)
            gradeSummarySwipe.setColorSchemeColors(requireContext().getThemeAttrColor(R.attr.colorPrimary))
            gradeSummarySwipe.setProgressBackgroundColorSchemeColor(
                requireContext().getThemeAttrColor(
                    R.attr.colorSwipeRefresh
                )
            )
            gradeSummaryErrorRetry.setOnClickListener { presenter.onRetry() }
            gradeSummaryErrorDetails.setOnClickListener { presenter.onDetailsClick() }
        }
    }

    override fun updateData(data: List<GradeSummaryItem>) {
        with(gradeSummaryAdapter) {
            items = data
            notifyDataSetChanged()
        }
    }

    override fun clearView() {
        with(gradeSummaryAdapter) {
            items = emptyList()
            notifyDataSetChanged()
        }
    }

    override fun resetView() {
        binding.gradeSummaryRecycler.scrollToPosition(0)
    }

    override fun showContent(show: Boolean) {
        binding.gradeSummaryRecycler.visibility = if (show) VISIBLE else INVISIBLE
    }

    override fun showEmpty(show: Boolean) {
        binding.gradeSummaryEmpty.visibility = if (show) VISIBLE else INVISIBLE
    }

    override fun showErrorView(show: Boolean) {
        binding.gradeSummaryError.visibility = if (show) VISIBLE else INVISIBLE
    }

    override fun setErrorDetails(message: String) {
        binding.gradeSummaryErrorMessage.text = message
    }

    override fun showProgress(show: Boolean) {
        binding.gradeSummaryProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun enableSwipe(enable: Boolean) {
        binding.gradeSummarySwipe.isEnabled = enable
    }

    override fun showRefresh(show: Boolean) {
        binding.gradeSummarySwipe.isRefreshing = show
    }

    override fun showCalculatedAverageHelpDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.grade_summary_calculated_average_help_dialog_title)
            .setMessage(R.string.grade_summary_calculated_average_help_dialog_message)
            .setPositiveButton(R.string.all_close) { _, _ -> }
            .show()
    }

    override fun showFinalAverageHelpDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.grade_summary_final_average_help_dialog_title)
            .setMessage(R.string.grade_summary_final_average_help_dialog_message)
            .setPositiveButton(R.string.all_close) { _, _ -> }
            .show()
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

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
