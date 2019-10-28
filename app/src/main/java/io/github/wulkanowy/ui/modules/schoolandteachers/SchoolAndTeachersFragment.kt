package io.github.wulkanowy.ui.modules.schoolandteachers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.base.BaseFragmentPagerAdapter
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.modules.schoolandteachers.school.SchoolFragment
import io.github.wulkanowy.ui.modules.schoolandteachers.teacher.TeacherFragment
import io.github.wulkanowy.utils.dpToPx
import io.github.wulkanowy.utils.setOnSelectPageListener
import kotlinx.android.synthetic.main.fragment_schoolandteachers.*
import javax.inject.Inject

class SchoolAndTeachersFragment : BaseFragment(), SchoolAndTeachersView, MainView.TitledView {

    @Inject
    lateinit var presenter: SchoolAndTeachersPresenter

    @Inject
    lateinit var pagerAdapter: BaseFragmentPagerAdapter

    companion object {
        fun newInstance() = SchoolAndTeachersFragment()
    }

    override val titleStringId: Int get() = R.string.schoolandteachers_title

    override val currentPageIndex get() = schoolandteachersViewPager.currentItem

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_schoolandteachers, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun initView() {
        with(pagerAdapter) {
            containerId = schoolandteachersViewPager.id
            addFragmentsWithTitle(mapOf(
                SchoolFragment.newInstance() to getString(R.string.school_title),
                TeacherFragment.newInstance() to getString(R.string.teachers_title)
            ))
        }

        with(schoolandteachersViewPager) {
            adapter = pagerAdapter
            offscreenPageLimit = 2
            setOnSelectPageListener(presenter::onPageSelected)
        }

        with(schoolandteachersTabLayout) {
            setupWithViewPager(schoolandteachersViewPager)
            setElevationCompat(context.dpToPx(4f))
        }
    }

    override fun showContent(show: Boolean) {
        schoolandteachersViewPager.visibility = if (show) VISIBLE else INVISIBLE
        schoolandteachersTabLayout.visibility = if (show) VISIBLE else INVISIBLE
    }

    override fun showProgress(show: Boolean) {
        schoolandteachersProgress.visibility = if (show) VISIBLE else INVISIBLE
    }

    fun onChildFragmentLoaded() {
        presenter.onChildViewLoaded()
    }

    override fun notifyChildLoadData(index: Int, forceRefresh: Boolean) {
        (pagerAdapter.getFragmentInstance(index) as? SchoolAndTeachersChildView)?.onParentLoadData(forceRefresh)
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
