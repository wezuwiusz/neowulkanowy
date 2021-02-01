package io.github.wulkanowy.ui.modules.schoolandteachers.teacher

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Teacher
import io.github.wulkanowy.databinding.FragmentTeacherBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.modules.schoolandteachers.SchoolAndTeachersChildView
import io.github.wulkanowy.ui.modules.schoolandteachers.SchoolAndTeachersFragment
import io.github.wulkanowy.ui.widgets.DividerItemDecoration
import io.github.wulkanowy.utils.getThemeAttrColor
import javax.inject.Inject

@AndroidEntryPoint
class TeacherFragment : BaseFragment<FragmentTeacherBinding>(R.layout.fragment_teacher),
    TeacherView, MainView.TitledView, SchoolAndTeachersChildView {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTeacherBinding.bind(view)
        presenter.onAttachView(this)
    }

    override fun initView() {
        with(binding.teacherRecycler) {
            layoutManager = LinearLayoutManager(context)
            adapter = teacherAdapter
            addItemDecoration(DividerItemDecoration(context))
        }
        with(binding) {
            teacherSwipe.setOnRefreshListener(presenter::onSwipeRefresh)
            teacherSwipe.setColorSchemeColors(requireContext().getThemeAttrColor(R.attr.colorPrimary))
            teacherSwipe.setProgressBackgroundColorSchemeColor(requireContext().getThemeAttrColor(R.attr.colorSwipeRefresh))
            teacherErrorRetry.setOnClickListener { presenter.onRetry() }
            teacherErrorDetails.setOnClickListener { presenter.onDetailsClick() }
        }
    }

    override fun updateData(data: List<Teacher>) {
        with(teacherAdapter) {
            items = data
            notifyDataSetChanged()
        }
    }

    override fun showEmpty(show: Boolean) {
        binding.teacherEmpty.visibility = if (show) VISIBLE else GONE
    }

    override fun showErrorView(show: Boolean) {
        binding.teacherError.visibility = if (show) VISIBLE else GONE
    }

    override fun setErrorDetails(message: String) {
        binding.teacherErrorMessage.text = message
    }

    override fun showProgress(show: Boolean) {
        binding.teacherProgress.visibility = if (show) VISIBLE else GONE
    }

    override fun enableSwipe(enable: Boolean) {
        binding.teacherSwipe.isEnabled = enable
    }

    override fun showContent(show: Boolean) {
        binding.teacherRecycler.visibility = if (show) VISIBLE else GONE
    }

    override fun hideRefresh() {
        binding.teacherSwipe.isRefreshing = false
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
