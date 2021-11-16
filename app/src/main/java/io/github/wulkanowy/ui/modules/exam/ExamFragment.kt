package io.github.wulkanowy.ui.modules.exam

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.databinding.FragmentExamBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.widgets.DividerItemDecoration
import io.github.wulkanowy.utils.dpToPx
import io.github.wulkanowy.utils.getThemeAttrColor
import javax.inject.Inject

@AndroidEntryPoint
class ExamFragment : BaseFragment<FragmentExamBinding>(R.layout.fragment_exam), ExamView,
    MainView.TitledView {

    @Inject
    lateinit var presenter: ExamPresenter

    @Inject
    lateinit var examAdapter: ExamAdapter

    companion object {
        private const val SAVED_DATE_KEY = "CURRENT_DATE"

        fun newInstance() = ExamFragment()
    }

    override val titleStringId get() = R.string.exam_title

    override val isViewEmpty get() = examAdapter.items.isEmpty()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentExamBinding.bind(view)
        messageContainer = binding.examRecycler
        presenter.onAttachView(this, savedInstanceState?.getLong(SAVED_DATE_KEY))
    }

    override fun initView() {
        examAdapter.onClickListener = presenter::onExamItemSelected

        with(binding.examRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = examAdapter
            addItemDecoration(DividerItemDecoration(context))
        }

        with(binding) {
            examSwipe.setOnRefreshListener(presenter::onSwipeRefresh)
            examSwipe.setColorSchemeColors(requireContext().getThemeAttrColor(R.attr.colorPrimary))
            examSwipe.setProgressBackgroundColorSchemeColor(requireContext().getThemeAttrColor(R.attr.colorSwipeRefresh))
            examErrorRetry.setOnClickListener { presenter.onRetry() }
            examErrorDetails.setOnClickListener { presenter.onDetailsClick() }

            examPreviousButton.setOnClickListener { presenter.onPreviousWeek() }
            examNextButton.setOnClickListener { presenter.onNextWeek() }

            examNavContainer.elevation = requireContext().dpToPx(8f)
        }
    }

    override fun showRefresh(show: Boolean) {
        binding.examSwipe.isRefreshing = show
    }

    override fun updateData(data: List<ExamItem<*>>) {
        with(examAdapter) {
            items = data
            notifyDataSetChanged()
        }
    }

    override fun updateNavigationWeek(date: String) {
        binding.examNavDate.text = date
    }

    override fun clearData() {
        with(examAdapter) {
            items = emptyList()
            notifyDataSetChanged()
        }
    }

    override fun showEmpty(show: Boolean) {
        binding.examEmpty.visibility = if (show) VISIBLE else GONE
    }

    override fun showErrorView(show: Boolean) {
        binding.examError.visibility = if (show) VISIBLE else GONE
    }

    override fun setErrorDetails(message: String) {
        binding.examErrorMessage.text = message
    }

    override fun showProgress(show: Boolean) {
        binding.examProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun enableSwipe(enable: Boolean) {
        binding.examSwipe.isEnabled = enable
    }

    override fun showContent(show: Boolean) {
        binding.examRecycler.visibility = if (show) VISIBLE else GONE
    }

    override fun showPreButton(show: Boolean) {
        binding.examPreviousButton.visibility = if (show) VISIBLE else INVISIBLE
    }

    override fun showNextButton(show: Boolean) {
        binding.examNextButton.visibility = if (show) VISIBLE else INVISIBLE
    }

    override fun showExamDialog(exam: Exam) {
        (activity as? MainActivity)?.showDialogFragment(ExamDialog.newInstance(exam))
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
