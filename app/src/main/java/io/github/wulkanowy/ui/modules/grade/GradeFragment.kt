package io.github.wulkanowy.ui.modules.grade

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.appcompat.app.AlertDialog
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.databinding.FragmentGradeBinding
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.base.BaseFragmentPagerAdapter
import io.github.wulkanowy.ui.modules.grade.details.GradeDetailsFragment
import io.github.wulkanowy.ui.modules.grade.statistics.GradeStatisticsFragment
import io.github.wulkanowy.ui.modules.grade.summary.GradeSummaryFragment
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.dpToPx
import io.github.wulkanowy.utils.setOnSelectPageListener
import javax.inject.Inject

@AndroidEntryPoint
class GradeFragment : BaseFragment<FragmentGradeBinding>(R.layout.fragment_grade), GradeView,
    MainView.MainChildView, MainView.TitledView {

    @Inject
    lateinit var presenter: GradePresenter

    private val pagerAdapter by lazy { BaseFragmentPagerAdapter(childFragmentManager) }

    private var semesterSwitchMenu: MenuItem? = null

    companion object {

        fun newInstance() = GradeFragment()
    }

    override val titleStringId get() = R.string.grade_title

    override var subtitleString = ""

    override val currentPageIndex get() = binding.gradeViewPager.currentItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentGradeBinding.bind(view)
        presenter.onAttachView(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.action_menu_grade, menu)
        semesterSwitchMenu = menu.findItem(R.id.gradeMenuSemester)
        presenter.onCreateMenu()
    }

    override fun initView() {
        with(pagerAdapter) {
            containerId = binding.gradeViewPager.id
            addFragmentsWithTitle(mapOf(
                GradeDetailsFragment.newInstance() to getString(R.string.all_details),
                GradeSummaryFragment.newInstance() to getString(R.string.grade_menu_summary),
                GradeStatisticsFragment.newInstance() to getString(R.string.grade_menu_statistics)
            ))
        }

        with(binding.gradeViewPager) {
            adapter = pagerAdapter
            offscreenPageLimit = 3
            setOnSelectPageListener(presenter::onPageSelected)
        }

        with(binding.gradeTabLayout) {
            setupWithViewPager(binding.gradeViewPager)
            setElevationCompat(context.dpToPx(4f))
        }

        with(binding) {
            gradeErrorRetry.setOnClickListener { presenter.onRetry() }
            gradeErrorDetails.setOnClickListener { presenter.onDetailsClick() }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.gradeMenuSemester) presenter.onSemesterSwitch()
        else false
    }

    override fun onFragmentReselected() {
        if (::presenter.isInitialized) presenter.onViewReselected()
    }

    override fun showContent(show: Boolean) {
        with(binding) {
            gradeViewPager.visibility = if (show) VISIBLE else INVISIBLE
            gradeTabLayout.visibility = if (show) VISIBLE else INVISIBLE
        }
    }

    override fun showProgress(show: Boolean) {
        binding.gradeProgress.visibility = if (show) VISIBLE else INVISIBLE
    }

    override fun showErrorView(show: Boolean) {
        binding.gradeError.visibility = if (show) VISIBLE else INVISIBLE
    }

    override fun setErrorDetails(message: String) {
        binding.gradeErrorMessage.text = message
    }

    override fun showSemesterSwitch(show: Boolean) {
        semesterSwitchMenu?.isVisible = show
    }

    override fun showSemesterDialog(selectedIndex: Int) {
        val choices = arrayOf(
            getString(R.string.grade_semester, 1),
            getString(R.string.grade_semester, 2)
        )

        AlertDialog.Builder(requireContext())
            .setSingleChoiceItems(choices, selectedIndex) { dialog, which ->
                presenter.onSemesterSelected(which)
                dialog.dismiss()
            }
            .setTitle(R.string.grade_switch_semester)
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .show()
    }

    override fun setCurrentSemesterName(semester: Int, schoolYear: Int) {
        subtitleString = getString(R.string.grade_subtitle, semester, schoolYear, schoolYear + 1)
        (activity as MainView).setViewSubTitle(subtitleString)
    }

    fun onChildRefresh() {
        presenter.onChildViewRefresh()
    }

    fun onChildFragmentLoaded(semesterId: Int) {
        presenter.onChildViewLoaded(semesterId)
    }

    override fun notifyChildLoadData(index: Int, semesterId: Int, forceRefresh: Boolean) {
        (pagerAdapter.getFragmentInstance(index) as? GradeView.GradeChildView)?.onParentLoadData(semesterId, forceRefresh)
    }

    override fun notifyChildParentReselected(index: Int) {
        (pagerAdapter.getFragmentInstance(index) as? GradeView.GradeChildView)?.onParentReselected()
    }

    override fun notifyChildSemesterChange(index: Int) {
        (pagerAdapter.getFragmentInstance(index) as? GradeView.GradeChildView)?.onParentChangeSemester()
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
