package io.github.wulkanowy.ui.modules.main

import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.data.logResourceStatus
import io.github.wulkanowy.data.onResourceError
import io.github.wulkanowy.data.onResourceSuccess
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.data.resourceFlow
import io.github.wulkanowy.services.sync.SyncManager
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.BaseView
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.ui.modules.Destination
import io.github.wulkanowy.ui.modules.account.AccountView
import io.github.wulkanowy.ui.modules.account.accountdetails.AccountDetailsView
import io.github.wulkanowy.ui.modules.grade.GradeView
import io.github.wulkanowy.ui.modules.message.MessageView
import io.github.wulkanowy.ui.modules.schoolandteachers.SchoolAndTeachersView
import io.github.wulkanowy.ui.modules.studentinfo.StudentInfoView
import io.github.wulkanowy.utils.AdsHelper
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.AppInfo
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.time.Duration
import java.time.Instant
import javax.inject.Inject

class MainPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val preferencesRepository: PreferencesRepository,
    private val syncManager: SyncManager,
    private val analytics: AnalyticsHelper,
    private val json: Json,
    private val adsHelper: AdsHelper,
    private val appInfo: AppInfo
) : BasePresenter<MainView>(errorHandler, studentRepository) {

    private var studentsWitSemesters: List<StudentWithSemesters>? = null

    private val rootDestinationTypeList = listOf(
        Destination.Type.DASHBOARD,
        Destination.Type.GRADE,
        Destination.Type.ATTENDANCE,
        Destination.Type.TIMETABLE,
        Destination.Type.MORE
    )

    private val Destination?.startMenuIndex
        get() = when {
            this == null -> preferencesRepository.startMenuIndex
            destinationType in rootDestinationTypeList -> {
                rootDestinationTypeList.indexOf(destinationType)
            }
            else -> 4
        }

    fun onAttachView(view: MainView, initDestinationJson: String?) {
        super.onAttachView(view)

        val initDestination: Destination? = initDestinationJson?.let { json.decodeFromString(it) }

        val startMenuIndex = initDestination.startMenuIndex
        val destinations = rootDestinationTypeList.map {
            if (it == initDestination?.destinationType) initDestination else it.defaultDestination
        }

        view.initView(startMenuIndex, destinations)
        if (initDestination != null && startMenuIndex == 4) {
            view.openMoreDestination(initDestination)
        }

        syncManager.startPeriodicSyncWorker()

        checkAppSupport()

        analytics.logEvent("app_open", "destination" to initDestination.toString())
        Timber.i("Main view was initialized with $initDestination")
    }

    fun onActionMenuCreated() {
        if (!studentsWitSemesters.isNullOrEmpty()) {
            showCurrentStudentAvatar()
            return
        }

        resourceFlow { studentRepository.getSavedStudents(false) }
            .logResourceStatus("load student avatar")
            .onResourceSuccess {
                studentsWitSemesters = it
                showCurrentStudentAvatar()
            }
            .onResourceError(errorHandler::dispatch)
            .launch("avatar")
    }

    fun onViewChange(destinationView: BaseView) {
        view?.apply {
            showBottomNavigation(shouldShowBottomNavigation(destinationView))
            showActionBarElevation(shouldShowActionBarElevation(destinationView))
            currentViewTitle?.let { setViewTitle(it) }
            currentViewSubtitle?.let { setViewSubTitle(it.ifBlank { null }) }
            currentStackSize?.let {
                if (it > 1) showHomeArrow(true)
                else showHomeArrow(false)
            }
        }
    }

    private fun shouldShowActionBarElevation(destination: BaseView) = when (destination) {
        is GradeView,
        is MessageView,
        is SchoolAndTeachersView -> false
        else -> true
    }

    private fun shouldShowBottomNavigation(destination: BaseView) = when (destination) {
        is AccountView,
        is StudentInfoView,
        is AccountDetailsView -> false
        else -> true
    }

    fun onAccountManagerSelected(): Boolean {
        if (studentsWitSemesters.isNullOrEmpty()) return true

        Timber.i("Select account manager")
        view?.showAccountPicker(studentsWitSemesters!!)
        return true
    }

    fun onUpNavigate(): Boolean {
        Timber.i("Up navigate pressed")
        view?.popView()
        return true
    }

    fun onBackPressed() {
        Timber.i("Back pressed in main view")
        view?.popView()
    }

    fun onTabSelected(index: Int, wasSelected: Boolean): Boolean {
        return view?.run {
            Timber.i("Switch main tab index: $index, reselected: $wasSelected")
            if (wasSelected) {
                notifyMenuViewReselected()
                false
            } else {
                notifyMenuViewChanged()
                switchMenuView(index)
                checkInAppReview()
                true
            }
        } == true
    }

    fun onEnableAdsSelected() {
        view?.showPrivacyPolicyDialog()
    }

    fun onPrivacyAgree(isPersonalizedAds: Boolean) {
        preferencesRepository.isAgreeToProcessData = true
        preferencesRepository.isPersonalizedAdsEnabled = isPersonalizedAds

        adsHelper.initialize()

        preferencesRepository.isAdsEnabled = true
    }

    fun onPrivacySelected() {
        view?.openPrivacyPolicy()
    }

    private fun checkInAppReview() {
        preferencesRepository.inAppReviewCount++

        if (preferencesRepository.inAppReviewDate == null) {
            preferencesRepository.inAppReviewDate = Instant.now()
        }

        if (!preferencesRepository.isAppReviewDone && preferencesRepository.inAppReviewCount >= 50 &&
            Instant.now().minus(Duration.ofDays(14)).isAfter(preferencesRepository.inAppReviewDate)
        ) {
            view?.showInAppReview()
            preferencesRepository.isAppReviewDone = true
        }
    }

    private fun checkAppSupport() {
        if (!preferencesRepository.isAppSupportShown && !preferencesRepository.isAdsEnabled
            && appInfo.buildFlavor == "play"
        ) {
            presenterScope.launch {
                val student = runCatching { studentRepository.getCurrentStudent(false) }
                    .onFailure { Timber.e(it) }
                    .getOrElse { return@launch }

                if (Instant.now().minus(Duration.ofDays(28)).isAfter(student.registrationDate)) {
                    view?.showAppSupport()
                    preferencesRepository.isAppSupportShown = true
                }
            }
        }
    }

    private fun showCurrentStudentAvatar() {
        val currentStudent =
            studentsWitSemesters?.singleOrNull { it.student.isCurrent }?.student ?: return

        view?.showStudentAvatar(currentStudent)
    }
}
