package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.db.entities.Student
import org.threeten.bp.LocalDateTime

fun getStudent(): Student {
    return Student(
        email = "test",
        password = "test123",
        schoolSymbol = "23",
        scrapperBaseUrl = "fakelog.cf",
        loginType = "AUTO",
        isCurrent = true,
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
