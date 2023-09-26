package io.github.wulkanowy.ui.modules.main

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup.MarginLayoutParams
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.core.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.elevation.ElevationOverlayProvider
import com.ncapdevi.fragnav.FragNavController
import com.ncapdevi.fragnav.FragNavController.Companion.HIDE
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.databinding.ActivityMainBinding
import io.github.wulkanowy.databinding.DialogAdsConsentBinding
import io.github.wulkanowy.ui.base.BaseActivity
import io.github.wulkanowy.ui.modules.Destination
import io.github.wulkanowy.ui.modules.account.accountquick.AccountQuickDialog
import io.github.wulkanowy.ui.modules.settings.appearance.menuorder.AppMenuItem
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

    private var onBackCallback: OnBackPressedCallback? = null

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            binding.mainAppBar.isLifted = true
        }
        initializeFragmentContainer()

        this.savedInstanceState = savedInstanceState
        messageContainer = binding.mainMessageContainer
        messageAnchor = binding.mainMessageContainer
        updateHelper.messageContainer = binding.mainFragmentContainer
        onBackCallback = onBackPressedDispatcher.addCallback(this, enabled = false) {
            presenter.onBackPressed()
        }

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
    @Deprecated("Deprecated in Java")
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

    override fun initView(
        startMenuIndex: Int,
        rootAppMenuItems: List<AppMenuItem>,
        rootUpdatedDestinations: List<Destination>
    ) {
        initializeToolbar()
        initializeBottomNavigation(startMenuIndex, rootAppMenuItems)
        initializeNavController(startMenuIndex, rootUpdatedDestinations)
    }

    private fun initializeNavController(
        startMenuIndex: Int,
        rootUpdatedDestinations: List<Destination>
    ) {
        with(navController) {
            setOnViewChangeListener { destinationView ->
                presenter.onViewChange(destinationView)
                analytics.setCurrentScreen(
                    this@MainActivity,
                    destinationView::class.java.simpleName
                )
            }
            fragmentHideStrategy = HIDE
            rootFragments = rootUpdatedDestinations.map { it.destinationFragment }

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

    private fun initializeBottomNavigation(
        startMenuIndex: Int,
        rootAppMenuItems: List<AppMenuItem>
    ) {
        with(binding.mainBottomNav) {
            with(menu) {
                rootAppMenuItems.forEachIndexed { index, item ->
                    add(Menu.NONE, index, Menu.NONE, item.title)
                        .setIcon(item.icon)
                }
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

    private fun initializeFragmentContainer() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.mainFragmentContainer) { view, insets ->
            val bottomInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())

            view.updateLayoutParams<MarginLayoutParams> {
                bottomMargin = if (binding.mainBottomNav.isVisible) 0 else bottomInsets.bottom
            }
            WindowInsetsCompat.CONSUMED
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

    override fun showBottomNavigation(show: Boolean) {
        binding.mainBottomNav.isVisible = show
        binding.mainFragmentContainer.requestApplyInsets()
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
        onBackCallback?.isEnabled = !isRootView
    }

    override fun popView(depth: Int) {
        if (supportFragmentManager.isStateSaved) return

        analytics.popCurrentScreen(navController.currentFrag!!::class.simpleName)
        navController.safelyPopFragments(depth)
        onBackCallback?.isEnabled = !isRootView
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

    override fun showAppSupport() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.main_support_title)
            .setMessage(R.string.main_support_description)
            .setPositiveButton(R.string.main_support_positive) { _, _ -> presenter.onEnableAdsSelected() }
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .setOnDismissListener { }
            .show()
    }

    override fun showPrivacyPolicyDialog() {
        val dialogAdsConsentBinding = DialogAdsConsentBinding.inflate(layoutInflater)

        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.pref_ads_consent_title)
            .setMessage(R.string.pref_ads_consent_description)
            .setView(dialogAdsConsentBinding.root)
            .show()

        dialogAdsConsentBinding.adsConsentOver.setOnCheckedChangeListener { _, isChecked ->
            dialogAdsConsentBinding.adsConsentPersonalised.isEnabled = isChecked
        }

        dialogAdsConsentBinding.adsConsentPersonalised.setOnClickListener {
            presenter.onPrivacyAgree(true)
            dialog.dismiss()
        }

        dialogAdsConsentBinding.adsConsentNonPersonalised.setOnClickListener {
            presenter.onPrivacyAgree(false)
            dialog.dismiss()
        }

        dialogAdsConsentBinding.adsConsentPrivacy.setOnClickListener { presenter.onPrivacySelected() }
        dialogAdsConsentBinding.adsConsentCancel.setOnClickListener { dialog.cancel() }
    }

    override fun openPrivacyPolicy() {
        openInternetBrowser(
            "https://wulkanowy.github.io/polityka-prywatnosci.html",
            ::showMessage
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        navController.onSaveInstanceState(outState)
    }
}
