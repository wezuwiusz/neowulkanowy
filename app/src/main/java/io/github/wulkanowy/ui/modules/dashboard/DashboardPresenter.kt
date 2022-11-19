package io.github.wulkanowy.ui.modules.dashboard

import io.github.wulkanowy.data.*
import io.github.wulkanowy.data.db.entities.AdminMessage
import io.github.wulkanowy.data.db.entities.LuckyNumber
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.enums.MessageFolder
import io.github.wulkanowy.data.repositories.*
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AdsHelper
import io.github.wulkanowy.utils.calculatePercentage
import io.github.wulkanowy.utils.nextOrSameSchoolDay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

class DashboardPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val luckyNumberRepository: LuckyNumberRepository,
    private val gradeRepository: GradeRepository,
    private val semesterRepository: SemesterRepository,
    private val messageRepository: MessageRepository,
    private val attendanceSummaryRepository: AttendanceSummaryRepository,
    private val timetableRepository: TimetableRepository,
    private val homeworkRepository: HomeworkRepository,
    private val examRepository: ExamRepository,
    private val conferenceRepository: ConferenceRepository,
    private val preferencesRepository: PreferencesRepository,
    private val schoolAnnouncementRepository: SchoolAnnouncementRepository,
    private val adminMessageRepository: AdminMessageRepository,
    private val adsHelper: AdsHelper
) : BasePresenter<DashboardView>(errorHandler, studentRepository) {

    private val dashboardItemLoadedList = mutableListOf<DashboardItem>()

    private val dashboardItemRefreshLoadedList = mutableListOf<DashboardItem>()

    private var dashboardItemsToLoad = emptySet<DashboardItem.Type>()

    private var dashboardTileLoadedList = emptySet<DashboardItem.Tile>()

    private val firstLoadedItemList = mutableListOf<DashboardItem.Type>()

    private lateinit var lastError: Throwable

    override fun onAttachView(view: DashboardView) {
        super.onAttachView(view)

        with(view) {
            initView()
            showProgress(true)
            showContent(false)
        }

        merge(
            preferencesRepository.selectedDashboardTilesFlow,
            preferencesRepository.isAdsEnabledFlow
                .map { preferencesRepository.selectedDashboardTiles }
        )
            .onEach { loadData(tilesToLoad = it) }
            .launch("dashboard_pref")
    }

    fun onAdminMessageDismissed(adminMessage: AdminMessage) {
        preferencesRepository.dismissedAdminMessageIds += adminMessage.id

        loadData(preferencesRepository.selectedDashboardTiles)
    }

    fun onDragAndDropEnd(list: List<DashboardItem>) {
        with(dashboardItemLoadedList) {
            clear()
            addAll(list)
        }

        val positionList =
            list.mapIndexed { index, dashboardItem -> Pair(dashboardItem.type, index) }.toMap()

        preferencesRepository.dashboardItemsPosition = positionList
    }

    fun loadData(
        tilesToLoad: Set<DashboardItem.Tile>,
        forceRefresh: Boolean = false,
    ) {
        val oldDashboardTileLoadedList = dashboardTileLoadedList
        dashboardItemsToLoad = tilesToLoad.map { it.toDashboardItemType() }.toSet()
        dashboardTileLoadedList = tilesToLoad

        val itemsToLoad = generateDashboardTileListToLoad(
            dashboardTilesToLoad = tilesToLoad,
            dashboardLoadedTiles = oldDashboardTileLoadedList,
            forceRefresh = forceRefresh
        ).map { it.toDashboardItemType() }

        removeUnselectedTiles(tilesToLoad.toList())
        loadTiles(tileList = itemsToLoad, forceRefresh = forceRefresh)
    }

    private fun generateDashboardTileListToLoad(
        dashboardTilesToLoad: Set<DashboardItem.Tile>,
        dashboardLoadedTiles: Set<DashboardItem.Tile>,
        forceRefresh: Boolean
    ) = dashboardTilesToLoad.filter { newItemToLoad ->
        dashboardLoadedTiles.none { it == newItemToLoad } || forceRefresh
            || newItemToLoad == DashboardItem.Tile.ADMIN_MESSAGE
    }

    private fun removeUnselectedTiles(tilesToLoad: List<DashboardItem.Tile>) {
        dashboardItemLoadedList.removeAll { loadedTile -> dashboardItemsToLoad.none { it == loadedTile.type } }

        val horizontalGroup =
            dashboardItemLoadedList.find { it is DashboardItem.HorizontalGroup } as DashboardItem.HorizontalGroup?

        if (horizontalGroup != null) {
            val isLuckyNumberToLoad = DashboardItem.Tile.LUCKY_NUMBER in tilesToLoad
            val isMessagesToLoad = DashboardItem.Tile.MESSAGES in tilesToLoad
            val isAttendanceToLoad = DashboardItem.Tile.ATTENDANCE in tilesToLoad

            val horizontalGroupIndex = dashboardItemLoadedList.indexOf(horizontalGroup)

            val newHorizontalGroup = horizontalGroup.copy(
                attendancePercentage = horizontalGroup.attendancePercentage.takeIf { isAttendanceToLoad },
                unreadMessagesCount = horizontalGroup.unreadMessagesCount.takeIf { isMessagesToLoad },
                luckyNumber = horizontalGroup.luckyNumber.takeIf { isLuckyNumberToLoad }
            )

            with(dashboardItemLoadedList) {
                removeAt(horizontalGroupIndex)
                add(horizontalGroupIndex, newHorizontalGroup)
            }
        }

        view?.updateData(dashboardItemLoadedList)
    }

    private fun loadTiles(
        tileList: List<DashboardItem.Type>,
        forceRefresh: Boolean
    ) {
        presenterScope.launch {
            Timber.i("Loading dashboard account data started")
            val student = runCatching { studentRepository.getCurrentStudent(true) }
                .onFailure {
                    Timber.i("Loading dashboard account result: An exception occurred")
                    errorHandler.dispatch(it)
                    updateData(DashboardItem.Account(error = it), forceRefresh)
                }
                .onSuccess { Timber.i("Loading dashboard account result: Success") }
                .getOrNull() ?: return@launch

            tileList.forEach {
                when (it) {
                    DashboardItem.Type.ACCOUNT -> {
                        updateData(DashboardItem.Account(student), forceRefresh)
                    }
                    DashboardItem.Type.HORIZONTAL_GROUP -> {
                        loadHorizontalGroup(student, forceRefresh)
                    }
                    DashboardItem.Type.LESSONS -> loadLessons(student, forceRefresh)
                    DashboardItem.Type.GRADES -> loadGrades(student, forceRefresh)
                    DashboardItem.Type.HOMEWORK -> loadHomework(student, forceRefresh)
                    DashboardItem.Type.ANNOUNCEMENTS -> {
                        loadSchoolAnnouncements(student, forceRefresh)
                    }
                    DashboardItem.Type.EXAMS -> loadExams(student, forceRefresh)
                    DashboardItem.Type.CONFERENCES -> {
                        loadConferences(student, forceRefresh)
                    }
                    DashboardItem.Type.ADS -> loadAds(forceRefresh)
                    DashboardItem.Type.ADMIN_MESSAGE -> loadAdminMessage(student, forceRefresh)
                }
            }
        }
    }

    fun onSwipeRefresh() {
        Timber.i("Force refreshing the dashboard")
        loadData(preferencesRepository.selectedDashboardTiles, forceRefresh = true)
    }

    fun onRetry() {
        view?.run {
            showErrorView(false)
            showProgress(true)
        }
        loadData(preferencesRepository.selectedDashboardTiles, forceRefresh = true)
    }

    fun onViewReselected() {
        Timber.i("Dashboard view is reselected")
        view?.run {
            resetView()
            popViewToRoot()
        }
    }

    fun onDetailsClick() {
        view?.showErrorDetailsDialog(lastError)
    }

    fun onNotificationsCenterSelected(): Boolean {
        view?.openNotificationsCenterView()
        return true
    }

    fun onDashboardTileSettingsSelected(): Boolean {
        view?.showDashboardTileSettings(preferencesRepository.selectedDashboardTiles.toList())
        return true
    }

    fun onDashboardTileSettingSelected(selectedItems: List<String>) {
        preferencesRepository.selectedDashboardTiles = selectedItems.map {
            DashboardItem.Tile.valueOf(it)
        }.toSet()
    }

    fun onAdminMessageSelected(url: String?) {
        url?.let { view?.openInternetBrowser(it) }
    }

    private fun loadHorizontalGroup(student: Student, forceRefresh: Boolean) {
        flow {
            val selectedTiles = preferencesRepository.selectedDashboardTiles
            val flowSuccess = flowOf(Resource.Success(null))

            val luckyNumberFlow = luckyNumberRepository.getLuckyNumber(student, forceRefresh)
                .mapResourceData {
                    it ?: LuckyNumber(0, LocalDate.now(), 0)
                }
                .onResourceError { errorHandler.dispatch(it) }
                .takeIf { DashboardItem.Tile.LUCKY_NUMBER in selectedTiles } ?: flowSuccess

            val messageFLow = flatResourceFlow {
                val mailbox = messageRepository.getMailboxByStudent(student)

                messageRepository.getMessages(
                    student = student,
                    mailbox = mailbox,
                    folder = MessageFolder.RECEIVED,
                    forceRefresh = forceRefresh
                )
            }
                .onResourceError { errorHandler.dispatch(it) }
                .takeIf { DashboardItem.Tile.MESSAGES in selectedTiles } ?: flowSuccess

            val attendanceFlow = flatResourceFlow {
                val semester = semesterRepository.getCurrentSemester(student)
                attendanceSummaryRepository.getAttendanceSummary(
                    student = student,
                    semester = semester,
                    subjectId = -1,
                    forceRefresh = forceRefresh
                )
            }
                .onResourceError { errorHandler.dispatch(it) }
                .takeIf { DashboardItem.Tile.ATTENDANCE in selectedTiles } ?: flowSuccess

            emitAll(
                combine(
                    flow = luckyNumberFlow,
                    flow2 = messageFLow,
                    flow3 = attendanceFlow,
                ) { luckyNumberResource, messageResource, attendanceResource ->
                    val resList = listOf(luckyNumberResource, messageResource, attendanceResource)

                    DashboardItem.HorizontalGroup(
                        isLoading = resList.any { it is Resource.Loading },
                        error = resList.map { it.errorOrNull }.let { errors ->
                            if (errors.all { it != null }) {
                                errors.firstOrNull()
                            } else null
                        },
                        attendancePercentage = DashboardItem.HorizontalGroup.Cell(
                            data = attendanceResource.dataOrNull?.calculatePercentage(),
                            error = attendanceResource.errorOrNull != null,
                            isLoading = attendanceResource is Resource.Loading,
                        ),
                        unreadMessagesCount = DashboardItem.HorizontalGroup.Cell(
                            data = messageResource.dataOrNull?.count { it.unread },
                            error = messageResource.errorOrNull != null,
                            isLoading = messageResource is Resource.Loading,
                        ),
                        luckyNumber = DashboardItem.HorizontalGroup.Cell(
                            data = luckyNumberResource.dataOrNull?.luckyNumber,
                            error = luckyNumberResource.errorOrNull != null,
                            isLoading = luckyNumberResource is Resource.Loading,
                        )
                    )
                })
        }
            .filterNot { it.isLoading && forceRefresh }
            .distinctUntilChanged()
            .onEach {
                updateData(it, forceRefresh)

                if (it.isLoading) {
                    Timber.i("Loading horizontal group data started")
                } else {
                    firstLoadedItemList += DashboardItem.Type.HORIZONTAL_GROUP
                    Timber.i("Loading horizontal group result: Success")
                }
            }
            .catch {
                Timber.i("Loading horizontal group result: An exception occurred")
                updateData(
                    DashboardItem.HorizontalGroup(error = it),
                    forceRefresh,
                )
                errorHandler.dispatch(it)
            }
            .launch("horizontal_group ${if (forceRefresh) "-forceRefresh" else ""}")
    }

    private fun loadGrades(student: Student, forceRefresh: Boolean) {
        flatResourceFlow {
            val semester = semesterRepository.getCurrentSemester(student)

            gradeRepository.getGrades(student, semester, forceRefresh)
        }
            .mapResourceData { (details, _) ->
                val filteredSubjectWithGrades = details
                    .filter { it.date >= LocalDate.now().minusDays(7) }
                    .groupBy { it.subject }
                    .mapValues { entry ->
                        entry.value
                            .take(5)
                            .sortedByDescending { it.date }
                    }
                    .toList()
                    .sortedByDescending { (_, grades) -> grades[0].date }
                    .toMap()

                filteredSubjectWithGrades
            }
            .onEach {
                when (it) {
                    is Resource.Loading -> {
                        Timber.i("Loading dashboard grades data started")
                        if (forceRefresh) return@onEach
                        updateData(
                            DashboardItem.Grades(
                                subjectWithGrades = it.dataOrNull,
                                gradeTheme = preferencesRepository.gradeColorTheme,
                                isLoading = true
                            ), forceRefresh
                        )

                        if (!it.dataOrNull.isNullOrEmpty()) {
                            firstLoadedItemList += DashboardItem.Type.GRADES
                        }
                    }
                    is Resource.Success -> {
                        Timber.i("Loading dashboard grades result: Success")
                        updateData(
                            DashboardItem.Grades(
                                subjectWithGrades = it.data,
                                gradeTheme = preferencesRepository.gradeColorTheme
                            ),
                            forceRefresh
                        )
                    }
                    is Resource.Error -> {
                        Timber.i("Loading dashboard grades result: An exception occurred")
                        errorHandler.dispatch(it.error)
                        updateData(DashboardItem.Grades(error = it.error), forceRefresh)
                    }
                }
            }
            .launchWithUniqueRefreshJob("dashboard_grades", forceRefresh)
    }

    private fun loadLessons(student: Student, forceRefresh: Boolean) {
        flatResourceFlow {
            val semester = semesterRepository.getCurrentSemester(student)
            val date = LocalDate.now().nextOrSameSchoolDay

            timetableRepository.getTimetable(
                student = student,
                semester = semester,
                start = date,
                end = date.plusDays(1),
                forceRefresh = forceRefresh
            )
        }
            .onEach {
                when (it) {
                    is Resource.Loading -> {
                        Timber.i("Loading dashboard lessons data started")
                        if (forceRefresh) return@onEach
                        updateData(
                            DashboardItem.Lessons(it.dataOrNull, isLoading = true),
                            forceRefresh
                        )

                        if (!it.dataOrNull?.lessons.isNullOrEmpty()) {
                            firstLoadedItemList += DashboardItem.Type.LESSONS
                        }
                    }
                    is Resource.Success -> {
                        Timber.i("Loading dashboard lessons result: Success")
                        updateData(
                            DashboardItem.Lessons(it.data), forceRefresh
                        )
                    }
                    is Resource.Error -> {
                        Timber.i("Loading dashboard lessons result: An exception occurred")
                        errorHandler.dispatch(it.error)
                        updateData(
                            DashboardItem.Lessons(error = it.error), forceRefresh
                        )
                    }
                }
            }
            .launchWithUniqueRefreshJob("dashboard_lessons", forceRefresh)
    }

    private fun loadHomework(student: Student, forceRefresh: Boolean) {
        flatResourceFlow {
            val semester = semesterRepository.getCurrentSemester(student)
            val date = LocalDate.now().nextOrSameSchoolDay

            homeworkRepository.getHomework(
                student = student,
                semester = semester,
                start = date,
                end = date,
                forceRefresh = forceRefresh
            )
        }
            .mapResourceData { homework ->
                val currentDate = LocalDate.now()

                val filteredHomework = homework.filter {
                    (it.date.isAfter(currentDate) || it.date == currentDate) && !it.isDone
                }.sortedBy { it.date }

                filteredHomework
            }
            .onEach {
                when (it) {
                    is Resource.Loading -> {
                        Timber.i("Loading dashboard homework data started")
                        if (forceRefresh) return@onEach
                        val data = it.dataOrNull.orEmpty()
                        updateData(
                            DashboardItem.Homework(data, isLoading = true),
                            forceRefresh
                        )

                        if (data.isNotEmpty()) {
                            firstLoadedItemList += DashboardItem.Type.HOMEWORK
                        }
                    }
                    is Resource.Success -> {
                        Timber.i("Loading dashboard homework result: Success")
                        updateData(DashboardItem.Homework(it.data), forceRefresh)
                    }
                    is Resource.Error -> {
                        Timber.i("Loading dashboard homework result: An exception occurred")
                        errorHandler.dispatch(it.error)
                        updateData(DashboardItem.Homework(error = it.error), forceRefresh)
                    }
                }
            }
            .launchWithUniqueRefreshJob("dashboard_homework", forceRefresh)
    }

    private fun loadSchoolAnnouncements(student: Student, forceRefresh: Boolean) {
        flatResourceFlow {
            schoolAnnouncementRepository.getSchoolAnnouncements(student, forceRefresh)
        }
            .onEach {
                when (it) {
                    is Resource.Loading -> {
                        Timber.i("Loading dashboard announcements data started")
                        if (forceRefresh) return@onEach
                        updateData(
                            DashboardItem.Announcements(it.dataOrNull.orEmpty(), isLoading = true),
                            forceRefresh
                        )

                        if (!it.dataOrNull.isNullOrEmpty()) {
                            firstLoadedItemList += DashboardItem.Type.ANNOUNCEMENTS
                        }
                    }
                    is Resource.Success -> {
                        Timber.i("Loading dashboard announcements result: Success")
                        updateData(DashboardItem.Announcements(it.data), forceRefresh)
                    }
                    is Resource.Error -> {
                        Timber.i("Loading dashboard announcements result: An exception occurred")
                        errorHandler.dispatch(it.error)
                        updateData(DashboardItem.Announcements(error = it.error), forceRefresh)
                    }
                }
            }
            .launchWithUniqueRefreshJob("dashboard_announcements", forceRefresh)
    }

    private fun loadExams(student: Student, forceRefresh: Boolean) {
        flatResourceFlow {
            val semester = semesterRepository.getCurrentSemester(student)

            examRepository.getExams(
                student = student,
                semester = semester,
                start = LocalDate.now(),
                end = LocalDate.now().plusDays(7),
                forceRefresh = forceRefresh
            )
        }
            .mapResourceData { exams -> exams.sortedBy { exam -> exam.date } }
            .onEach {
                when (it) {
                    is Resource.Loading -> {
                        Timber.i("Loading dashboard exams data started")
                        if (forceRefresh) return@onEach
                        updateData(
                            DashboardItem.Exams(it.dataOrNull.orEmpty(), isLoading = true),
                            forceRefresh
                        )

                        if (!it.dataOrNull.isNullOrEmpty()) {
                            firstLoadedItemList += DashboardItem.Type.EXAMS
                        }
                    }
                    is Resource.Success -> {
                        Timber.i("Loading dashboard exams result: Success")
                        updateData(DashboardItem.Exams(it.data), forceRefresh)
                    }
                    is Resource.Error -> {
                        Timber.i("Loading dashboard exams result: An exception occurred")
                        errorHandler.dispatch(it.error)
                        updateData(DashboardItem.Exams(error = it.error), forceRefresh)
                    }
                }
            }
            .launchWithUniqueRefreshJob("dashboard_exams", forceRefresh)
    }

    private fun loadConferences(student: Student, forceRefresh: Boolean) {
        flatResourceFlow {
            val semester = semesterRepository.getCurrentSemester(student)

            conferenceRepository.getConferences(
                student = student,
                semester = semester,
                forceRefresh = forceRefresh,
                startDate = Instant.now(),
            )
        }
            .onEach {
                when (it) {
                    is Resource.Loading -> {
                        Timber.i("Loading dashboard conferences data started")
                        if (forceRefresh) return@onEach
                        updateData(
                            DashboardItem.Conferences(it.dataOrNull.orEmpty(), isLoading = true),
                            forceRefresh
                        )

                        if (!it.dataOrNull.isNullOrEmpty()) {
                            firstLoadedItemList += DashboardItem.Type.CONFERENCES
                        }
                    }
                    is Resource.Success -> {
                        Timber.i("Loading dashboard conferences result: Success")
                        updateData(DashboardItem.Conferences(it.data), forceRefresh)
                    }
                    is Resource.Error -> {
                        Timber.i("Loading dashboard conferences result: An exception occurred")
                        errorHandler.dispatch(it.error)
                        updateData(DashboardItem.Conferences(error = it.error), forceRefresh)
                    }
                }
            }
            .launchWithUniqueRefreshJob("dashboard_conferences", forceRefresh)
    }

    private fun loadAdminMessage(student: Student, forceRefresh: Boolean) {
        flatResourceFlow { adminMessageRepository.getAdminMessages(student) }
            .filter {
                val data = it.dataOrNull ?: return@filter true
                val isDismissed = data.id in preferencesRepository.dismissedAdminMessageIds
                !isDismissed
            }
            .onEach {
                when (it) {
                    is Resource.Loading -> {
                        Timber.i("Loading dashboard admin message data started")
                        if (forceRefresh) return@onEach
                        updateData(DashboardItem.AdminMessages(), forceRefresh)
                    }
                    is Resource.Success -> {
                        Timber.i("Loading dashboard admin message result: Success")
                        updateData(
                            dashboardItem = DashboardItem.AdminMessages(adminMessage = it.data),
                            forceRefresh = forceRefresh
                        )
                    }
                    is Resource.Error -> {
                        Timber.i("Loading dashboard admin message result: An exception occurred")
                        errorHandler.dispatch(it.error)
                        updateData(
                            dashboardItem = DashboardItem.AdminMessages(
                                adminMessage = null,
                                error = it.error
                            ),
                            forceRefresh = forceRefresh
                        )
                    }
                }
            }
            .launchWithUniqueRefreshJob("dashboard_admin_messages", forceRefresh)
    }

    private fun loadAds(forceRefresh: Boolean) {
        presenterScope.launch {
            if (!forceRefresh) {
                updateData(DashboardItem.Ads(), forceRefresh)
            }

            val dashboardAdItem =
                runCatching {
                    DashboardItem.Ads(adsHelper.getDashboardTileAdBanner(view!!.tileWidth))
                }
                    .onFailure { Timber.e(it) }
                    .getOrElse { DashboardItem.Ads(error = it) }

            updateData(dashboardAdItem, forceRefresh)
        }
    }

    private fun updateData(dashboardItem: DashboardItem, forceRefresh: Boolean) {
        val isForceRefreshError = forceRefresh && dashboardItem.error != null
        val isFirstRunDataLoadedError =
            dashboardItem.type in firstLoadedItemList && dashboardItem.error != null

        with(dashboardItemLoadedList) {
            removeAll { it.type == dashboardItem.type && !isForceRefreshError && !isFirstRunDataLoadedError }
            if (!isForceRefreshError && !isFirstRunDataLoadedError) add(dashboardItem)
        }

        sortDashboardItems()

        if (dashboardItem is DashboardItem.AdminMessages) {
            if (!dashboardItem.isDataLoaded) {
                dashboardItemsToLoad = dashboardItemsToLoad - DashboardItem.Type.ADMIN_MESSAGE
                dashboardTileLoadedList = dashboardTileLoadedList - DashboardItem.Tile.ADMIN_MESSAGE

                dashboardItemLoadedList.removeAll { it.type == DashboardItem.Type.ADMIN_MESSAGE }
            } else {
                dashboardItemsToLoad = dashboardItemsToLoad + DashboardItem.Type.ADMIN_MESSAGE
                dashboardTileLoadedList = dashboardTileLoadedList + DashboardItem.Tile.ADMIN_MESSAGE
            }
        }

        if (dashboardItem is DashboardItem.Ads) {
            if (!dashboardItem.isDataLoaded) {
                dashboardItemsToLoad = dashboardItemsToLoad - DashboardItem.Type.ADS
                dashboardTileLoadedList = dashboardTileLoadedList - DashboardItem.Tile.ADS

                dashboardItemLoadedList.removeAll { it.type == DashboardItem.Type.ADS }
            } else {
                dashboardItemsToLoad = dashboardItemsToLoad + DashboardItem.Type.ADS
                dashboardTileLoadedList = dashboardTileLoadedList + DashboardItem.Tile.ADS
            }
        }

        if (forceRefresh) {
            updateForceRefreshData(dashboardItem)
        } else {
            updateNormalData()
        }
    }

    private fun updateNormalData() {
        val isItemsLoaded =
            dashboardItemsToLoad.all { type -> dashboardItemLoadedList.any { it.type == type } }
        val isItemsDataLoaded = isItemsLoaded && dashboardItemLoadedList.all {
            it.isDataLoaded || it.error != null
        }

        if (isItemsDataLoaded) {
            view?.run {
                showProgress(false)
                showErrorView(false)
                showContent(true)
                updateData(dashboardItemLoadedList.toList())
            }
        }

        showErrorIfExists(
            isItemsLoaded = isItemsLoaded,
            itemsLoadedList = dashboardItemLoadedList,
            forceRefresh = false
        )
    }

    private fun updateForceRefreshData(dashboardItem: DashboardItem) {
        val isNotLoadedAdminMessage =
            dashboardItem is DashboardItem.AdminMessages && !dashboardItem.isDataLoaded

        with(dashboardItemRefreshLoadedList) {
            removeAll { it.type == dashboardItem.type }
            if (!isNotLoadedAdminMessage) add(dashboardItem)
        }

        val isRefreshItemLoaded =
            dashboardItemsToLoad.all { type -> dashboardItemRefreshLoadedList.any { it.type == type } }
        val isRefreshItemsDataLoaded = isRefreshItemLoaded && dashboardItemRefreshLoadedList.all {
            it.isDataLoaded || it.error != null
        }

        if (isRefreshItemsDataLoaded) {
            view?.run {
                showRefresh(false)
                showErrorView(false)
                showContent(true)
                updateData(dashboardItemLoadedList.toList())
            }
        }

        showErrorIfExists(
            isItemsLoaded = isRefreshItemLoaded,
            itemsLoadedList = dashboardItemRefreshLoadedList,
            forceRefresh = true
        )

        if (isRefreshItemsDataLoaded) dashboardItemRefreshLoadedList.clear()
    }

    private fun showErrorIfExists(
        isItemsLoaded: Boolean,
        itemsLoadedList: List<DashboardItem>,
        forceRefresh: Boolean
    ) {
        val filteredItems = itemsLoadedList.filterNot {
            it.type == DashboardItem.Type.ACCOUNT || it.type == DashboardItem.Type.ADMIN_MESSAGE
        }
        val isAccountItemError =
            itemsLoadedList.find { it.type == DashboardItem.Type.ACCOUNT }?.error != null
        val isGeneralError =
            filteredItems.none { it.error == null } && filteredItems.isNotEmpty() || isAccountItemError
        val firstError = itemsLoadedList.mapNotNull { it.error }.firstOrNull()

        val filteredOriginalLoadedList =
            dashboardItemLoadedList.filterNot { it.type == DashboardItem.Type.ACCOUNT }
        val wasAccountItemError =
            dashboardItemLoadedList.find { it.type == DashboardItem.Type.ACCOUNT }?.error != null
        val wasGeneralError =
            filteredOriginalLoadedList.none { it.error == null } && filteredOriginalLoadedList.isNotEmpty() || wasAccountItemError

        if (isGeneralError && isItemsLoaded) {
            lastError = requireNotNull(firstError)

            view?.run {
                showProgress(false)
                showRefresh(false)
                if ((forceRefresh && wasGeneralError) || !forceRefresh) {
                    showContent(false)
                    showErrorView(true)
                    setErrorDetails(lastError)
                }
            }
        }
    }

    private fun sortDashboardItems() {
        val dashboardItemsPosition = preferencesRepository.dashboardItemsPosition

        dashboardItemLoadedList.sortBy { tile ->
            val defaultPosition = if (tile is DashboardItem.AdminMessages) {
                -1
            } else {
                tile.type.ordinal + 100
            }

            dashboardItemsPosition?.getOrDefault(tile.type, defaultPosition) ?: tile.type.ordinal
        }
    }

    private fun Flow<Resource<*>>.launchWithUniqueRefreshJob(name: String, forceRefresh: Boolean) {
        val jobName = if (forceRefresh) "$name-forceRefresh" else name

        if (forceRefresh) {
            onEach {
                if (it is Resource.Success) {
                    cancelJobs(jobName)
                }
            }.launch(jobName)
        } else {
            launch(jobName)
        }
    }
}
