package io.github.wulkanowy.ui.modules.schoolandteachers.teacher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Teacher
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.widgets.DividerItemDecoration
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.modules.schoolandteachers.SchoolAndTeachersChildView
import io.github.wulkanowy.ui.modules.schoolandteachers.SchoolAndTeachersFragment
import kotlinx.android.synthetic.main.fragment_teacher.*
import javax.inject.Inject

class TeacherFragment : BaseFragment(), TeacherView, MainView.TitledView,
    SchoolAndTeachersChildView {

    @Inject
    lateinit var presenter: TeacherPresenter

    @Inject
    lateinit var teacherAdapter: TeacherAdapter

    companion object {
        fun newInstance() = TeacherFragment()
    }

    override val titleStringId: Int
        get() = R.string.teachers_title

    override val noSubjectString get() = getString(R.string.teacher_no_subject)

    override val isViewEmpty: Boolean
        get() = teacherAdapter.items.isEmpty()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_teacher, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun initView() {
        teacherRecycler.run {
            layoutManager = LinearLayoutManager(context)
            adapter = teacherAdapter
            addItemDecoration(DividerItemDecoration(context))
        }
        teacherSwipe.setOnRefreshListener { presenter.onSwipeRefresh() }
        teacherErrorRetry.setOnClickListener { presenter.onRetry() }
        teacherErrorDetails.setOnClickListener { presenter.onDetailsClick() }
    }

    override fun updateData(data: List<Teacher>) {
        with(teacherAdapter) {
            items = data
            notifyDataSetChanged()
        }
    }

    override fun showEmpty(show: Boolean) {
        teacherEmpty.visibility = if (show) VISIBLE else GONE
    }

    override fun showErrorView(show: Boolean) {
        teacherError.visibility = if (show) VISIBLE else GONE
    }

    override fun setErrorDetails(message: String) {
        teacherErrorMessage.text = message
    }

    override fun showProgress(show: Boolean) {
        teacherProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun enableSwipe(enable: Boolean) {
        teacherSwipe.isEnabled = enable
    }

    override fun showContent(show: Boolean) {
        teacherRecycler.visibility = if (show) VISIBLE else GONE
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
