package io.github.wulkanowy.ui.modules.main

import android.content.Context
import android.content.Intent
import android.os.Build.VERSION_CODES.P
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.elevation.ElevationOverlayProvider
import com.ncapdevi.fragnav.FragNavController
import com.ncapdevi.fragnav.FragNavController.Companion.HIDE
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.databinding.ActivityMainBinding
import io.github.wulkanowy.ui.base.BaseActivity
import io.github.wulkanowy.ui.modules.Destination
import io.github.wulkanowy.ui.modules.account.accountquick.AccountQuickDialog
import io.github.wulkanowy.utils.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<MainPresenter, ActivityMainBinding>(), MainView,
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    @Inject
    override lateinit var presenter: MainPresenter

    @Inject
    lateinit var analytics: AnalyticsHelper

    @Inject
    lateinit var updateHelper: UpdateHelper

    @Inject
    lateinit var inAppReviewHelper: InAppReviewHelper

    @Inject
    lateinit var appInfo: AppInfo

    private var accountMenu: MenuItem? = null

    private val overlayProvider by lazy { ElevationOverlayProvider(this) }

    private val navController =
        FragNavController(supportFragmentManager, R.id.main_fragment_container)

    companion object {

        private const val EXTRA_START_DESTINATION = "start_destination_json"

        fun getStartIntent(
            context: Context,
            destination: Destination? = null,
        ) = Intent(context, MainActivity::class.java).apply {
            destination?.let { putExtra(EXTRA_START_DESTINATION, Json.encodeToString(it)) }
        }
    }

    override val isRootView get() = navController.isRootFragment

    override val currentStackSize get() = navController.currentStack?.size

    override val currentViewTitle
        get() = (navController.currentFrag as? MainView.TitledView)?.titleStringId
            ?.let { getString(it) }

    override val currentViewSubtitle get() = (navController.currentFrag as? MainView.TitledView)?.subtitleString

    private var savedInstanceState: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityMainBinding.inflate(layoutInflater).apply { binding = this }.root)
        setSupportActionBar(binding.mainToolbar)
        this.savedInstanceState = savedInstanceState
        messageContainer = binding.mainMessageContainer
        updateHelper.messageContainer = binding.mainFragmentContainer

        val destination = intent.getStringExtra(EXTRA_START_DESTINATION)
            ?.takeIf { savedInstanceState == null }

        presenter.onAttachView(this, destination)
        updateHelper.checkAndInstallUpdates(this)
    }

    override fun onResume() {
        super.onResume()
        updateHelper.onResume(this)
    }

    //https://developer.android.com/guide/playcore/in-app-updates#status_callback
    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        updateHelper.onActivityResult(requestCode, resultCode)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_menu_main, menu)
        accountMenu = menu.findItem(R.id.mainMenuAccount)

        presenter.onActionMenuCreated()
        return true
    }

    override fun initView(startMenuIndex: Int, rootDestinations: List<Destination>) {
        initializeToolbar()
        initializeBottomNavigation(startMenuIndex)
        initializeNavController(startMenuIndex, rootDestinations)
    }

    private fun initializeNavController(startMenuIndex: Int, rootDestinations: List<Destination>) {
        with(navController) {
            setOnViewChangeListener { destinationView ->
                presenter.onViewChange(destinationView)
                analytics.setCurrentScreen(
                    this@MainActivity,
                    destinationView::class.java.simpleName
                )
            }
            fragmentHideStrategy = HIDE
            rootFragments = rootDestinations.map { it.destinationFragment }

            initialize(startMenuIndex, savedInstanceState)
        }
        savedInstanceState = null
    }

    private fun initializeToolbar() {
        with(binding.mainToolbar) {
            stateListAnimator = null
            setBackgroundColor(
                overlayProvider.compositeOverlayWithThemeSurfaceColorIfNeeded(dpToPx(4f))
            )
        }
    }

    private fun initializeBottomNavigation(startMenuIndex: Int) {
        with(binding.mainBottomNav) {
            with(menu) {
                add(Menu.NONE, 0, Menu.NONE, R.string.dashboard_title)
                    .setIcon(R.drawable.ic_main_dashboard)
                add(Menu.NONE, 1, Menu.NONE, R.string.grade_title)
                    .setIcon(R.drawable.ic_main_grade)
                add(Menu.NONE, 2, Menu.NONE, R.string.attendance_title)
                    .setIcon(R.drawable.ic_main_attendance)
                add(Menu.NONE, 3, Menu.NONE, R.string.timetable_title)
                    .setIcon(R.drawable.ic_main_timetable)
                add(Menu.NONE, 4, Menu.NONE, R.string.more_title)
                    .setIcon(R.drawable.ic_main_more)
            }
            selectedItemId = startMenuIndex
            setOnItemSelectedListener {
                this@MainActivity.presenter.onTabSelected(it.itemId, false)
            }
            setOnItemReselectedListener {
                this@MainActivity.presenter.onTabSelected(it.itemId, true)
            }
        }
    }

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat,
        pref: Preference
    ): Boolean {
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
            classLoader,
            pref.fragment.toString()
        )
        pushView(fragment)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.mainMenuAccount) presenter.onAccountManagerSelected()
        else false
    }

    override fun onSupportNavigateUp(): Boolean {
        return presenter.onUpNavigate()
    }

    override fun switchMenuView(position: Int) {
        if (supportFragmentManager.isStateSaved) return

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

    override fun showAccountPicker(studentWithSemesters: List<StudentWithSemesters>) {
        showDialogFragment(AccountQuickDialog.newInstance(studentWithSemesters))
    }

    override fun showActionBarElevation(show: Boolean) {
        ViewCompat.setElevation(binding.mainToolbar, if (show) dpToPx(4f) else 0f)
    }

    override fun showBottomNavigation(show: Boolean) {
        binding.mainBottomNav.isVisible = show

        if (appInfo.systemVersion >= P) {
            window.navigationBarColor = if (show) {
                getThemeAttrColor(android.R.attr.navigationBarColor)
            } else {
                getThemeAttrColor(R.attr.colorSurface)
            }
        }
    }

    override fun openMoreDestination(destination: Destination) {
        pushView(destination.destinationFragment)
    }

    override fun notifyMenuViewReselected() {
        (navController.currentStack?.getOrNull(0) as? MainView.MainChildView)?.onFragmentReselected()
    }

    override fun notifyMenuViewChanged() {
        Timber.d("Menu view changed")
        (navController.currentStack?.getOrNull(0) as? MainView.MainChildView)?.onFragmentChanged()
    }

    @Suppress("DEPRECATION")
    fun showDialogFragment(dialog: DialogFragment) {
        if (supportFragmentManager.isStateSaved) return

        //Deprecated method is used here to avoid fragnav bug
        if (navController.currentDialogFrag?.fragmentManager == null) {
            FragNavController::class.java.getDeclaredField("mCurrentDialogFrag").apply {
                isAccessible = true
                set(navController, null)
            }
        }

        navController.showDialogFragment(dialog)
    }

    fun pushView(fragment: Fragment) {
        if (supportFragmentManager.isStateSaved) return

        analytics.popCurrentScreen(navController.currentFrag!!::class.simpleName)
        navController.pushFragment(fragment)
    }

    override fun popView(depth: Int) {
        if (supportFragmentManager.isStateSaved) return

        analytics.popCurrentScreen(navController.currentFrag!!::class.simpleName)
        navController.safelyPopFragments(depth)
    }

    override fun onBackPressed() {
        presenter.onBackPressed { super.onBackPressed() }
    }

    override fun showStudentAvatar(student: Student) {
        accountMenu?.run {
            icon = createNameInitialsDrawable(student.nickOrName, student.avatarColor, 0.44f)
            title = getString(R.string.main_account_picker)
        }
    }

    override fun showInAppReview() {
        inAppReviewHelper.showInAppReview(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        navController.onSaveInstanceState(outState)
    }
}
