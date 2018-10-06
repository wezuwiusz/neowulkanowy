package io.github.wulkanowy.ui.main.exam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.utils.setOnItemClickListener
import kotlinx.android.synthetic.main.fragment_exam.*
import javax.inject.Inject

class ExamFragment : BaseFragment(), ExamView {

    @Inject
    lateinit var presenter: ExamPresenter

    @Inject
    lateinit var examAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    companion object {
        private const val SAVED_DATE_KEY = "CURRENT_DATE"
        fun newInstance() = ExamFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_exam, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.run {
            attachView(this@ExamFragment)
            loadData(date = savedInstanceState?.getLong(SAVED_DATE_KEY))
        }
    }

    override fun initView() {
        examAdapter.run {
            setOnItemClickListener { presenter.onExamItemSelected(getItem(it)) }
        }
        examRecycler.run {
            layoutManager = SmoothScrollLinearLayoutManager(context)
            adapter = examAdapter
        }
        examSwipe.setOnRefreshListener { presenter.loadData(date = null, forceRefresh = true) }
        examPreviousButton.setOnClickListener { presenter.loadExamsForPreviousWeek() }
        examNextButton.setOnClickListener { presenter.loadExamsForNextWeek()}
    }

    override fun updateData(data: List<ExamItem>) {
        examAdapter.updateDataSet(data, true)
    }

    override fun updateNavigationWeek(date: String) {
        examNavDate.text = date
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

    override fun showRefresh(show: Boolean) {
        examSwipe.isRefreshing = show
    }

    override fun showPreButton(show: Boolean) {
        examPreviousButton.visibility = if (show) VISIBLE else INVISIBLE
    }

    override fun showNextButton(show: Boolean) {
        examNextButton.visibility = if (show) VISIBLE else INVISIBLE
    }

    override fun showExamDialog(exam: Exam) {
        ExamDialog.newInstance(exam).show(fragmentManager, exam.toString())
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(SAVED_DATE_KEY, presenter.currentDate.toEpochDay())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
    }
}
