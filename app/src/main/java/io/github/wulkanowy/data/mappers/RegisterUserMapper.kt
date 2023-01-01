package io.github.wulkanowy.data.mappers

import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.data.pojos.*
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.sdk.mapper.mapSemesters
import java.time.Instant
import io.github.wulkanowy.sdk.scrapper.register.RegisterStudent as SdkRegisterStudent
import io.github.wulkanowy.sdk.scrapper.register.RegisterUser as SdkRegisterUser

fun SdkRegisterUser.mapToPojo(password: String) = RegisterUser(
    email = email,
    login = login,
    password = password,
    baseUrl = baseUrl,
    loginType = loginType,
    symbols = symbols.map { registerSymbol ->
        RegisterSymbol(
            symbol = registerSymbol.symbol,
            error = registerSymbol.error,
            userName = registerSymbol.userName,
            schools = registerSymbol.schools.map {
                RegisterUnit(
                    userLoginId = it.userLoginId,
                    schoolId = it.schoolId,
                    schoolName = it.schoolName,
                    schoolShortName = it.schoolShortName,
                    parentIds = it.parentIds,
                    studentIds = it.studentIds,
                    employeeIds = it.employeeIds,
                    error = it.error,
                    students = it.subjects
                        .filterIsInstance<SdkRegisterStudent>()
                        .map { registerSubject ->
                            RegisterStudent(
                                studentId = registerSubject.studentId,
                                studentName = registerSubject.studentName,
                                studentSecondName = registerSubject.studentSecondName,
                                studentSurname = registerSubject.studentSurname,
                                className = registerSubject.className,
                                classId = registerSubject.classId,
                                isParent = registerSubject.isParent,
                                semesters = registerSubject.semesters
                                    .mapSemesters()
                                    .mapToEntities(registerSubject.studentId),
                            )
                        },
                )
            }
        )
    }
)

fun RegisterStudent.mapToStudentWithSemesters(
    user: RegisterUser,
    symbol: RegisterSymbol,
    unit: RegisterUnit,
    colors: List<Long>,
): StudentWithSemesters = StudentWithSemesters(
    semesters = semesters,
    student = Student(
        email = user.login, // for compatibility
        userName = symbol.userName,
        userLoginId = unit.userLoginId,
        isParent = isParent,
        className = className,
        classId = classId,
        studentId = studentId,
        symbol = symbol.symbol,
        loginType = user.loginType.name,
        schoolName = unit.schoolName,
        schoolShortName = unit.schoolShortName,
        schoolSymbol = unit.schoolId,
        studentName = "$studentName $studentSurname",
        loginMode = Sdk.Mode.SCRAPPER.name,
        scrapperBaseUrl = user.baseUrl,
        mobileBaseUrl = "",
        certificateKey = "",
        privateKey = "",
        password = user.password,
        isCurrent = false,
        registrationDate = Instant.now(),
    ).apply {
        avatarColor = colors.random()
    },
)
