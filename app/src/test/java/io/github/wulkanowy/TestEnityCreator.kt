package io.github.wulkanowy

import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.sdk.Sdk
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalDateTime.now

fun createSemesterEntity(diaryId: Int, semesterId: Int, start: LocalDate, end: LocalDate, semesterName: Int = 1): Semester {
    return Semester(
        studentId = 1,
        diaryId = diaryId,
        semesterId = semesterId,
        diaryName = "$semesterId",
        schoolYear = 1970,
        classId = 0,
        semesterName = semesterName,
        unitId = 1,
        start = start,
        end = end
    )
}

fun getStudentEntity(mode: Sdk.Mode = Sdk.Mode.API): Student {
    return Student(
        scrapperBaseUrl = "http://fakelog.cf",
        email = "jan@fakelog.cf",
        certificateKey = "",
        classId = 0,
        className = "",
        isCurrent = false,
        isParent = false,
        loginMode = mode.name,
        loginType = "STANDARD",
        mobileBaseUrl = "",
        password = "",
        privateKey = "",
        registrationDate = now(),
        schoolName = "",
        schoolShortName = "test",
        schoolSymbol = "",
        studentId = 0,
        studentName = "",
        symbol = "",
        userLoginId = 0
    )
}

fun getTimetableEntity(
    isStudentPlan: Boolean = false,
    canceled: Boolean = false,
    start: LocalDateTime = now(),
    end: LocalDateTime = now()
) = Timetable(
    studentId = 0,
    subject = "",
    number = 0,
    diaryId = 0,
    canceled = canceled,
    changes = false,
    date = LocalDate.now(),
    end = end,
    group = "",
    info = "",
    isStudentPlan = isStudentPlan,
    room = "",
    roomOld = "",
    start = start,
    subjectOld = "",
    teacher = "",
    teacherOld = ""
)

fun getMessageEntity(
    messageId: Int,
    content: String,
    unread: Boolean
) = Message(
    studentId = 1,
    realId = 1,
    messageId = messageId,
    sender = "",
    senderId = 1,
    recipient = "",
    subject = "",
    content = content,
    date = now(),
    folderId = 1,
    unread = unread,
    unreadBy = 1,
    readBy = 1,
    removed = false,
    hasAttachments = false
)
