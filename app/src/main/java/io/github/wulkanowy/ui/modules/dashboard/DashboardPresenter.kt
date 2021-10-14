package io.github.wulkanowy.ui.modules.dashboard

import io.github.wulkanowy.data.Resource
import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.db.entities.LuckyNumber
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.enums.MessageFolder
import io.github.wulkanowy.data.repositories.AdminMessageRepository
import io.github.wulkanowy.data.repositories.AttendanceSummaryRepository
import io.github.wulkanowy.data.repositories.ConferenceRepository
import io.github.wulkanowy.data.repositories.ExamRepository
import io.github.wulkanowy.data.repositories.GradeRepository
import io.github.wulkanowy.data.repositories.HomeworkRepository
import io.github.wulkanowy.data.repositories.LuckyNumberRepository
import io.github.wulkanowy.data.repositories.MessageRepository
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.SchoolAnnouncementRepository
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.data.repositories.TimetableRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.calculatePercentage
import io.github.wulkanowy.utils.flowWithResourceIn
import io.github.wulkanowy.utils.nextOrSameSchoolDay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
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
    private val adminMessageRepository: AdminMessageRepository
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

        preferencesRepository.selectedDashboardTilesFlow
            .onEach { loadData(tilesToLoad = it) }
            .launch("dashboard_pref")
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
                    DashboardItem.Type.ADS -> TODO()
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
            val semester = semesterRepository.getCurrentSemester(student)
            val selectedTiles = preferencesRepository.selectedDashboardTiles

            val luckyNumberFlow = luckyNumberRepository.getLuckyNumber(student, forceRefresh)
                .map {
                    if (it.data == null) {
                        it.copy(data = LuckyNumber(0, LocalDate.now(), 0))
                    } else it
                }
                .takeIf { DashboardItem.Tile.LUCKY_NUMBER in selectedTiles } ?: flowOf(null)

            val messageFLow = messageRepository.getMessages(
                student = student,
                semester = semester,
                folder = MessageFolder.RECEIVED,
                forceRefresh = forceRefresh
            ).takeIf { DashboardItem.Tile.MESSAGES in selectedTiles } ?: flowOf(null)

            val attendanceFlow = attendanceSummaryRepository.getAttendanceSummary(
                student = student,
                semester = semester,
                subjectId = -1,
                forceRefresh = forceRefresh
            ).takeIf { DashboardItem.Tile.ATTENDANCE in selectedTiles } ?: flowOf(null)

            emitAll(
                combine(
                    luckyNumberFlow,
                    messageFLow,
                    attendanceFlow
                ) { luckyNumberResource, messageResource, attendanceResource ->
                    val error =
                        luckyNumberResource?.error ?: messageResource?.error ?: attendanceResource?.error
                    error?.let { throw it }

                    val luckyNumber = luckyNumberResource?.data?.luckyNumber
                    val messageCount = messageResource?.data?.count { it.unread }
                    val attendancePercentage = attendanceResource?.data?.calculatePercentage()

                    val isLoading =
                        luckyNumberResource?.status == Status.LOADING || messageResource?.status == Status.LOADING || attendanceResource?.status == Status.LOADING

                    DashboardItem.HorizontalGroup(
                        isLoading = isLoading,
                        attendancePercentage = if (attendancePercentage == 0.0 && isLoading) -1.0 else attendancePercentage,
                        unreadMessagesCount = if (messageCount == 0 && isLoading) -1 else messageCount,
                        luckyNumber = if (luckyNumber == 0 && isLoading) -1 else luckyNumber
                    )
                })
        }
            .filterNot { it.isLoading && forceRefresh }
            .distinctUntilChanged()
            .onEach {
                updateData(it, forceRefresh)

                if (it.isLoading) {
                    Timber.i("Loading horizontal group data started")

                    if (it.isFullDataLoaded) {
                        firstLoadedItemList += DashboardItem.Type.HORIZONTAL_GROUP
                    }
                } else {
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
            .launch("horizontal_group")
    }

    private fun loadGrades(student: Student, forceRefresh: Boolean) {
        flowWithResourceIn {
            val semester = semesterRepository.getCurrentSemester(student)

            gradeRepository.getGrades(student, semester, forceRefresh)
        }.map { originalResource ->
            val filteredSubjectWithGrades = originalResource.data?.first.orEmpty()
                .filter { grade ->
                    grade.date.isAfter(LocalDate.now().minusDays(7))
                }
                .groupBy { grade -> grade.subject }
                .mapValues { entry ->
                    entry.value
                        .take(5)
                        .sortedBy { grade -> grade.date }
                }
                .toList()
                .sortedBy { subjectWithGrades -> subjectWithGrades.second[0].date }
                .toMap()

            Resource(
                status = originalResource.status,
                data = filteredSubjectWithGrades.takeIf { originalResource.data != null },
                error = originalResource.error
            )
        }.onEach {
            when (it.status) {
                Status.LOADING -> {
                    Timber.i("Loading dashboard grades data started")
                    if (forceRefresh) return@onEach

                    updateData(
                        DashboardItem.Grades(
                            subjectWithGrades = it.data,
                            gradeTheme = preferencesRepository.gradeColorTheme,
                            isLoading = true
                        ), forceRefresh
                    )

                    if (!it.data.isNullOrEmpty()) {
                        firstLoadedItemList += DashboardItem.Type.GRADES
                    }
                }
                Status.SUCCESS -> {
                    Timber.i("Loading dashboard grades result: Success")
                    updateData(
                        DashboardItem.Grades(
                            subjectWithGrades = it.data,
                            gradeTheme = preferencesRepository.gradeColorTheme
                        ),
                        forceRefresh
                    )
                }
                Status.ERROR -> {
                    Timber.i("Loading dashboard grades result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                    updateData(DashboardItem.Grades(error = it.error), forceRefresh)
                }
            }
        }.launch("dashboard_grades")
    }

    private fun loadLessons(student: Student, forceRefresh: Boolean) {
        flowWithResourceIn {
            val semester = semesterRepository.getCurrentSemester(student)
            val date = LocalDate.now().nextOrSameSchoolDay

            timetableRepository.getTimetable(
                student = student,
                semester = semester,
                start = date,
                end = date.plusDays(1),
                forceRefresh = forceRefresh
            )

        }.onEach {
            when (it.status) {
                Status.LOADING -> {
                    Timber.i("Loading dashboard lessons data started")
                    if (forceRefresh) return@onEach
                    updateData(
                        DashboardItem.Lessons(it.data, isLoading = true),
                        forceRefresh
                    )

                    if (!it.data?.lessons.isNullOrEmpty()) {
                        firstLoadedItemList += DashboardItem.Type.LESSONS
                    }
                }
                Status.SUCCESS -> {
                    Timber.i("Loading dashboard lessons result: Success")
                    updateData(
                        DashboardItem.Lessons(it.data), forceRefresh
                    )
                }
                Status.ERROR -> {
                    Timber.i("Loading dashboard lessons result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                    updateData(
                        DashboardItem.Lessons(error = it.error), forceRefresh
                    )
                }
            }
        }.launch("dashboard_lessons")
    }

    private fun loadHomework(student: Student, forceRefresh: Boolean) {
        flowWithResourceIn {
            val semester = semesterRepository.getCurrentSemester(student)
            val date = LocalDate.now().nextOrSameSchoolDay

            homeworkRepository.getHomework(
                student = student,
                semester = semester,
                start = date,
                end = date,
                forceRefresh = forceRefresh
            )
        }.map { homeworkResource ->
            val currentDate = LocalDate.now()

            val filteredHomework = homeworkResource.data?.filter {
                (it.date.isAfter(currentDate) || it.date == currentDate) && !it.isDone
            }

            homeworkResource.copy(data = filteredHomework)
        }.onEach {
            when (it.status) {
                Status.LOADING -> {
                    Timber.i("Loading dashboard homework data started")
                    if (forceRefresh) return@onEach
                    updateData(
                        DashboardItem.Homework(it.data ?: emptyList(), isLoading = true),
                        forceRefresh
                    )

                    if (!it.data.isNullOrEmpty()) {
                        firstLoadedItemList += DashboardItem.Type.HOMEWORK
                    }
                }
                Status.SUCCESS -> {
                    Timber.i("Loading dashboard homework result: Success")
                    updateData(DashboardItem.Homework(it.data ?: emptyList()), forceRefresh)
                }
                Status.ERROR -> {
                    Timber.i("Loading dashboard homework result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                    updateData(DashboardItem.Homework(error = it.error), forceRefresh)
                }
            }
        }.launch("dashboard_homework")
    }

    private fun loadSchoolAnnouncements(student: Student, forceRefresh: Boolean) {
        flowWithResourceIn {
            schoolAnnouncementRepository.getSchoolAnnouncements(student, forceRefresh)
        }.onEach {
            when (it.status) {
                Status.LOADING -> {
                    Timber.i("Loading dashboard announcements data started")
                    if (forceRefresh) return@onEach
                    updateData(
                        DashboardItem.Announcements(it.data ?: emptyList(), isLoading = true),
                        forceRefresh
                    )

                    if (!it.data.isNullOrEmpty()) {
                        firstLoadedItemList += DashboardItem.Type.ANNOUNCEMENTS
                    }
                }
                Status.SUCCESS -> {
                    Timber.i("Loading dashboard announcements result: Success")
                    updateData(DashboardItem.Announcements(it.data ?: emptyList()), forceRefresh)
                }
                Status.ERROR -> {
                    Timber.i("Loading dashboard announcements result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                    updateData(DashboardItem.Announcements(error = it.error), forceRefresh)
                }
            }
        }.launch("dashboard_announcements")
    }

    private fun loadExams(student: Student, forceRefresh: Boolean) {
        flowWithResourceIn {
            val semester = semesterRepository.getCurrentSemester(student)

            examRepository.getExams(
                student = student,
                semester = semester,
                start = LocalDate.now(),
                end = LocalDate.now().plusDays(7),
                forceRefresh = forceRefresh
            )
        }
            .map { examResource ->
                val sortedExams = examResource.data?.sortedBy { it.date }

                examResource.copy(data = sortedExams)
            }
            .onEach {
                when (it.status) {
                    Status.LOADING -> {
                        Timber.i("Loading dashboard exams data started")
                        if (forceRefresh) return@onEach
                        updateData(
                            DashboardItem.Exams(it.data.orEmpty(), isLoading = true),
                            forceRefresh
                        )

                        if (!it.data.isNullOrEmpty()) {
                            firstLoadedItemList += DashboardItem.Type.EXAMS
                        }
                    }
                    Status.SUCCESS -> {
                        Timber.i("Loading dashboard exams result: Success")
                        updateData(DashboardItem.Exams(it.data ?: emptyList()), forceRefresh)
                    }
                    Status.ERROR -> {
                        Timber.i("Loading dashboard exams result: An exception occurred")
                        errorHandler.dispatch(it.error!!)
                        updateData(DashboardItem.Exams(error = it.error), forceRefresh)
                    }
                }
            }.launch("dashboard_exams")
    }

    private fun loadConferences(student: Student, forceRefresh: Boolean) {
        flowWithResourceIn {
            val semester = semesterRepository.getCurrentSemester(student)

            conferenceRepository.getConferences(
                student = student,
                semester = semester,
                forceRefresh = forceRefresh,
                startDate = LocalDateTime.now()
            )
        }.onEach {
            when (it.status) {
                Status.LOADING -> {
                    Timber.i("Loading dashboard conferences data started")
                    if (forceRefresh) return@onEach
                    updateData(
                        DashboardItem.Conferences(it.data ?: emptyList(), isLoading = true),
                        forceRefresh
                    )

                    if (!it.data.isNullOrEmpty()) {
                        firstLoadedItemList += DashboardItem.Type.CONFERENCES
                    }
                }
                Status.SUCCESS -> {
                    Timber.i("Loading dashboard conferences result: Success")
                    updateData(DashboardItem.Conferences(it.data ?: emptyList()), forceRefresh)
                }
                Status.ERROR -> {
                    Timber.i("Loading dashboard conferences result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                    updateData(DashboardItem.Conferences(error = it.error), forceRefresh)
                }
            }
        }.launch("dashboard_conferences")
    }

    private fun loadAdminMessage(student: Student, forceRefresh: Boolean) {
        flowWithResourceIn { adminMessageRepository.getAdminMessages(student, forceRefresh) }
            .onEach {
                when (it.status) {
                    Status.LOADING -> {
                        Timber.i("Loading dashboard admin message data started")
                        if (forceRefresh) return@onEach
                        updateData(DashboardItem.AdminMessages(), forceRefresh)
                    }
                    Status.SUCCESS -> {
                        Timber.i("Loading dashboard admin message result: Success")
                        updateData(
                            dashboardItem = DashboardItem.AdminMessages(adminMessage = it.data),
                            forceRefresh = forceRefresh
                        )
                    }
                    Status.ERROR -> {
                        Timber.i("Loading dashboard admin message result: An exception occurred")
                        errorHandler.dispatch(it.error!!)
                        updateData(
                            dashboardItem = DashboardItem.AdminMessages(
                                adminMessage = it.data,
                                error = it.error
                            ),
                            forceRefresh = forceRefresh
                        )
                    }
                }
            }
            .launch("dashboard_admin_messages")
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

        if (dashboardItem is DashboardItem.AdminMessages && !dashboardItem.isDataLoaded) {
            dashboardItemsToLoad = dashboardItemsToLoad - DashboardItem.Type.ADMIN_MESSAGE
            dashboardTileLoadedList = dashboardTileLoadedList - DashboardItem.Tile.ADMIN_MESSAGE

            dashboardItemLoadedList.removeAll { it.type == DashboardItem.Type.ADMIN_MESSAGE }
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
        val errorMessage = itemsLoadedList.map { it.error?.stackTraceToString() }.toString()

        val filteredOriginalLoadedList =
            dashboardItemLoadedList.filterNot { it.type == DashboardItem.Type.ACCOUNT }
        val wasAccountItemError =
            dashboardItemLoadedList.find { it.type == DashboardItem.Type.ACCOUNT }?.error != null
        val wasGeneralError =
            filteredOriginalLoadedList.none { it.error == null } && filteredOriginalLoadedList.isNotEmpty() || wasAccountItemError

        if (isGeneralError && isItemsLoaded) {
            lastError = Exception(errorMessage)

            view?.run {
                showProgress(false)
                showRefresh(false)
                if ((forceRefresh && wasGeneralError) || !forceRefresh) {
                    showContent(false)
                    showErrorView(true)
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
}