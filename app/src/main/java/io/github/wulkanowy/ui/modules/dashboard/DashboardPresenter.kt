package io.github.wulkanowy.ui.modules.dashboard

import io.github.wulkanowy.data.Resource
import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.enums.MessageFolder
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
import io.github.wulkanowy.utils.flowWithResource
import io.github.wulkanowy.utils.flowWithResourceIn
import io.github.wulkanowy.utils.nextOrSameSchoolDay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
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
    private val schoolAnnouncementRepository: SchoolAnnouncementRepository
) : BasePresenter<DashboardView>(errorHandler, studentRepository) {

    private val dashboardItemLoadedList = mutableListOf<DashboardItem>()

    private val dashboardItemRefreshLoadedList = mutableListOf<DashboardItem>()

    private lateinit var dashboardItemsToLoad: Set<DashboardItem.Type>

    private var dashboardTilesToLoad: Set<DashboardItem.Tile> = emptySet()

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

    fun loadData(forceRefresh: Boolean = false, tilesToLoad: Set<DashboardItem.Tile>) {
        val oldDashboardDataToLoad = dashboardTilesToLoad

        dashboardTilesToLoad = tilesToLoad
        dashboardItemsToLoad = dashboardTilesToLoad.map { it.toDashboardItemType() }.toSet()

        removeUnselectedTiles()

        val newTileList = generateTileListToLoad(oldDashboardDataToLoad, forceRefresh)
        loadTiles(forceRefresh, newTileList)
    }

    private fun removeUnselectedTiles() {
        val isLuckyNumberToLoad =
            dashboardTilesToLoad.any { it == DashboardItem.Tile.LUCKY_NUMBER }
        val isMessagesToLoad =
            dashboardTilesToLoad.any { it == DashboardItem.Tile.MESSAGES }
        val isAttendanceToLoad =
            dashboardTilesToLoad.any { it == DashboardItem.Tile.ATTENDANCE }

        dashboardItemLoadedList.removeAll { loadedTile -> dashboardItemsToLoad.none { it == loadedTile.type } }

        val horizontalGroup =
            dashboardItemLoadedList.find { it is DashboardItem.HorizontalGroup } as DashboardItem.HorizontalGroup?

        if (horizontalGroup != null) {
            val horizontalIndex = dashboardItemLoadedList.indexOf(horizontalGroup)
            dashboardItemLoadedList.remove(horizontalGroup)

            var updatedHorizontalGroup = horizontalGroup

            if (horizontalGroup.luckyNumber != null && !isLuckyNumberToLoad) {
                updatedHorizontalGroup = updatedHorizontalGroup.copy(luckyNumber = null)
            }

            if (horizontalGroup.attendancePercentage != null && !isAttendanceToLoad) {
                updatedHorizontalGroup = updatedHorizontalGroup.copy(attendancePercentage = null)
            }

            if (horizontalGroup.unreadMessagesCount != null && !isMessagesToLoad) {
                updatedHorizontalGroup = updatedHorizontalGroup.copy(unreadMessagesCount = null)
            }

            if (horizontalGroup.error != null) {
                updatedHorizontalGroup = updatedHorizontalGroup.copy(error = null, isLoading = true)
            }

            dashboardItemLoadedList.add(horizontalIndex, updatedHorizontalGroup)
        }

        view?.updateData(dashboardItemLoadedList)
    }

    private fun loadTiles(forceRefresh: Boolean, tileList: List<DashboardItem.Tile>) {
        tileList.forEach {
            when (it) {
                DashboardItem.Tile.ACCOUNT -> loadCurrentAccount(forceRefresh)
                DashboardItem.Tile.LUCKY_NUMBER -> loadLuckyNumber(forceRefresh)
                DashboardItem.Tile.MESSAGES -> loadMessages(forceRefresh)
                DashboardItem.Tile.ATTENDANCE -> loadAttendance(forceRefresh)
                DashboardItem.Tile.LESSONS -> loadLessons(forceRefresh)
                DashboardItem.Tile.GRADES -> loadGrades(forceRefresh)
                DashboardItem.Tile.HOMEWORK -> loadHomework(forceRefresh)
                DashboardItem.Tile.ANNOUNCEMENTS -> loadSchoolAnnouncements(forceRefresh)
                DashboardItem.Tile.EXAMS -> loadExams(forceRefresh)
                DashboardItem.Tile.CONFERENCES -> loadConferences(forceRefresh)
                DashboardItem.Tile.ADS -> TODO()
            }
        }
    }

    private fun generateTileListToLoad(
        oldDashboardTileToLoad: Set<DashboardItem.Tile>,
        forceRefresh: Boolean
    ) = dashboardTilesToLoad.filter { newTileToLoad ->
        oldDashboardTileToLoad.none { it == newTileToLoad } || forceRefresh
    }

    fun onSwipeRefresh() {
        Timber.i("Force refreshing the dashboard")
        loadData(true, preferencesRepository.selectedDashboardTiles)
    }

    fun onRetry() {
        view?.run {
            showErrorView(false)
            showProgress(true)
        }
        loadData(true, preferencesRepository.selectedDashboardTiles)
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

    fun onDashboardTileSettingsSelected(): Boolean {
        view?.showDashboardTileSettings(preferencesRepository.selectedDashboardTiles.toList())
        return true
    }

    fun onDashboardTileSettingSelected(selectedItems: List<String>) {
        preferencesRepository.selectedDashboardTiles = selectedItems.map {
            DashboardItem.Tile.valueOf(it)
        }.toSet()
    }

    private fun loadCurrentAccount(forceRefresh: Boolean) {
        flowWithResource { studentRepository.getCurrentStudent(false) }
            .onEach {
                when (it.status) {
                    Status.LOADING -> {
                        Timber.i("Loading dashboard account data started")
                        if (forceRefresh) return@onEach
                        updateData(DashboardItem.Account(it.data, isLoading = true), forceRefresh)
                    }
                    Status.SUCCESS -> {
                        Timber.i("Loading dashboard account result: Success")
                        updateData(DashboardItem.Account(it.data), forceRefresh)
                    }
                    Status.ERROR -> {
                        Timber.i("Loading dashboard account result: An exception occurred")
                        errorHandler.dispatch(it.error!!)
                        updateData(DashboardItem.Account(error = it.error), forceRefresh)
                    }
                }
            }
            .launch("dashboard_account")
    }

    private fun loadLuckyNumber(forceRefresh: Boolean) {
        flowWithResourceIn {
            val student = studentRepository.getCurrentStudent(true)

            luckyNumberRepository.getLuckyNumber(student, forceRefresh)
        }.onEach {
            when (it.status) {
                Status.LOADING -> {
                    Timber.i("Loading dashboard lucky number data started")
                    if (forceRefresh) return@onEach
                    processHorizontalGroupData(
                        luckyNumber = it.data?.luckyNumber,
                        isLoading = true,
                        forceRefresh = forceRefresh
                    )
                }
                Status.SUCCESS -> {
                    Timber.i("Loading dashboard lucky number result: Success")
                    processHorizontalGroupData(
                        luckyNumber = it.data?.luckyNumber ?: -1,
                        forceRefresh = forceRefresh
                    )
                }
                Status.ERROR -> {
                    Timber.i("Loading dashboard lucky number result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                    processHorizontalGroupData(error = it.error, forceRefresh = forceRefresh)
                }
            }
        }.launch("dashboard_lucky_number")
    }

    private fun loadMessages(forceRefresh: Boolean) {
        flowWithResourceIn {
            val student = studentRepository.getCurrentStudent(true)
            val semester = semesterRepository.getCurrentSemester(student)

            messageRepository.getMessages(student, semester, MessageFolder.RECEIVED, forceRefresh)
        }.onEach {
            when (it.status) {
                Status.LOADING -> {
                    Timber.i("Loading dashboard messages data started")
                    if (forceRefresh) return@onEach
                    val unreadMessagesCount = it.data?.count { message -> message.unread }

                    processHorizontalGroupData(
                        unreadMessagesCount = unreadMessagesCount,
                        isLoading = true,
                        forceRefresh = forceRefresh
                    )
                }
                Status.SUCCESS -> {
                    Timber.i("Loading dashboard messages result: Success")
                    val unreadMessagesCount = it.data?.count { message -> message.unread }

                    processHorizontalGroupData(
                        unreadMessagesCount = unreadMessagesCount,
                        forceRefresh = forceRefresh
                    )
                }
                Status.ERROR -> {
                    Timber.i("Loading dashboard messages result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                    processHorizontalGroupData(error = it.error, forceRefresh = forceRefresh)
                }
            }
        }.launch("dashboard_messages")
    }

    private fun loadAttendance(forceRefresh: Boolean) {
        flowWithResourceIn {
            val student = studentRepository.getCurrentStudent(true)
            val semester = semesterRepository.getCurrentSemester(student)

            attendanceSummaryRepository.getAttendanceSummary(student, semester, -1, forceRefresh)
        }.onEach {
            when (it.status) {
                Status.LOADING -> {
                    Timber.i("Loading dashboard attendance data started")
                    if (forceRefresh) return@onEach
                    val attendancePercentage = it.data?.calculatePercentage()

                    processHorizontalGroupData(
                        attendancePercentage = attendancePercentage,
                        isLoading = true,
                        forceRefresh = forceRefresh
                    )
                }
                Status.SUCCESS -> {
                    Timber.i("Loading dashboard attendance result: Success")
                    val attendancePercentage = it.data?.calculatePercentage()

                    processHorizontalGroupData(
                        attendancePercentage = attendancePercentage,
                        forceRefresh = forceRefresh
                    )
                }
                Status.ERROR -> {
                    Timber.i("Loading dashboard attendance result: An exception occurred")
                    errorHandler.dispatch(it.error!!)

                    processHorizontalGroupData(error = it.error, forceRefresh = forceRefresh)
                }
            }
        }.launch("dashboard_attendance")
    }

    private fun loadGrades(forceRefresh: Boolean) {
        flowWithResourceIn {
            val student = studentRepository.getCurrentStudent(true)
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
                }
                Status.SUCCESS -> {
                    Timber.i("Loading dashboard grades result: Success")
                    updateData(
                        DashboardItem.Grades(
                            subjectWithGrades = it.data,
                            gradeTheme = preferencesRepository.gradeColorTheme
                        ), forceRefresh
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

    private fun loadLessons(forceRefresh: Boolean) {
        flowWithResourceIn {
            val student = studentRepository.getCurrentStudent(true)
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
                    updateData(DashboardItem.Lessons(it.data, isLoading = true), forceRefresh)
                }
                Status.SUCCESS -> {
                    Timber.i("Loading dashboard lessons result: Success")
                    updateData(DashboardItem.Lessons(it.data), forceRefresh)
                }
                Status.ERROR -> {
                    Timber.i("Loading dashboard lessons result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                    updateData(DashboardItem.Lessons(error = it.error), forceRefresh)
                }
            }
        }.launch("dashboard_lessons")
    }

    private fun loadHomework(forceRefresh: Boolean) {
        flowWithResourceIn {
            val student = studentRepository.getCurrentStudent(true)
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

    private fun loadSchoolAnnouncements(forceRefresh: Boolean) {
        flowWithResourceIn {
            val student = studentRepository.getCurrentStudent(true)

            schoolAnnouncementRepository.getSchoolAnnouncements(student, forceRefresh)
        }.onEach {
            when (it.status) {
                Status.LOADING -> {
                    Timber.i("Loading dashboard announcements data started")
                    if (forceRefresh) return@onEach
                    updateData(
                        DashboardItem.Announcements(
                            it.data ?: emptyList(),
                            isLoading = true
                        ), forceRefresh
                    )
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

    private fun loadExams(forceRefresh: Boolean) {
        flowWithResourceIn {
            val student = studentRepository.getCurrentStudent(true)
            val semester = semesterRepository.getCurrentSemester(student)

            examRepository.getExams(
                student = student,
                semester = semester,
                start = LocalDate.now(),
                end = LocalDate.now().plusDays(7),
                forceRefresh = forceRefresh
            )
        }.onEach {
            when (it.status) {
                Status.LOADING -> {
                    Timber.i("Loading dashboard exams data started")
                    if (forceRefresh) return@onEach
                    updateData(
                        DashboardItem.Exams(it.data.orEmpty(), isLoading = true),
                        forceRefresh
                    )
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

    private fun loadConferences(forceRefresh: Boolean) {
        flowWithResourceIn {
            val student = studentRepository.getCurrentStudent(true)
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

    private fun processHorizontalGroupData(
        luckyNumber: Int? = null,
        unreadMessagesCount: Int? = null,
        attendancePercentage: Double? = null,
        error: Throwable? = null,
        isLoading: Boolean = false,
        forceRefresh: Boolean
    ) {
        val isLuckyNumberToLoad =
            dashboardTilesToLoad.any { it == DashboardItem.Tile.LUCKY_NUMBER }
        val isMessagesToLoad =
            dashboardTilesToLoad.any { it == DashboardItem.Tile.MESSAGES }
        val isAttendanceToLoad =
            dashboardTilesToLoad.any { it == DashboardItem.Tile.ATTENDANCE }
        val isPushedToList =
            dashboardItemLoadedList.any { it.type == DashboardItem.Type.HORIZONTAL_GROUP }

        if (error != null) {
            updateData(DashboardItem.HorizontalGroup(error = error), forceRefresh)
            return
        }

        if (isLoading) {
            val horizontalGroup =
                dashboardItemLoadedList.find { it is DashboardItem.HorizontalGroup } as DashboardItem.HorizontalGroup?
            val updatedHorizontalGroup =
                horizontalGroup?.copy(isLoading = true) ?: DashboardItem.HorizontalGroup(isLoading = true)

            updateData(updatedHorizontalGroup, forceRefresh)
        }

        if (forceRefresh && !isPushedToList) {
            updateData(DashboardItem.HorizontalGroup(), forceRefresh)
        }

        val horizontalGroup =
            dashboardItemLoadedList.single { it is DashboardItem.HorizontalGroup } as DashboardItem.HorizontalGroup

        when {
            luckyNumber != null -> {
                updateData(horizontalGroup.copy(luckyNumber = luckyNumber), forceRefresh)
            }
            unreadMessagesCount != null -> {
                updateData(
                    horizontalGroup.copy(unreadMessagesCount = unreadMessagesCount),
                    forceRefresh
                )
            }
            attendancePercentage != null -> {
                updateData(
                    horizontalGroup.copy(attendancePercentage = attendancePercentage),
                    forceRefresh
                )
            }
        }

        val isHorizontalGroupLoaded = dashboardItemLoadedList.any {
            if (it !is DashboardItem.HorizontalGroup) return@any false

            val isLuckyNumberStateCorrect = (it.luckyNumber != null) == isLuckyNumberToLoad
            val isMessagesStateCorrect = (it.unreadMessagesCount != null) == isMessagesToLoad
            val isAttendanceStateCorrect = (it.attendancePercentage != null) == isAttendanceToLoad

            isLuckyNumberStateCorrect && isAttendanceStateCorrect && isMessagesStateCorrect
        }

        if (isHorizontalGroupLoaded) {
            val updatedHorizontalGroup =
                dashboardItemLoadedList.single { it is DashboardItem.HorizontalGroup } as DashboardItem.HorizontalGroup

            updateData(updatedHorizontalGroup.copy(isLoading = false, error = null), forceRefresh)
        }
    }

    private fun updateData(dashboardItem: DashboardItem, forceRefresh: Boolean) {
        val isForceRefreshError = forceRefresh && dashboardItem.error != null

        with(dashboardItemLoadedList) {
            removeAll { it.type == dashboardItem.type && !isForceRefreshError }
            if (!isForceRefreshError) add(dashboardItem)
            sortBy { tile -> dashboardItemsToLoad.single { it == tile.type }.ordinal }
        }

        if (forceRefresh) {
            with(dashboardItemRefreshLoadedList) {
                removeAll { it.type == dashboardItem.type }
                add(dashboardItem)
            }
        }

        dashboardItemLoadedList.sortBy { tile -> dashboardItemsToLoad.single { it == tile.type }.ordinal }

        val isItemsLoaded =
            dashboardItemsToLoad.all { type -> dashboardItemLoadedList.any { it.type == type } }
        val isRefreshItemLoaded =
            dashboardItemsToLoad.all { type -> dashboardItemRefreshLoadedList.any { it.type == type } }
        val isItemsDataLoaded = isItemsLoaded && dashboardItemLoadedList.all {
            it.isDataLoaded || it.error != null
        }
        val isRefreshItemsDataLoaded = isRefreshItemLoaded && dashboardItemRefreshLoadedList.all {
            it.isDataLoaded || it.error != null
        }

        if (isRefreshItemsDataLoaded) {
            view?.showRefresh(false)
            dashboardItemRefreshLoadedList.clear()
        }

        view?.run {
            if (!forceRefresh) {
                showProgress(!isItemsDataLoaded)
                showContent(isItemsDataLoaded)
            }
            updateData(dashboardItemLoadedList.toList())
        }

        if (isItemsLoaded) {
            val filteredItems =
                dashboardItemLoadedList.filterNot { it.type == DashboardItem.Type.ACCOUNT }
            val isAccountItemError =
                dashboardItemLoadedList.single { it.type == DashboardItem.Type.ACCOUNT }.error != null
            val isGeneralError =
                filteredItems.all { it.error != null } && filteredItems.isNotEmpty() || isAccountItemError

            val errorMessage = filteredItems.map { it.error?.stackTraceToString() }.toString()

            lastError = Exception(errorMessage)

            view?.run {
                showProgress(false)
                showContent(!isGeneralError)
                showErrorView(isGeneralError)
            }
        }
    }
}