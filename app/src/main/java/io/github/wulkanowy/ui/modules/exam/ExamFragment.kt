package io.github.wulkanowy.ui.modules.exam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.ui.base.session.BaseSessionFragment
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.setOnItemClickListener
import kotlinx.android.synthetic.main.fragment_exam.*
import javax.inject.Inject

class ExamFragment : BaseSessionFragment(), ExamView, MainView.MainChildView, MainView.TitledView {

    @Inject
    lateinit var presenter: ExamPresenter

    @Inject
    lateinit var examAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    companion object {
        private const val SAVED_DATE_KEY = "CURRENT_DATE"

        fun newInstance() = ExamFragment()
    }

    override val titleStringId: Int
        get() = R.string.exam_title

    override val isViewEmpty: Boolean
        get() = examAdapter.isEmpty

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_exam, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        messageContainer = examRecycler
        presenter.onAttachView(this, savedInstanceState?.getLong(SAVED_DATE_KEY))
    }

    override fun initView() {
        examAdapter.run {
            setOnItemClickListener { presenter.onExamItemSelected(it) }
        }
        examRecycler.run {
            layoutManager = SmoothScrollLinearLayoutManager(context)
            adapter = examAdapter
            addItemDecoration(FlexibleItemDecoration(context)
                .withDefaultDivider(R.layout.item_exam)
                .withDrawDividerOnLastItem(false)
            )
        }
        examSwipe.setOnRefreshListener { presenter.onSwipeRefresh() }
        examPreviousButton.setOnClickListener { presenter.onPreviousWeek() }
        examNextButton.setOnClickListener { presenter.onNextWeek() }
    }

    override fun hideRefresh() {
        examSwipe.isRefreshing = false
    }

    override fun updateData(data: List<ExamItem>) {
        examAdapter.updateDataSet(data, true)
    }

    override fun updateNavigationWeek(date: String) {
        examNavDate.text = date
    }

    override fun clearData() {
        examAdapter.clear()
    }

    override fun resetView() {
        examRecycler.scrollToPosition(0)
    }

    override fun onFragmentReselected() {
        presenter.onViewReselected()
    }

    override fun showEmpty(show: Boolean) {
        examEmpty.visibility = if (show) VISIBLE else GONE
    }

    override fun showProgress(show: Boolean) {
        examProgress.visibility = if (show) VISIBLE else GONE
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
