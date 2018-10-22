package io.github.wulkanowy.ui.main.grade

import android.os.Bundle
import android.view.*
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.appcompat.app.AlertDialog
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.base.BasePagerAdapter
import io.github.wulkanowy.ui.main.MainView
import io.github.wulkanowy.ui.main.grade.details.GradeDetailsFragment
import io.github.wulkanowy.ui.main.grade.summary.GradeSummaryFragment
import io.github.wulkanowy.utils.setOnSelectPageListener
import kotlinx.android.synthetic.main.fragment_grade.*
import javax.inject.Inject

class GradeFragment : BaseFragment(), GradeView, MainView.MainChildView, MainView.TitledView {

    @Inject
    lateinit var presenter: GradePresenter

    @Inject
    lateinit var pagerAdapter: BasePagerAdapter

    companion object {
        private const val SAVED_SEMESTER_KEY = "CURRENT_SEMESTER"

        fun newInstance() = GradeFragment()
    }

    override val titleStringId: Int
        get() = R.string.grade_title

    override val currentPageIndex: Int
        get() = gradeViewPager.currentItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_grade, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.onAttachView(this, savedInstanceState?.getInt(SAVED_SEMESTER_KEY))
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.action_menu_grade, menu)
    }

    override fun initView() {
        pagerAdapter.fragments.putAll(mapOf(
                getString(R.string.all_details) to GradeDetailsFragment.newInstance(),
                getString(R.string.grade_menu_summary) to GradeSummaryFragment.newInstance()
        ))
        gradeViewPager.run {
            adapter = pagerAdapter
            setOnSelectPageListener { presenter.onPageSelected(it) }
        }
        gradeTabLayout.setupWithViewPager(gradeViewPager)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == R.id.gradeMenuSemester) presenter.onSemesterSwitch()
        else false
    }

    override fun onFragmentReselected() {
        presenter.onViewReselected()
    }

    override fun showContent(show: Boolean) {
        gradeViewPager.visibility = if (show) VISIBLE else INVISIBLE
        gradeTabLayout.visibility = if (show) VISIBLE else INVISIBLE
    }

    override fun showProgress(show: Boolean) {
        gradeProgress.visibility = if (show) VISIBLE else INVISIBLE
    }

    override fun showSemesterDialog(selectedIndex: Int) {
        arrayOf(getString(R.string.grade_semester, 1),
                getString(R.string.grade_semester, 2)).also { array ->
            context?.let {
                AlertDialog.Builder(it)
                        .setSingleChoiceItems(array, selectedIndex) { dialog, which ->
                            presenter.onSemesterSelected(which)
                            dialog.dismiss()
                        }
                        .setTitle(R.string.grade_switch_semester)
                        .setNegativeButton(R.string.all_cancel) { dialog, _ -> dialog.dismiss() }
                        .show()
            }
        }
    }

    fun onChildRefresh() {
        presenter.onChildViewRefresh()
    }

    fun onChildFragmentLoaded(semesterId: Int) {
        presenter.onChildViewLoaded(semesterId)
    }

    override fun notifyChildLoadData(index: Int, semesterId: Int, forceRefresh: Boolean) {
        (childFragmentManager.fragments[index] as GradeView.GradeChildView).onParentLoadData(semesterId, forceRefresh)
    }

    override fun notifyChildParentReselected(index: Int) {
        (pagerAdapter.registeredFragments[index] as? GradeView.GradeChildView)?.onParentReselected()
    }

    override fun notifyChildSemesterChange(index: Int) {
        (pagerAdapter.registeredFragments[index] as? GradeView.GradeChildView)?.onParentChangeSemester()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SAVED_SEMESTER_KEY, presenter.selectedIndex)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.onDetachView()
    }
}
