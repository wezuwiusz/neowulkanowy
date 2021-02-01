package io.github.wulkanowy.ui.modules.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.LOLLIPOP
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import androidx.core.view.ViewCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation.TitleState.ALWAYS_SHOW
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.google.android.material.elevation.ElevationOverlayProvider
import com.ncapdevi.fragnav.FragNavController
import com.ncapdevi.fragnav.FragNavController.Companion.HIDE
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.databinding.ActivityMainBinding
import io.github.wulkanowy.ui.base.BaseActivity
import io.github.wulkanowy.ui.modules.account.accountquick.AccountQuickDialog
import io.github.wulkanowy.ui.modules.attendance.AttendanceFragment
import io.github.wulkanowy.ui.modules.exam.ExamFragment
import io.github.wulkanowy.ui.modules.grade.GradeFragment
import io.github.wulkanowy.ui.modules.homework.HomeworkFragment
import io.github.wulkanowy.ui.modules.luckynumber.LuckyNumberFragment
import io.github.wulkanowy.ui.modules.message.MessageFragment
import io.github.wulkanowy.ui.modules.more.MoreFragment
import io.github.wulkanowy.ui.modules.note.NoteFragment
import io.github.wulkanowy.ui.modules.timetable.TimetableFragment
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.AppInfo
import io.github.wulkanowy.utils.UpdateHelper
import io.github.wulkanowy.utils.dpToPx
import io.github.wulkanowy.utils.getThemeAttrColor
import io.github.wulkanowy.utils.safelyPopFragments
import io.github.wulkanowy.utils.setOnViewChangeListener
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<MainPresenter, ActivityMainBinding>(), MainView {

    @Inject
    override lateinit var presenter: MainPresenter

    @Inject
    lateinit var analytics: AnalyticsHelper

    @Inject
    lateinit var updateHelper: UpdateHelper

    @Inject
    lateinit var appInfo: AppInfo

    private val overlayProvider by lazy { ElevationOverlayProvider(this) }

    private val navController =
        FragNavController(supportFragmentManager, R.id.mainFragmentContainer)

    companion object {
        const val EXTRA_START_MENU = "extraStartMenu"

        fun getStartIntent(
            context: Context,
            startMenu: MainView.Section? = null,
            clear: Boolean = false
        ) = Intent(context, MainActivity::class.java).apply {
            if (clear) flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
            startMenu?.let { putExtra(EXTRA_START_MENU, it.id) }
        }
    }

    override val isRootView get() = navController.isRootFragment

    override val currentStackSize get() = navController.currentStack?.size

    override val currentViewTitle
        get() = (navController.currentFrag as? MainView.TitledView)?.titleStringId?.let {
            getString(it)
        }

    override val currentViewSubtitle get() = (navController.currentFrag as? MainView.TitledView)?.subtitleString

    override var startMenuIndex = 0

    override var startMenuMoreIndex = -1

    private val moreMenuFragments = mapOf<Int, Fragment>(
        MainView.Section.MESSAGE.id to MessageFragment.newInstance(),
        MainView.Section.HOMEWORK.id to HomeworkFragment.newInstance(),
        MainView.Section.NOTE.id to NoteFragment.newInstance(),
        MainView.Section.LUCKY_NUMBER.id to LuckyNumberFragment.newInstance()
    )

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityMainBinding.inflate(layoutInflater).apply { binding = this }.root)
        setSupportActionBar(binding.mainToolbar)
        messageContainer = binding.mainFragmentContainer
        updateHelper.messageContainer = binding.mainFragmentContainer

        val section = MainView.Section.values()
            .singleOrNull { it.id == intent.getIntExtra(EXTRA_START_MENU, -1) }

        presenter.onAttachView(this, section)

        with(navController) {
            initialize(startMenuIndex, savedInstanceState)
            pushFragment(moreMenuFragments[startMenuMoreIndex])
        }
        updateHelper.checkAndInstallUpdates(this)
    }

    override fun onResume() {
        super.onResume()
        updateHelper.onResume(this)
    }

    @SuppressLint("NewApi")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        updateHelper.onActivityResult(requestCode, resultCode)
        if (appInfo.systemVersion >= Build.VERSION_CODES.N_MR1) initShortcuts()
    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    fun initShortcuts() {
        val shortcutsList = mutableListOf<ShortcutInfo>()

        listOf(
            Triple(
                getString(R.string.grade_title),
                R.drawable.ic_shortcut_grade,
                MainView.Section.GRADE
            ),
            Triple(
                getString(R.string.attendance_title),
                R.drawable.ic_shortcut_attendance,
                MainView.Section.ATTENDANCE
            ),
            Triple(
                getString(R.string.exam_title),
                R.drawable.ic_shortcut_exam,
                MainView.Section.EXAM
            ),
            Triple(
                getString(R.string.timetable_title),
                R.drawable.ic_shortcut_timetable,
                MainView.Section.TIMETABLE
            ),
            Triple(
                getString(R.string.message_title),
                R.drawable.ic_shortcut_message,
                MainView.Section.MESSAGE
            )
        ).forEach { (title, icon, enum) ->
            shortcutsList.add(
                ShortcutInfo.Builder(applicationContext, title)
                    .setShortLabel(title)
                    .setLongLabel(title)
                    .setIcon(Icon.createWithResource(applicationContext, icon))
                    .setIntents(
                        arrayOf(
                            Intent(applicationContext, MainActivity::class.java)
                                .setAction(Intent.ACTION_VIEW),
                            Intent(applicationContext, MainActivity::class.java)
                                .putExtra(EXTRA_START_MENU, enum.id)
                                .setAction(Intent.ACTION_VIEW)
                                .addFlags(FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK)
                        )
                    )
                    .build()
            )
        }

        getSystemService<ShortcutManager>()?.dynamicShortcuts = shortcutsList
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_menu_main, menu)
        return true
    }

    override fun initView() {
        with(binding.mainToolbar) {
            if (SDK_INT >= LOLLIPOP) stateListAnimator = null
            setBackgroundColor(
                overlayProvider.compositeOverlayWithThemeSurfaceColorIfNeeded(dpToPx(4f))
            )
        }

        with(binding.mainBottomNav) {
            addItems(
                listOf(
                    AHBottomNavigationItem(R.string.grade_title, R.drawable.ic_main_grade, 0),
                    AHBottomNavigationItem(
                        R.string.attendance_title,
                        R.drawable.ic_main_attendance,
                        0
                    ),
                    AHBottomNavigationItem(R.string.exam_title, R.drawable.ic_main_exam, 0),
                    AHBottomNavigationItem(
                        R.string.timetable_title,
                        R.drawable.ic_main_timetable,
                        0
                    ),
                    AHBottomNavigationItem(R.string.more_title, R.drawable.ic_main_more, 0)
                )
            )
            accentColor = getThemeAttrColor(R.attr.colorPrimary)
            inactiveColor = getThemeAttrColor(R.attr.colorOnSurface, 153)
            defaultBackgroundColor =
                overlayProvider.compositeOverlayWithThemeSurfaceColorIfNeeded(dpToPx(8f))
            titleState = ALWAYS_SHOW
            currentItem = startMenuIndex
            isBehaviorTranslationEnabled = false
            setTitleTextSizeInSp(10f, 10f)
            setOnTabSelectedListener(presenter::onTabSelected)
        }

        with(navController) {
            setOnViewChangeListener { section, name ->
                binding.mainBottomNav.visibility =
                    if (section == MainView.Section.ACCOUNT || section == MainView.Section.STUDENT_INFO) {
                        View.GONE
                    } else {
                        View.VISIBLE
                    }

                analytics.setCurrentScreen(this@MainActivity, name)
                presenter.onViewChange(section)
            }
            fragmentHideStrategy = HIDE
            rootFragments = listOf(
                GradeFragment.newInstance(),
                AttendanceFragment.newInstance(),
                ExamFragment.newInstance(),
                TimetableFragment.newInstance(),
                MoreFragment.newInstance()
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.mainMenuAccount) presenter.onAccountManagerSelected()
        else false
    }

    override fun onSupportNavigateUp(): Boolean {
        return presenter.onUpNavigate()
    }

    override fun switchMenuView(position: Int) {
        analytics.popCurrentScreen(navController.currentFrag!!::class.simpleName)
        navController.switchTab(position)
    }

    override fun setViewTitle(title: String) {
        supportActionBar?.title = title
    }

    override fun setViewSubTitle(subtitle: String?) {
        supportActionBar?.subtitle = subtitle
    }

    override fun showHomeArrow(show: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(show)
    }

    override fun showAccountPicker() {
        navController.showDialogFragment(AccountQuickDialog.newInstance())
    }

    override fun showActionBarElevation(show: Boolean) {
        ViewCompat.setElevation(binding.mainToolbar, if (show) dpToPx(4f) else 0f)
    }

    override fun notifyMenuViewReselected() {
        (navController.currentStack?.getOrNull(0) as? MainView.MainChildView)?.onFragmentReselected()
    }

    override fun notifyMenuViewChanged() {
        Timber.d("Menu view changed")
        (navController.currentStack?.getOrNull(0) as? MainView.MainChildView)?.onFragmentChanged()
    }

    fun showDialogFragment(dialog: DialogFragment) {
        navController.showDialogFragment(dialog)
    }

    fun pushView(fragment: Fragment) {
        analytics.popCurrentScreen(navController.currentFrag!!::class.simpleName)
        navController.pushFragment(fragment)
    }

    override fun popView(depth: Int) {
        analytics.popCurrentScreen(navController.currentFrag!!::class.simpleName)
        navController.safelyPopFragments(depth)
    }

    override fun onBackPressed() {
        presenter.onBackPressed { super.onBackPressed() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        navController.onSaveInstanceState(outState)
        intent.removeExtra(EXTRA_START_MENU)
    }
}
