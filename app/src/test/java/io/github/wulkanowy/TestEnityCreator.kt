package io.github.wulkanowy

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.sdk.Sdk
import java.time.LocalDate
import java.time.LocalDateTime.now
import io.github.wulkanowy.sdk.pojo.Semester as SdkSemester

fun getSemesterEntity(diaryId: Int = 1, semesterId: Int = 1, start: LocalDate = LocalDate.now(), end: LocalDate = LocalDate.now(), semesterName: Int = 1) = Semester(
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

fun getSemesterPojo(diaryId: Int, semesterId: Int, start: LocalDate, end: LocalDate, semesterName: Int = 1) = SdkSemester(
    diaryId = diaryId,
    semesterId = semesterId,
    diaryName = "$semesterId",
    schoolYear = 1970,
    classId = 0,
    semesterNumber = semesterName,
    unitId = 1,
    start = start,
    end = end,
)

fun getStudentEntity(mode: Sdk.Mode = Sdk.Mode.API) = Student(
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
    studentId = 1,
    studentName = "",
    symbol = "",
    userLoginId = 1,
    userName = "",
).apply {
    id = 1
}
