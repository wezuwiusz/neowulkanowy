package io.github.wulkanowy.ui.modules.homework

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Homework
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.widgets.DividerItemDecoration
import io.github.wulkanowy.ui.modules.homework.details.HomeworkDetailsDialog
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.dpToPx
import kotlinx.android.synthetic.main.fragment_homework.*
import javax.inject.Inject

class HomeworkFragment : BaseFragment(), HomeworkView, MainView.TitledView {

    @Inject
    lateinit var presenter: HomeworkPresenter

    @Inject
    lateinit var homeworkAdapter: HomeworkAdapter

    companion object {
        private const val SAVED_DATE_KEY = "CURRENT_DATE"

        fun newInstance() = HomeworkFragment()
    }

    override val titleStringId get() = R.string.homework_title

    override val isViewEmpty get() = homeworkAdapter.items.isEmpty()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_homework, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        messageContainer = homeworkRecycler
        presenter.onAttachView(this, savedInstanceState?.getLong(SAVED_DATE_KEY))
    }

    override fun initView() {
        homeworkAdapter.onClickListener = presenter::onHomeworkItemSelected

        with(homeworkRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = homeworkAdapter
            addItemDecoration(DividerItemDecoration(context))
        }

        homeworkSwipe.setOnRefreshListener(presenter::onSwipeRefresh)
        homeworkErrorRetry.setOnClickListener { presenter.onRetry() }
        homeworkErrorDetails.setOnClickListener { presenter.onDetailsClick() }

        homeworkPreviousButton.setOnClickListener { presenter.onPreviousDay() }
        homeworkNextButton.setOnClickListener { presenter.onNextDay() }

        homeworkNavContainer.setElevationCompat(requireContext().dpToPx(8f))
    }

    override fun updateData(data: List<HomeworkItem<*>>) {
        with(homeworkAdapter) {
            items = data
            notifyDataSetChanged()
        }
    }

    fun onReloadList() {
        presenter.reloadData()
    }

    override fun clearData() {
        with(homeworkAdapter) {
            items = emptyList()
            notifyDataSetChanged()
        }
    }

    override fun updateNavigationWeek(date: String) {
        homeworkNavDate.text = date
    }

    override fun hideRefresh() {
        homeworkSwipe.isRefreshing = false
    }

    override fun showEmpty(show: Boolean) {
        homeworkEmpty.visibility = if (show) VISIBLE else GONE
    }

    override fun showErrorView(show: Boolean) {
        homeworkError.visibility = if (show) VISIBLE else GONE
    }

    override fun setErrorDetails(message: String) {
        homeworkErrorMessage.text = message
    }

    override fun showProgress(show: Boolean) {
        homeworkProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun enableSwipe(enable: Boolean) {
        homeworkSwipe.isEnabled = enable
    }

    override fun showContent(show: Boolean) {
        homeworkRecycler.visibility = if (show) VISIBLE else GONE
    }

    override fun showPreButton(show: Boolean) {
        homeworkPreviousButton.visibility = if (show) VISIBLE else View.INVISIBLE
    }

    override fun showNextButton(show: Boolean) {
        homeworkNextButton.visibility = if (show) VISIBLE else View.INVISIBLE
    }

    override fun showTimetableDialog(homework: Homework) {
        (activity as? MainActivity)?.showDialogFragment(HomeworkDetailsDialog.newInstance(homework))
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
