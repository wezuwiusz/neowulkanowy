package io.github.wulkanowy.ui.modules.exam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.widgets.DividerItemDecoration
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.dpToPx
import kotlinx.android.synthetic.main.fragment_exam.*
import javax.inject.Inject

class ExamFragment : BaseFragment(), ExamView, MainView.MainChildView, MainView.TitledView {

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_exam, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        messageContainer = examRecycler
        presenter.onAttachView(this, savedInstanceState?.getLong(SAVED_DATE_KEY))
    }

    override fun initView() {
        examAdapter.onClickListener = presenter::onExamItemSelected

        with(examRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = examAdapter
            addItemDecoration(DividerItemDecoration(context))
        }

        examSwipe.setOnRefreshListener(presenter::onSwipeRefresh)
        examErrorRetry.setOnClickListener { presenter.onRetry() }
        examErrorDetails.setOnClickListener { presenter.onDetailsClick() }

        examPreviousButton.setOnClickListener { presenter.onPreviousWeek() }
        examNextButton.setOnClickListener { presenter.onNextWeek() }

        examNavContainer.setElevationCompat(requireContext().dpToPx(8f))
    }

    override fun hideRefresh() {
        examSwipe.isRefreshing = false
    }

    override fun updateData(data: List<ExamItem<*>>) {
        with(examAdapter) {
            items = data
            notifyDataSetChanged()
        }
    }

    override fun updateNavigationWeek(date: String) {
        examNavDate.text = date
    }

    override fun clearData() {
        with(examAdapter) {
            items = emptyList()
            notifyDataSetChanged()
        }
    }

    override fun resetView() {
        examRecycler.scrollToPosition(0)
    }

    override fun onFragmentReselected() {
        if (::presenter.isInitialized) presenter.onViewReselected()
    }

    override fun showEmpty(show: Boolean) {
        examEmpty.visibility = if (show) VISIBLE else GONE
    }

    override fun showErrorView(show: Boolean) {
        examError.visibility = if (show) VISIBLE else GONE
    }

    override fun setErrorDetails(message: String) {
        examErrorMessage.text = message
    }

    override fun showProgress(show: Boolean) {
        examProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun enableSwipe(enable: Boolean) {
        examSwipe.isEnabled = enable
    }

    override fun showContent(show: Boolean) {
        examRecycler.visibility = if (show) VISIBLE else GONE
    }

    override fun showPreButton(show: Boolean) {
        examPreviousButton.visibility = if (show) VISIBLE else INVISIBLE
    }

    override fun showNextButton(show: Boolean) {
        examNextButton.visibility = if (show) VISIBLE else INVISIBLE
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
