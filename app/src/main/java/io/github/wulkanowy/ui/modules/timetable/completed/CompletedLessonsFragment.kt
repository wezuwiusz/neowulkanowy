package io.github.wulkanowy.ui.modules.timetable.completed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.CompletedLesson
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.widgets.DividerItemDecoration
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.SchooldaysRangeLimiter
import io.github.wulkanowy.utils.dpToPx
import io.github.wulkanowy.utils.getCompatDrawable
import kotlinx.android.synthetic.main.fragment_timetable_completed.*
import org.threeten.bp.LocalDate
import javax.inject.Inject

class CompletedLessonsFragment : BaseFragment(), CompletedLessonsView, MainView.TitledView {

    @Inject
    lateinit var presenter: CompletedLessonsPresenter

    @Inject
    lateinit var completedLessonsAdapter: CompletedLessonsAdapter

    companion object {
        private const val SAVED_DATE_KEY = "CURRENT_DATE"

        fun newInstance() = CompletedLessonsFragment()
    }

    override val titleStringId get() = R.string.completed_lessons_title

    override val isViewEmpty get() = completedLessonsAdapter.items.isEmpty()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_timetable_completed, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        messageContainer = completedLessonsRecycler
        presenter.onAttachView(this, savedInstanceState?.getLong(SAVED_DATE_KEY))
    }

    override fun initView() {
        completedLessonsAdapter.onClickListener = presenter::onCompletedLessonsItemSelected

        with(completedLessonsRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = completedLessonsAdapter
            addItemDecoration(DividerItemDecoration(context))
        }

        completedLessonsSwipe.setOnRefreshListener(presenter::onSwipeRefresh)
        completedLessonErrorRetry.setOnClickListener { presenter.onRetry() }
        completedLessonErrorDetails.setOnClickListener { presenter.onDetailsClick() }

        completedLessonsPreviousButton.setOnClickListener { presenter.onPreviousDay() }
        completedLessonsNavDate.setOnClickListener { presenter.onPickDate() }
        completedLessonsNextButton.setOnClickListener { presenter.onNextDay() }

        completedLessonsNavContainer.setElevationCompat(requireContext().dpToPx(8f))
    }

    override fun updateData(data: List<CompletedLesson>) {
        with(completedLessonsAdapter) {
            items = data
            notifyDataSetChanged()
        }
    }

    override fun clearData() {
        with(completedLessonsAdapter) {
            items = emptyList()
            notifyDataSetChanged()
        }
    }

    override fun updateNavigationDay(date: String) {
        completedLessonsNavDate.text = date
    }

    override fun hideRefresh() {
        completedLessonsSwipe.isRefreshing = false
    }

    override fun showEmpty(show: Boolean) {
        completedLessonsEmpty.visibility = if (show) VISIBLE else GONE
    }

    override fun showErrorView(show: Boolean) {
        completedLessonError.visibility = if (show) VISIBLE else GONE
    }

    override fun setErrorDetails(message: String) {
        completedLessonErrorMessage.text = message
    }

    override fun showFeatureDisabled() {
        completedLessonsInfo.text = getString(R.string.error_feature_disabled)
        completedLessonsInfoImage.setImageDrawable(requireContext().getCompatDrawable(R.drawable.ic_all_close_circle))
    }

    override fun showProgress(show: Boolean) {
        completedLessonsProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun enableSwipe(enable: Boolean) {
        completedLessonsSwipe.isEnabled = enable
    }

    override fun showContent(show: Boolean) {
        completedLessonsRecycler.visibility = if (show) VISIBLE else GONE
    }

    override fun showPreButton(show: Boolean) {
        completedLessonsPreviousButton.visibility = if (show) VISIBLE else INVISIBLE
    }

    override fun showNextButton(show: Boolean) {
        completedLessonsNextButton.visibility = if (show) VISIBLE else INVISIBLE
    }

    override fun showCompletedLessonDialog(completedLesson: CompletedLesson) {
        (activity as? MainActivity)?.showDialogFragment(CompletedLessonDialog.newInstance(completedLesson))
    }

    override fun showDatePickerDialog(currentDate: LocalDate) {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            presenter.onDateSet(year, month + 1, dayOfMonth)
        }
        val datePickerDialog = DatePickerDialog.newInstance(dateSetListener,
            currentDate.year, currentDate.monthValue - 1, currentDate.dayOfMonth)

        with(datePickerDialog) {
            setDateRangeLimiter(SchooldaysRangeLimiter())
            version = DatePickerDialog.Version.VERSION_2
            scrollOrientation = DatePickerDialog.ScrollOrientation.VERTICAL
            show(this@CompletedLessonsFragment.parentFragmentManager, null)
        }
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
