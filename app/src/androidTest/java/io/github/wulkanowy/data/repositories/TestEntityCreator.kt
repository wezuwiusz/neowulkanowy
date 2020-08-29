package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import java.time.LocalDate.now
import java.time.LocalDateTime

fun getStudent(): Student {
    return Student(
        email = "test",
        password = "test123",
        schoolSymbol = "23",
        scrapperBaseUrl = "fakelog.cf",
        loginType = "AUTO",
        isCurrent = true,
        userName = "",
        studentName = "",
        schoolShortName = "",
        schoolName = "",
        studentId = 0,
        classId = 1,
        symbol = "",
        registrationDate = LocalDateTime.now(),
        className = "",
        loginMode = "API",
        certificateKey = "",
        privateKey = "",
        mobileBaseUrl = "",
        userLoginId = 0,
        isParent = false
    )
}

fun getSemester() = Semester(
    semesterId = 1,
    studentId = 1,
    classId = 1,
    diaryId = 2,
    diaryName = "",
    end = now(),
    schoolYear = 2019,
    semesterName = 1,
    start = now(),
    unitId = 1
)
