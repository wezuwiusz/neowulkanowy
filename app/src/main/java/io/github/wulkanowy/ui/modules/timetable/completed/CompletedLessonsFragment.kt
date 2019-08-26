package io.github.wulkanowy.ui.modules.timetable.completed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.CompletedLesson
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.getCompatDrawable
import io.github.wulkanowy.utils.setOnItemClickListener
import kotlinx.android.synthetic.main.fragment_timetable_completed.*
import javax.inject.Inject

class CompletedLessonsFragment : BaseFragment(), CompletedLessonsView, MainView.TitledView {

    @Inject
    lateinit var presenter: CompletedLessonsPresenter

    @Inject
    lateinit var completedLessonsAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    companion object {
        private const val SAVED_DATE_KEY = "CURRENT_DATE"

        fun newInstance() = CompletedLessonsFragment()
    }

    override val titleStringId: Int
        get() = R.string.completed_lessons_title

    override val isViewEmpty
        get() = completedLessonsAdapter.isEmpty

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_timetable_completed, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        messageContainer = completedLessonsRecycler
        presenter.onAttachView(this, savedInstanceState?.getLong(SAVED_DATE_KEY))
    }

    override fun initView() {
        completedLessonsAdapter.run {
            setOnItemClickListener { presenter.onCompletedLessonsItemSelected(it) }
        }

        completedLessonsRecycler.run {
            layoutManager = SmoothScrollLinearLayoutManager(context)
            adapter = completedLessonsAdapter
        }
        completedLessonsSwipe.setOnRefreshListener { presenter.onSwipeRefresh() }
        completedLessonsPreviousButton.setOnClickListener { presenter.onPreviousDay() }
        completedLessonsNextButton.setOnClickListener { presenter.onNextDay() }
    }

    override fun updateData(data: List<CompletedLessonItem>) {
        completedLessonsAdapter.updateDataSet(data, true)
    }

    override fun clearData() {
        completedLessonsAdapter.clear()
    }

    override fun updateNavigationDay(date: String) {
        completedLessonsNavDate.text = date
    }

    override fun hideRefresh() {
        completedLessonsSwipe.isRefreshing = false
    }

    override fun showEmpty(show: Boolean) {
        completedLessonsEmpty.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showFeatureDisabled() {
        context?.let {
            completedLessonsInfo.text = getString(R.string.error_feature_disabled)
            completedLessonsInfoImage.setImageDrawable(it.getCompatDrawable(R.drawable.ic_all_close_circle))
        }
    }

    override fun showProgress(show: Boolean) {
        completedLessonsProgress.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun enableSwipe(enable: Boolean) {
        completedLessonsSwipe.isEnabled = enable
    }

    override fun showContent(show: Boolean) {
        completedLessonsRecycler.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showPreButton(show: Boolean) {
        completedLessonsPreviousButton.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    override fun showNextButton(show: Boolean) {
        completedLessonsNextButton.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    override fun showCompletedLessonDialog(completedLesson: CompletedLesson) {
        (activity as? MainActivity)?.showDialogFragment(CompletedLessonDialog.newInstance(completedLesson))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(CompletedLessonsFragment.SAVED_DATE_KEY, presenter.currentDate.toEpochDay())
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
