package io.github.wulkanowy.ui.modules.attendance.summary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.dpToPx
import io.github.wulkanowy.utils.setOnItemSelectedListener
import kotlinx.android.synthetic.main.fragment_attendance_summary.*
import javax.inject.Inject

class AttendanceSummaryFragment : BaseFragment(), AttendanceSummaryView, MainView.TitledView {

    @Inject
    lateinit var presenter: AttendanceSummaryPresenter

    @Inject
    lateinit var attendanceSummaryAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    private lateinit var subjectsAdapter: ArrayAdapter<String>

    companion object {
        private const val SAVED_SUBJECT_KEY = "CURRENT_SUBJECT"

        fun newInstance() = AttendanceSummaryFragment()
    }

    override val titleStringId get() = R.string.attendance_title

    override val isViewEmpty get() = attendanceSummaryAdapter.isEmpty

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_attendance_summary, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        messageContainer = attendanceSummaryRecycler
        presenter.onAttachView(this, savedInstanceState?.getInt(SAVED_SUBJECT_KEY))
    }

    override fun initView() {
        with(attendanceSummaryRecycler) {
            layoutManager = SmoothScrollLinearLayoutManager(context)
            adapter = attendanceSummaryAdapter
        }

        attendanceSummarySwipe.setOnRefreshListener(presenter::onSwipeRefresh)

        subjectsAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mutableListOf())
        subjectsAdapter.setDropDownViewResource(R.layout.item_attendance_summary_subject)

        with(attendanceSummarySubjects) {
            adapter = subjectsAdapter
            setOnItemSelectedListener<TextView> { presenter.onSubjectSelected(it?.text?.toString()) }
        }

        attendanceSummarySubjectsContainer.setElevationCompat(requireContext().dpToPx(1f))
    }

    override fun updateSubjects(data: ArrayList<String>) {
        with(subjectsAdapter) {
            clear()
            addAll(data)
            notifyDataSetChanged()
        }
    }

    override fun updateDataSet(data: List<AttendanceSummaryItem>, header: AttendanceSummaryScrollableHeader) {
        with(attendanceSummaryAdapter) {
            updateDataSet(data, true)
            removeAllScrollableFooters()
            addScrollableHeader(header)
        }
    }

    override fun clearView() {
        attendanceSummaryAdapter.clear()
    }

    override fun showEmpty(show: Boolean) {
        attendanceSummaryEmpty.visibility = if (show) VISIBLE else GONE
    }

    override fun showProgress(show: Boolean) {
        attendanceSummaryProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun enableSwipe(enable: Boolean) {
        attendanceSummarySwipe.isEnabled = enable
    }

    override fun showContent(show: Boolean) {
        attendanceSummaryRecycler.visibility = if (show) VISIBLE else GONE
    }

    override fun showSubjects(show: Boolean) {
        attendanceSummarySubjectsContainer.visibility = if (show) VISIBLE else INVISIBLE
    }

    override fun hideRefresh() {
        attendanceSummarySwipe.isRefreshing = false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SAVED_SUBJECT_KEY, presenter.currentSubjectId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDetachView()
    }
}
