package io.github.wulkanowy.ui.modules.schoolandteachers

import android.os.Bundle
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.databinding.FragmentSchoolandteachersBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.base.BaseFragmentPagerAdapter
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.ui.modules.schoolandteachers.school.SchoolFragment
import io.github.wulkanowy.ui.modules.schoolandteachers.teacher.TeacherFragment
import io.github.wulkanowy.utils.dpToPx
import io.github.wulkanowy.utils.setOnSelectPageListener
import javax.inject.Inject

@AndroidEntryPoint
class SchoolAndTeachersFragment :
    BaseFragment<FragmentSchoolandteachersBinding>(R.layout.fragment_schoolandteachers),
    SchoolAndTeachersView, MainView.TitledView {

    @Inject
    lateinit var presenter: SchoolAndTeachersPresenter

    private val pagerAdapter by lazy {
        BaseFragmentPagerAdapter(
            fragmentManager = childFragmentManager,
            pagesCount = 2,
            lifecycle = lifecycle,
        )
    }

    companion object {
        fun newInstance() = SchoolAndTeachersFragment()
    }

    override val titleStringId: Int get() = R.string.schoolandteachers_title

    override val currentPageIndex get() = binding.schoolandteachersViewPager.currentItem

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSchoolandteachersBinding.bind(view)
        presenter.onAttachView(this)
    }

    override fun initView() {
        with(binding.schoolandteachersViewPager) {
            adapter = pagerAdapter
            offscreenPageLimit = 2
            setOnSelectPageListener(presenter::onPageSelected)
        }

        with(pagerAdapter) {
            containerId = binding.schoolandteachersViewPager.id
            titleFactory = {
                when (it) {
                    0 -> getString(R.string.school_title)
                    1 -> getString(R.string.teachers_title)
                    else -> throw IllegalStateException()
                }
            }
            itemFactory = {
                when (it) {
                    0 -> SchoolFragment.newInstance()
                    1 -> TeacherFragment.newInstance()
                    else -> throw IllegalStateException()
                }
            }
            TabLayoutMediator(
                binding.schoolandteachersTabLayout,
                binding.schoolandteachersViewPager,
                this
            ).attach()
        }

        binding.schoolandteachersTabLayout.elevation = requireContext().dpToPx(4f)
    }

    override fun showContent(show: Boolean) {
        with(binding) {
            schoolandteachersViewPager.visibility = if (show) VISIBLE else INVISIBLE
            schoolandteachersTabLayout.visibility = if (show) VISIBLE else INVISIBLE
        }
    }

    override fun showProgress(show: Boolean) {
        binding.schoolandteachersProgress.visibility = if (show) VISIBLE else INVISIBLE
    }

    fun onChildFragmentLoaded() {
        presenter.onChildViewLoaded()
    }

    override fun notifyChildLoadData(index: Int, forceRefresh: Boolean) {
        (pagerAdapter.getFragmentInstance(index) as? SchoolAndTeachersChildView)
            ?.onParentLoadData(forceRefresh)
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
