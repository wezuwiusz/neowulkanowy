package io.github.wulkanowy.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.ncapdevi.fragnav.FragNavController
import com.ncapdevi.fragnav.FragNavController.Companion.DETACH_ON_NAVIGATE_HIDE_ON_SWITCH
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseActivity
import io.github.wulkanowy.ui.main.attendance.AttendanceFragment
import io.github.wulkanowy.ui.main.exam.ExamFragment
import io.github.wulkanowy.ui.main.grade.GradeFragment
import io.github.wulkanowy.ui.main.more.MoreFragment
import io.github.wulkanowy.ui.main.timetable.TimetableFragment
import io.github.wulkanowy.utils.setOnTabTransactionListener
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : BaseActivity(), MainView {

    @Inject
    lateinit var presenter: MainPresenter

    @Inject
    lateinit var navController: FragNavController

    companion object {
        const val DEFAULT_TAB = 2

        fun getStartIntent(context: Context) = Intent(context, MainActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(mainToolbar)
        messageContainer = mainFragmentContainer

        presenter.attachView(this)
        navController.initialize(DEFAULT_TAB, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        presenter.onStartView()
    }

    override fun initView() {
        mainBottomNav.run {
            addItems(mutableListOf(
                    AHBottomNavigationItem(R.string.grade_title, R.drawable.ic_menu_main_grade_26dp, 0),
                    AHBottomNavigationItem(R.string.attendance_title, R.drawable.ic_menu_main_attendance_24dp, 0),
                    AHBottomNavigationItem(R.string.exam_title, R.drawable.ic_menu_main_exam_24dp, 0),
                    AHBottomNavigationItem(R.string.timetable_title, R.drawable.ic_menu_main_timetable_24dp, 0),
                    AHBottomNavigationItem(R.string.more_title, R.drawable.ic_menu_main_more_24dp, 0)
            ))
            accentColor = ContextCompat.getColor(context, R.color.colorPrimary)
            inactiveColor = ContextCompat.getColor(context, android.R.color.black)
            titleState = AHBottomNavigation.TitleState.ALWAYS_SHOW
            currentItem = DEFAULT_TAB
            isBehaviorTranslationEnabled = false
            setTitleTextSizeInSp(10f, 10f)

            setOnTabSelectedListener { position, wasSelected ->
                presenter.onTabSelected(position, wasSelected)
            }
        }

        navController.run {
            setOnTabTransactionListener { presenter.onMenuViewChange(it) }
            fragmentHideStrategy = DETACH_ON_NAVIGATE_HIDE_ON_SWITCH
            rootFragments = listOf(
                    GradeFragment.newInstance(),
                    AttendanceFragment.newInstance(),
                    ExamFragment.newInstance(),
                    TimetableFragment.newInstance(),
                    MoreFragment.newInstance()
            )
        }
    }

    override fun switchMenuView(position: Int) {
        navController.switchTab(position)
    }

    override fun setViewTitle(title: String) {
        supportActionBar?.title = title
    }

    override fun expandActionBar(show: Boolean) {
        mainAppBarContainer.setExpanded(show, true)
    }

    override fun viewTitle(index: Int): String {
        return getString(listOf(R.string.grade_title,
                R.string.attendance_title,
                R.string.exam_title,
                R.string.timetable_title,
                R.string.more_title)[index])
    }

    override fun currentMenuIndex() = navController.currentStackIndex

    override fun notifyMenuViewReselected() {
        (navController.currentFrag as? MainView.MenuFragmentView)?.onFragmentReselected()
    }

    override fun onBackPressed() {
        navController.apply { if (isRootFragment) super.onBackPressed() else popFragment() }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        navController.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}
