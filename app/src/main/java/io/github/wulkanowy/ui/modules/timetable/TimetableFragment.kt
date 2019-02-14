package io.github.wulkanowy.ui.modules.timetable

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.ui.base.session.BaseSessionFragment
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.modules.timetable.completed.CompletedLessonsFragment
import io.github.wulkanowy.utils.setOnItemClickListener
import kotlinx.android.synthetic.main.fragment_timetable.*
import javax.inject.Inject

class TimetableFragment : BaseSessionFragment(), TimetableView, MainView.MainChildView, MainView.TitledView {

    @Inject
    lateinit var presenter: TimetablePresenter

    @Inject
    lateinit var timetableAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    companion object {
        private const val SAVED_DATE_KEY = "CURRENT_DATE"

        fun newInstance() = TimetableFragment()
    }

    override val titleStringId: Int
        get() = R.string.timetable_title

    override val roomString: String
        get() = getString(R.string.timetable_room)

    override val isViewEmpty: Boolean
        get() = timetableAdapter.isEmpty

    override val currentStackSize: Int?
        get() = (activity as? MainActivity)?.currentStackSize

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_timetable, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        messageContainer = timetableRecycler
        presenter.onAttachView(this, savedInstanceState?.getLong(SAVED_DATE_KEY))
    }

    override fun initView() {
        timetableAdapter.run {
            setOnItemClickListener { presenter.onTimetableItemSelected(it) }
        }

        timetableRecycler.run {
            layoutManager = SmoothScrollLinearLayoutManager(context)
            adapter = timetableAdapter
            addItemDecoration(FlexibleItemDecoration(context)
                .withDefaultDivider()
                .withDrawDividerOnLastItem(false)
            )
        }
        timetableSwipe.setOnRefreshListener { presenter.onSwipeRefresh() }
        timetablePreviousButton.setOnClickListener { presenter.onPreviousDay() }
        timetableNextButton.setOnClickListener { presenter.onNextDay() }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.action_menu_timetable, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == R.id.timetableMenuCompletedLessons) presenter.onCompletedLessonsSwitchSelected()
        else false
    }

    override fun updateData(data: List<TimetableItem>) {
        timetableAdapter.updateDataSet(data, true)
    }

    override fun clearData() {
        timetableAdapter.clear()
    }

    override fun updateNavigationDay(date: String) {
        timetableNavDate.text = date
    }

    override fun hideRefresh() {
        timetableSwipe.isRefreshing = false
    }

    override fun resetView() {
        timetableRecycler.smoothScrollToPosition(0)
    }

    override fun onFragmentReselected() {
        presenter.onViewReselected()
    }

    override fun popView() {
        (activity as? MainActivity)?.popView()
    }

    override fun showEmpty(show: Boolean) {
        timetableEmpty.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showProgress(show: Boolean) {
        timetableProgress.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showContent(show: Boolean) {
        timetableRecycler.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showPreButton(show: Boolean) {
        timetablePreviousButton.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    override fun showNextButton(show: Boolean) {
        timetableNextButton.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    override fun showTimetableDialog(lesson: Timetable) {
        (activity as? MainActivity)?.showDialogFragment(TimetableDialog.newInstance(lesson))
    }

    override fun openCompletedLessonsView() {
        (activity as? MainActivity)?.pushView(CompletedLessonsFragment.newInstance())
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
