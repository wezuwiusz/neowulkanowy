package io.github.wulkanowy.ui.modules.schoolandteachers.teacher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.FlexibleItemDecoration
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.modules.schoolandteachers.SchoolAndTeachersChildView
import io.github.wulkanowy.ui.modules.schoolandteachers.SchoolAndTeachersFragment
import kotlinx.android.synthetic.main.fragment_teacher.*
import javax.inject.Inject

class TeacherFragment : BaseFragment(), TeacherView, MainView.TitledView, SchoolAndTeachersChildView {

    @Inject
    lateinit var presenter: TeacherPresenter

    @Inject
    lateinit var teacherAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    companion object {
        fun newInstance() = TeacherFragment()
    }

    override val titleStringId: Int
        get() = R.string.teachers_title

    override val noSubjectString get() = getString(R.string.teacher_no_subject)

    override val isViewEmpty: Boolean
        get() = teacherAdapter.isEmpty

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_teacher, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun initView() {
        teacherRecycler.run {
            layoutManager = SmoothScrollLinearLayoutManager(context)
            adapter = teacherAdapter
            addItemDecoration(FlexibleItemDecoration(context)
                .withDefaultDivider()
                .withDrawDividerOnLastItem(false)
            )
        }
        teacherSwipe.setOnRefreshListener { presenter.onSwipeRefresh() }
    }

    override fun updateData(data: List<TeacherItem>) {
        teacherAdapter.updateDataSet(data, true)
    }

    override fun updateItem(item: AbstractFlexibleItem<*>) {
        teacherAdapter.updateItem(item)
    }

    override fun clearData() {
        teacherAdapter.clear()
    }

    override fun showEmpty(show: Boolean) {
        teacherEmpty.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun showProgress(show: Boolean) {
        teacherProgress.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun enableSwipe(enable: Boolean) {
        teacherSwipe.isEnabled = enable
    }

    override fun showContent(show: Boolean) {
        teacherRecycler.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun hideRefresh() {
        teacherSwipe.isRefreshing = false
    }

    override fun notifyParentDataLoaded() {
        (parentFragment as? SchoolAndTeachersFragment)?.onChildFragmentLoaded()
    }

    override fun onParentLoadData(forceRefresh: Boolean) {
        presenter.onParentViewLoadData(forceRefresh)
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
