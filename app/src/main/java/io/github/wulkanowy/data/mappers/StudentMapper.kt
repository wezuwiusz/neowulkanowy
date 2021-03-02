package io.github.wulkanowy.data.mappers

import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import java.time.LocalDateTime
import io.github.wulkanowy.sdk.pojo.Student as SdkStudent

fun List<SdkStudent>.mapToEntities(password: String = "", colors: List<Long>) = map {
    StudentWithSemesters(
        student = Student(
            email = it.email,
            password = password,
            isParent = it.isParent,
            symbol = it.symbol,
            studentId = it.studentId,
            userLoginId = it.userLoginId,
            userName = it.userName,
            studentName = it.studentName + " " + it.studentSurname,
            schoolSymbol = it.schoolSymbol,
            schoolShortName = it.schoolShortName,
            schoolName = it.schoolName,
            className = it.className,
            classId = it.classId,
            scrapperBaseUrl = it.scrapperBaseUrl,
            loginType = it.loginType.name,
            isCurrent = false,
            registrationDate = LocalDateTime.now(),
            mobileBaseUrl = it.mobileBaseUrl,
            privateKey = it.privateKey,
            certificateKey = it.certificateKey,
            loginMode = it.loginMode.name,
        ).apply {
            avatarColor = colors.random()
        },
        semesters = it.semesters.mapToEntities(it.studentId)
    )
}
