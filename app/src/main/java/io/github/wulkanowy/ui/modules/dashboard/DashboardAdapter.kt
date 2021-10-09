package io.github.wulkanowy.ui.modules.dashboard

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMarginsRelative
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.data.db.entities.TimetableHeader
import io.github.wulkanowy.databinding.ItemDashboardAccountBinding
import io.github.wulkanowy.databinding.ItemDashboardAnnouncementsBinding
import io.github.wulkanowy.databinding.ItemDashboardConferencesBinding
import io.github.wulkanowy.databinding.ItemDashboardExamsBinding
import io.github.wulkanowy.databinding.ItemDashboardGradesBinding
import io.github.wulkanowy.databinding.ItemDashboardHomeworkBinding
import io.github.wulkanowy.databinding.ItemDashboardHorizontalGroupBinding
import io.github.wulkanowy.databinding.ItemDashboardLessonsBinding
import io.github.wulkanowy.utils.createNameInitialsDrawable
import io.github.wulkanowy.utils.dpToPx
import io.github.wulkanowy.utils.getThemeAttrColor
import io.github.wulkanowy.utils.left
import io.github.wulkanowy.utils.nickOrName
import io.github.wulkanowy.utils.toFormattedString
import timber.log.Timber
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.timer

class DashboardAdapter @Inject constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var lessonsTimer: Timer? = null

    var onAccountTileClickListener: (Student) -> Unit = {}

    var onLuckyNumberTileClickListener: () -> Unit = {}

    var onMessageTileClickListener: () -> Unit = {}

    var onGradeTileClickListener: () -> Unit = {}

    var onAttendanceTileClickListener: () -> Unit = {}

    var onLessonsTileClickListener: (LocalDate) -> Unit = {}

    var onHomeworkTileClickListener: () -> Unit = {}

    var onAnnouncementsTileClickListener: () -> Unit = {}

    var onExamsTileClickListener: () -> Unit = {}

    var onConferencesTileClickListener: () -> Unit = {}

    val items = mutableListOf<DashboardItem>()

    fun submitList(newItems: List<DashboardItem>) {
        val diffResult =
            DiffUtil.calculateDiff(DiffCallback(newItems, items.toMutableList()))

        with(items) {
            clear()
            addAll(newItems)
        }

        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = items[position].type.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            DashboardItem.Type.ACCOUNT.ordinal -> AccountViewHolder(
                ItemDashboardAccountBinding.inflate(inflater, parent, false)
            )
            DashboardItem.Type.HORIZONTAL_GROUP.ordinal -> HorizontalGroupViewHolder(
                ItemDashboardHorizontalGroupBinding.inflate(inflater, parent, false)
            )
            DashboardItem.Type.GRADES.ordinal -> GradesViewHolder(
                ItemDashboardGradesBinding.inflate(inflater, parent, false)
            )
            DashboardItem.Type.LESSONS.ordinal -> LessonsViewHolder(
                ItemDashboardLessonsBinding.inflate(inflater, parent, false)
            )
            DashboardItem.Type.HOMEWORK.ordinal -> HomeworkViewHolder(
                ItemDashboardHomeworkBinding.inflate(inflater, parent, false)
            )
            DashboardItem.Type.ANNOUNCEMENTS.ordinal -> AnnouncementsViewHolder(
                ItemDashboardAnnouncementsBinding.inflate(inflater, parent, false)
            )
            DashboardItem.Type.EXAMS.ordinal -> ExamsViewHolder(
                ItemDashboardExamsBinding.inflate(inflater, parent, false)
            )
            DashboardItem.Type.CONFERENCES.ordinal -> ConferencesViewHolder(
                ItemDashboardConferencesBinding.inflate(inflater, parent, false)
            )
            else -> throw IllegalArgumentException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is AccountViewHolder -> bindAccountViewHolder(holder, position)
            is HorizontalGroupViewHolder -> bindHorizontalGroupViewHolder(holder, position)
            is GradesViewHolder -> bindGradesViewHolder(holder, position)
            is LessonsViewHolder -> bindLessonsViewHolder(holder, position)
            is HomeworkViewHolder -> bindHomeworkViewHolder(holder, position)
            is AnnouncementsViewHolder -> bindAnnouncementsViewHolder(holder, position)
            is ExamsViewHolder -> bindExamsViewHolder(holder, position)
            is ConferencesViewHolder -> bindConferencesViewHolder(holder, position)
        }
    }

    fun clearTimers() {
        lessonsTimer?.let {
            it.cancel()
            it.purge()
        }
        lessonsTimer = null
    }

    private fun bindAccountViewHolder(accountViewHolder: AccountViewHolder, position: Int) {
        val item = items[position] as DashboardItem.Account
        val student = item.student
        val isLoading = item.isLoading

        val avatar = student?.let {
            accountViewHolder.binding.root.context.createNameInitialsDrawable(
                text = it.nickOrName,
                backgroundColor = it.avatarColor
            )
        }

        with(accountViewHolder.binding) {
            dashboardAccountItemContent.isVisible = !isLoading
            dashboardAccountItemProgress.isVisible = isLoading

            dashboardAccountItemAvatar.setImageDrawable(avatar)
            dashboardAccountItemName.text = student?.nickOrName.orEmpty()
            dashboardAccountItemSchoolName.text = student?.schoolName.orEmpty()

            root.setOnClickListener { student?.let(onAccountTileClickListener) }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindHorizontalGroupViewHolder(
        horizontalGroupViewHolder: HorizontalGroupViewHolder,
        position: Int
    ) {
        val item = items[position] as DashboardItem.HorizontalGroup
        val unreadMessagesCount = item.unreadMessagesCount
        val attendancePercentage = item.attendancePercentage
        val luckyNumber = item.luckyNumber
        val error = item.error
        val isLoading = item.isLoading
        val binding = horizontalGroupViewHolder.binding
        val context = binding.root.context
        val isLoadingVisible =
            (isLoading && !item.isDataLoaded) || (isLoading && !item.isFullDataLoaded)
        val attendanceColor = when {
            attendancePercentage == null || attendancePercentage == .0 -> {
                context.getThemeAttrColor(R.attr.colorOnSurface)
            }
            attendancePercentage <= ATTENDANCE_SECOND_WARNING_THRESHOLD -> {
                context.getThemeAttrColor(R.attr.colorPrimary)
            }
            attendancePercentage <= ATTENDANCE_FIRST_WARNING_THRESHOLD -> {
                context.getThemeAttrColor(R.attr.colorTimetableChange)
            }
            else -> context.getThemeAttrColor(R.attr.colorOnSurface)
        }
        val attendanceString = if (attendancePercentage == null || attendancePercentage == .0) {
            context.getString(R.string.dashboard_horizontal_group_no_data)
        } else {
            "%.2f%%".format(attendancePercentage)
        }

        with(binding.dashboardHorizontalGroupItemAttendanceValue) {
            text = attendanceString
            setTextColor(attendanceColor)
        }

        with(binding) {
            dashboardHorizontalGroupItemMessageValue.text = unreadMessagesCount.toString()
            dashboardHorizontalGroupItemLuckyValue.text = if (luckyNumber == 0) {
                context.getString(R.string.dashboard_horizontal_group_no_data)
            } else luckyNumber?.toString()

            dashboardHorizontalGroupItemInfoContainer.isVisible = error != null || isLoadingVisible
            dashboardHorizontalGroupItemInfoProgress.isVisible = isLoadingVisible
            dashboardHorizontalGroupItemInfoErrorText.isVisible = error != null

            with(dashboardHorizontalGroupItemLuckyContainer) {
                isVisible = luckyNumber != null && luckyNumber != -1 && !isLoadingVisible
                setOnClickListener { onLuckyNumberTileClickListener() }

                updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    updateMarginsRelative(
                        end = if (attendancePercentage == null && unreadMessagesCount == null && luckyNumber != null) {
                            0
                        } else {
                            context.dpToPx(8f).toInt()
                        }
                    )
                }
            }

            with(dashboardHorizontalGroupItemAttendanceContainer) {
                isVisible =
                    attendancePercentage != null && attendancePercentage != -1.0 && !isLoadingVisible
                updateLayoutParams<ConstraintLayout.LayoutParams> {
                    matchConstraintPercentWidth = when {
                        luckyNumber == null && unreadMessagesCount == null -> 1.0f
                        luckyNumber == null || unreadMessagesCount == null -> 0.5f
                        else -> 0.4f
                    }
                }
                setOnClickListener { onAttendanceTileClickListener() }
            }

            with(dashboardHorizontalGroupItemMessageContainer) {
                isVisible =
                    unreadMessagesCount != null && unreadMessagesCount != -1 && !isLoadingVisible
                setOnClickListener { onMessageTileClickListener() }
            }
        }
    }

    private fun bindGradesViewHolder(gradesViewHolder: GradesViewHolder, position: Int) {
        val item = items[position] as DashboardItem.Grades
        val subjectWithGrades = item.subjectWithGrades.orEmpty()
        val gradeTheme = item.gradeTheme
        val error = item.error
        val isLoading = item.isLoading
        val dashboardGradesAdapter = gradesViewHolder.adapter.apply {
            this.items = subjectWithGrades.toList()
            this.gradeTheme = gradeTheme.orEmpty()
        }

        with(gradesViewHolder.binding) {
            dashboardGradesItemEmpty.isVisible =
                subjectWithGrades.isEmpty() && error == null && !isLoading
            dashboardGradesItemError.isVisible = error != null && !isLoading
            dashboardGradesItemProgress.isVisible =
                isLoading && error == null && subjectWithGrades.isEmpty()

            with(dashboardGradesItemRecycler) {
                adapter = dashboardGradesAdapter
                layoutManager = LinearLayoutManager(context)
                isVisible = subjectWithGrades.isNotEmpty() && error == null
                suppressLayout(true)
            }

            root.setOnClickListener { onGradeTileClickListener() }
        }
    }

    private fun bindLessonsViewHolder(lessonsViewHolder: LessonsViewHolder, position: Int) {
        val item = items[position] as DashboardItem.Lessons
        val timetableFull = item.lessons
        val binding = lessonsViewHolder.binding
        var dateToNavigate = LocalDate.now()

        fun updateLessonState() {
            val currentDateTime = LocalDateTime.now()
            val currentDate = LocalDate.now()
            val tomorrowDate = currentDate.plusDays(1)

            val currentTimetable = timetableFull?.lessons
                .orEmpty()
                .filter { it.date == currentDate }
                .filter { it.end.isAfter(currentDateTime) }
                .filterNot { it.canceled }
            val currentDayHeader =
                timetableFull?.headers.orEmpty().singleOrNull { it.date == currentDate }

            val tomorrowTimetable = timetableFull?.lessons.orEmpty()
                .filter { it.date == currentDate.plusDays(1) }
                .filterNot { it.canceled }
            val tomorrowDayHeader =
                timetableFull?.headers.orEmpty().singleOrNull { it.date == currentDate.plusDays(1) }

            when {
                currentTimetable.isNotEmpty() -> {
                    dateToNavigate = currentDate
                    updateLessonView(item, currentTimetable, binding)
                    binding.dashboardLessonsItemTitleTomorrow.isVisible = false
                    binding.dashboardLessonsItemTitleTodayAndTomorrow.isVisible = false
                }
                tomorrowTimetable.isNotEmpty() -> {
                    dateToNavigate = tomorrowDate
                    updateLessonView(item, tomorrowTimetable, binding)
                    binding.dashboardLessonsItemTitleTomorrow.isVisible = true
                    binding.dashboardLessonsItemTitleTodayAndTomorrow.isVisible = false
                }
                currentDayHeader != null && currentDayHeader.content.isNotBlank() -> {
                    dateToNavigate = currentDate
                    updateLessonView(item, emptyList(), binding, currentDayHeader)
                    binding.dashboardLessonsItemTitleTomorrow.isVisible = false
                    binding.dashboardLessonsItemTitleTodayAndTomorrow.isVisible = false
                }
                tomorrowDayHeader != null && tomorrowDayHeader.content.isNotBlank() -> {
                    dateToNavigate = tomorrowDate
                    updateLessonView(item, emptyList(), binding, tomorrowDayHeader)
                    binding.dashboardLessonsItemTitleTomorrow.isVisible = true
                    binding.dashboardLessonsItemTitleTodayAndTomorrow.isVisible = false
                }
                else -> {
                    dateToNavigate = currentDate
                    updateLessonView(item, emptyList(), binding)
                    binding.dashboardLessonsItemTitleTomorrow.isVisible = false
                    binding.dashboardLessonsItemTitleTodayAndTomorrow.isVisible =
                        !(item.isLoading && item.error == null)
                }
            }
        }

        updateLessonState()

        lessonsTimer?.cancel()
        lessonsTimer = timer(period = 1000) {
            Handler(Looper.getMainLooper()).post { updateLessonState() }
        }

        binding.root.setOnClickListener { onLessonsTileClickListener(dateToNavigate) }
    }

    private fun updateLessonView(
        item: DashboardItem.Lessons,
        timetableToShow: List<Timetable>,
        binding: ItemDashboardLessonsBinding,
        header: TimetableHeader? = null,
    ) {
        val currentDateTime = LocalDateTime.now()
        val nextLessons = timetableToShow.filter { it.end.isAfter(currentDateTime) }
            .sortedBy { it.start }

        with(binding) {
            dashboardLessonsItemEmpty.isVisible =
                (timetableToShow.isEmpty() || nextLessons.isEmpty()) && item.error == null && header == null && !item.isLoading
            dashboardLessonsItemError.isVisible = item.error != null && !item.isLoading
            dashboardLessonsItemProgress.isVisible =
                item.isLoading && (timetableToShow.isEmpty() || nextLessons.isEmpty()) && item.error == null && header == null

            val secondLesson = nextLessons.getOrNull(1)
            val firstLesson = nextLessons.getOrNull(0)

            updateFirstLessonView(binding, firstLesson, currentDateTime)
            updateSecondLesson(binding, firstLesson, secondLesson)
            updateLessonSummary(binding, nextLessons)
            updateLessonHeader(binding, header)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateFirstLessonView(
        binding: ItemDashboardLessonsBinding,
        firstLesson: Timetable?,
        currentDateTime: LocalDateTime
    ) {
        val context = binding.root.context
        val sansSerifFont = Typeface.create("sans-serif", Typeface.NORMAL)
        val sansSerifMediumFont = Typeface.create("sans-serif-medium", Typeface.NORMAL)

        with(binding) {
            dashboardLessonsItemFirstTitle.isVisible = firstLesson != null
            dashboardLessonsItemFirstTime.isVisible = firstLesson != null
            dashboardLessonsItemFirstTimeRange.isVisible = firstLesson != null
            dashboardLessonsItemFirstValue.isVisible = firstLesson != null
        }

        firstLesson ?: return

        val minutesToStartLesson =
            Duration.between(currentDateTime, firstLesson.start).toMinutes() + 1
        val isFirstTimeVisible: Boolean
        val isFirstTimeRangeVisible: Boolean
        val firstTimeText: String
        val firstTimeRangeText: String
        val firstTitleText: String
        val firstTitleAndValueTextColor: Int
        val firstTitleAndValueTextFont: Typeface

        if (currentDateTime < firstLesson.start) {
            if (minutesToStartLesson > 60) {
                val formattedStartTime = firstLesson.start.toFormattedString("HH:mm")
                val formattedEndTime = firstLesson.end.toFormattedString("HH:mm")

                firstTimeRangeText = "$formattedStartTime - $formattedEndTime"
                firstTimeText = ""

                isFirstTimeRangeVisible = true
                isFirstTimeVisible = false
            } else {
                firstTimeText = context.resources.getQuantityString(
                    R.plurals.dashboard_timetable_first_lesson_time_in_minutes,
                    minutesToStartLesson.toInt(),
                    minutesToStartLesson
                )
                firstTimeRangeText = ""

                isFirstTimeRangeVisible = false
                isFirstTimeVisible = true
            }

            when {
                minutesToStartLesson < 60 -> {
                    firstTitleAndValueTextColor = context.getThemeAttrColor(R.attr.colorPrimary)
                    firstTitleAndValueTextFont = sansSerifMediumFont
                    firstTitleText =
                        context.getString(R.string.dashboard_timetable_first_lesson_title_moment)
                }
                minutesToStartLesson < 240 -> {
                    firstTitleAndValueTextColor =
                        context.getThemeAttrColor(R.attr.colorOnSurface)
                    firstTitleAndValueTextFont = sansSerifFont
                    firstTitleText =
                        context.getString(R.string.dashboard_timetable_first_lesson_title_soon)
                }
                else -> {
                    firstTitleAndValueTextColor =
                        context.getThemeAttrColor(R.attr.colorOnSurface)
                    firstTitleAndValueTextFont = sansSerifFont
                    firstTitleText =
                        context.getString(R.string.dashboard_timetable_first_lesson_title_first)
                }
            }
        } else {
            val minutesToEndLesson = firstLesson.left?.toMinutes()?.plus(1) ?: run {
                Timber.e(IllegalArgumentException("Lesson left is null. START ${firstLesson.start} ; END ${firstLesson.end} ; CURRENT ${LocalDateTime.now()}"))
                0
            }

            firstTimeText = context.resources.getQuantityString(
                R.plurals.dashboard_timetable_first_lesson_time_more_minutes,
                minutesToEndLesson.toInt(),
                minutesToEndLesson
            )
            firstTimeRangeText = ""

            isFirstTimeRangeVisible = false
            isFirstTimeVisible = true

            firstTitleAndValueTextColor = context.getThemeAttrColor(R.attr.colorPrimary)
            firstTitleAndValueTextFont = sansSerifMediumFont
            firstTitleText = context.getString(R.string.dashboard_timetable_first_lesson_title_now)
        }

        with(binding.dashboardLessonsItemFirstTime) {
            isVisible = isFirstTimeVisible
            text = firstTimeText
        }
        with(binding.dashboardLessonsItemFirstTimeRange) {
            isVisible = isFirstTimeRangeVisible
            text = firstTimeRangeText
        }
        with(binding.dashboardLessonsItemFirstTitle) {
            setTextColor(firstTitleAndValueTextColor)
            typeface = firstTitleAndValueTextFont
            text = firstTitleText
        }
        with(binding.dashboardLessonsItemFirstValue) {
            setTextColor(firstTitleAndValueTextColor)
            typeface = firstTitleAndValueTextFont
            text =
                "${firstLesson.subject} ${if (firstLesson.room.isNotBlank()) "(${firstLesson.room})" else ""}"
        }
    }

    private fun updateSecondLesson(
        binding: ItemDashboardLessonsBinding,
        firstLesson: Timetable?,
        secondLesson: Timetable?
    ) {
        val context = binding.root.context

        val formattedStartTime = secondLesson?.start?.toFormattedString("HH:mm")
        val formattedEndTime = secondLesson?.end?.toFormattedString("HH:mm")

        val secondTimeText = "$formattedStartTime - $formattedEndTime"
        val secondValueText = if (secondLesson != null) {
            val roomString = if (secondLesson.room.isNotBlank()) "(${secondLesson.room})" else ""

            "${secondLesson.subject} $roomString"
        } else {
            context.getString(R.string.dashboard_timetable_second_lesson_value_end)
        }

        with(binding.dashboardLessonsItemSecondTime) {
            isVisible = secondLesson != null
            text = secondTimeText
        }
        with(binding.dashboardLessonsItemSecondValue) {
            isVisible = !(secondLesson == null && firstLesson == null)
            text = secondValueText
        }
        binding.dashboardLessonsItemSecondTitle.isVisible =
            !(secondLesson == null && firstLesson == null)
    }

    private fun updateLessonSummary(
        binding: ItemDashboardLessonsBinding,
        nextLessons: List<Timetable>
    ) {
        val context = binding.root.context
        val formattedEndTime = nextLessons.lastOrNull()?.end?.toFormattedString("HH:mm")

        with(binding) {
            dashboardLessonsItemThirdTime.isVisible =
                nextLessons.size > LESSON_SUMMARY_VISIBILITY_THRESHOLD
            dashboardLessonsItemThirdTitle.isVisible =
                nextLessons.size > LESSON_SUMMARY_VISIBILITY_THRESHOLD
            dashboardLessonsItemThirdValue.isVisible =
                nextLessons.size > LESSON_SUMMARY_VISIBILITY_THRESHOLD
            dashboardLessonsItemDivider.isVisible =
                nextLessons.size > LESSON_SUMMARY_VISIBILITY_THRESHOLD

            dashboardLessonsItemThirdValue.text = context.resources.getQuantityString(
                R.plurals.dashboard_timetable_third_value,
                nextLessons.size - LESSON_SUMMARY_VISIBILITY_THRESHOLD,
                nextLessons.size - LESSON_SUMMARY_VISIBILITY_THRESHOLD
            )
            dashboardLessonsItemThirdTime.text =
                context.getString(R.string.dashboard_timetable_third_time, formattedEndTime)
        }
    }

    private fun updateLessonHeader(
        binding: ItemDashboardLessonsBinding,
        header: TimetableHeader?
    ) {
        with(binding.dashboardLessonsItemDayHeader) {
            isVisible = header != null
            text = header?.content
        }
    }

    private fun bindHomeworkViewHolder(homeworkViewHolder: HomeworkViewHolder, position: Int) {
        val item = items[position] as DashboardItem.Homework
        val homeworkList = item.homework.orEmpty()
        val error = item.error
        val isLoading = item.isLoading
        val context = homeworkViewHolder.binding.root.context
        val homeworkAdapter = homeworkViewHolder.adapter.apply {
            this.items = homeworkList.take(MAX_VISIBLE_LIST_ITEMS)
        }

        with(homeworkViewHolder.binding) {
            dashboardHomeworkItemEmpty.isVisible =
                homeworkList.isEmpty() && error == null && !isLoading
            dashboardHomeworkItemError.isVisible = error != null && !isLoading
            dashboardHomeworkItemProgress.isVisible =
                isLoading && error == null && homeworkList.isEmpty()
            dashboardHomeworkItemDivider.isVisible = homeworkList.size > MAX_VISIBLE_LIST_ITEMS
            dashboardHomeworkItemMore.isVisible = homeworkList.size > MAX_VISIBLE_LIST_ITEMS
            dashboardHomeworkItemMore.text = context.resources.getQuantityString(
                R.plurals.dashboard_homework_more,
                homeworkList.size - MAX_VISIBLE_LIST_ITEMS,
                homeworkList.size - MAX_VISIBLE_LIST_ITEMS
            )

            with(dashboardHomeworkItemRecycler) {
                adapter = homeworkAdapter
                layoutManager = LinearLayoutManager(context)
                isVisible = homeworkList.isNotEmpty() && error == null
                suppressLayout(true)
            }

            root.setOnClickListener { onHomeworkTileClickListener() }
        }
    }

    private fun bindAnnouncementsViewHolder(
        announcementsViewHolder: AnnouncementsViewHolder,
        position: Int
    ) {
        val item = items[position] as DashboardItem.Announcements
        val schoolAnnouncementList = item.announcement.orEmpty()
        val error = item.error
        val isLoading = item.isLoading
        val context = announcementsViewHolder.binding.root.context
        val schoolAnnouncementsAdapter = announcementsViewHolder.adapter.apply {
            this.items = schoolAnnouncementList.take(MAX_VISIBLE_LIST_ITEMS)
        }

        with(announcementsViewHolder.binding) {
            dashboardAnnouncementsItemEmpty.isVisible =
                schoolAnnouncementList.isEmpty() && error == null && !isLoading
            dashboardAnnouncementsItemError.isVisible = error != null && !isLoading
            dashboardAnnouncementsItemProgress.isVisible =
                isLoading && error == null && schoolAnnouncementList.isEmpty()
            dashboardAnnouncementsItemDivider.isVisible =
                schoolAnnouncementList.size > MAX_VISIBLE_LIST_ITEMS
            dashboardAnnouncementsItemMore.isVisible =
                schoolAnnouncementList.size > MAX_VISIBLE_LIST_ITEMS
            dashboardAnnouncementsItemMore.text = context.resources.getQuantityString(
                R.plurals.dashboard_announcements_more,
                schoolAnnouncementList.size - MAX_VISIBLE_LIST_ITEMS,
                schoolAnnouncementList.size - MAX_VISIBLE_LIST_ITEMS
            )

            with(dashboardAnnouncementsItemRecycler) {
                layoutManager = LinearLayoutManager(context)
                adapter = schoolAnnouncementsAdapter
                isVisible = schoolAnnouncementList.isNotEmpty() && error == null
                suppressLayout(true)
            }

            root.setOnClickListener { onAnnouncementsTileClickListener() }
        }
    }

    private fun bindExamsViewHolder(examsViewHolder: ExamsViewHolder, position: Int) {
        val item = items[position] as DashboardItem.Exams
        val exams = item.exams.orEmpty()
        val error = item.error
        val isLoading = item.isLoading
        val context = examsViewHolder.binding.root.context
        val examAdapter = examsViewHolder.adapter.apply {
            this.items = exams.take(MAX_VISIBLE_LIST_ITEMS)
        }

        with(examsViewHolder.binding) {
            dashboardExamsItemEmpty.isVisible = exams.isEmpty() && error == null && !isLoading
            dashboardExamsItemError.isVisible = error != null && !isLoading
            dashboardExamsItemProgress.isVisible = isLoading && error == null && exams.isEmpty()
            dashboardExamsItemDivider.isVisible = exams.size > MAX_VISIBLE_LIST_ITEMS
            dashboardExamsItemMore.isVisible = exams.size > MAX_VISIBLE_LIST_ITEMS
            dashboardExamsItemMore.text = context.resources.getQuantityString(
                R.plurals.dashboard_exams_more,
                exams.size - MAX_VISIBLE_LIST_ITEMS,
                exams.size - MAX_VISIBLE_LIST_ITEMS
            )

            with(dashboardExamsItemRecycler) {
                layoutManager = LinearLayoutManager(context)
                adapter = examAdapter
                isVisible = exams.isNotEmpty() && error == null
                suppressLayout(true)
            }

            root.setOnClickListener { onExamsTileClickListener() }
        }
    }

    private fun bindConferencesViewHolder(
        conferencesViewHolder: ConferencesViewHolder,
        position: Int
    ) {
        val item = items[position] as DashboardItem.Conferences
        val conferences = item.conferences.orEmpty()
        val error = item.error
        val isLoading = item.isLoading
        val context = conferencesViewHolder.binding.root.context
        val conferenceAdapter = conferencesViewHolder.adapter.apply {
            this.items = conferences.take(MAX_VISIBLE_LIST_ITEMS)
        }

        with(conferencesViewHolder.binding) {
            dashboardConferencesItemEmpty.isVisible =
                conferences.isEmpty() && error == null && !isLoading
            dashboardConferencesItemError.isVisible = error != null && !isLoading
            dashboardConferencesItemProgress.isVisible =
                isLoading && error == null && conferences.isEmpty()
            dashboardConferencesItemDivider.isVisible = conferences.size > MAX_VISIBLE_LIST_ITEMS
            dashboardConferencesItemMore.isVisible = conferences.size > MAX_VISIBLE_LIST_ITEMS
            dashboardConferencesItemMore.text = context.resources.getQuantityString(
                R.plurals.dashboard_conference_more,
                conferences.size - MAX_VISIBLE_LIST_ITEMS,
                conferences.size - MAX_VISIBLE_LIST_ITEMS
            )

            with(dashboardConferencesItemRecycler) {
                layoutManager = LinearLayoutManager(context)
                adapter = conferenceAdapter
                isVisible = conferences.isNotEmpty() && error == null
                suppressLayout(true)
            }

            root.setOnClickListener { onConferencesTileClickListener() }
        }
    }

    class AccountViewHolder(val binding: ItemDashboardAccountBinding) :
        RecyclerView.ViewHolder(binding.root)

    class HorizontalGroupViewHolder(val binding: ItemDashboardHorizontalGroupBinding) :
        RecyclerView.ViewHolder(binding.root)

    class GradesViewHolder(val binding: ItemDashboardGradesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val adapter by lazy { DashboardGradesAdapter() }
    }

    class LessonsViewHolder(val binding: ItemDashboardLessonsBinding) :
        RecyclerView.ViewHolder(binding.root)

    class HomeworkViewHolder(val binding: ItemDashboardHomeworkBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val adapter by lazy { DashboardHomeworkAdapter() }
    }

    class AnnouncementsViewHolder(val binding: ItemDashboardAnnouncementsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val adapter by lazy { DashboardAnnouncementsAdapter() }
    }

    class ExamsViewHolder(val binding: ItemDashboardExamsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val adapter by lazy { DashboardExamsAdapter() }
    }

    class ConferencesViewHolder(val binding: ItemDashboardConferencesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val adapter by lazy { DashboardConferencesAdapter() }
    }

    private class DiffCallback(
        private val newList: List<DashboardItem>,
        private val oldList: List<DashboardItem>
    ) : DiffUtil.Callback() {

        override fun getNewListSize() = newList.size

        override fun getOldListSize() = oldList.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            newList[newItemPosition] == oldList[oldItemPosition]

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            newList[newItemPosition].type == oldList[oldItemPosition].type
    }

    private companion object {

        private const val LESSON_SUMMARY_VISIBILITY_THRESHOLD = 2

        private const val MAX_VISIBLE_LIST_ITEMS = 5

        private const val ATTENDANCE_FIRST_WARNING_THRESHOLD = 75.0

        private const val ATTENDANCE_SECOND_WARNING_THRESHOLD = 50.0
    }
}
