package io.github.wulkanowy.data.repositories.student

import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.sdk.Sdk
import io.reactivex.Single
import org.threeten.bp.LocalDateTime.now
import javax.inject.Inject
import javax.inject.Singleton
import io.github.wulkanowy.sdk.pojo.Student as SdkStudent

@Singleton
class StudentRemote @Inject constructor(private val sdk: Sdk) {

    private fun mapStudents(students: List<SdkStudent>, email: String, password: String): List<Student> {
        return students.map { student ->
            Student(
                email = email.ifBlank { student.email },
                password = password,
                isParent = student.isParent,
                symbol = student.symbol,
                studentId = student.studentId,
                userLoginId = student.userLoginId,
                studentName = student.studentName,
                schoolSymbol = student.schoolSymbol,
                schoolShortName = student.schoolShortName,
                schoolName = student.schoolName,
                className = student.className,
                classId = student.classId,
                scrapperBaseUrl = student.scrapperBaseUrl,
                loginType = student.loginType.name,
                isCurrent = false,
                registrationDate = now(),
                mobileBaseUrl = student.mobileBaseUrl,
                privateKey = student.privateKey,
                certificateKey = student.certificateKey,
                loginMode = student.loginMode.name
            )
        }
    }

    fun getStudentsMobileApi(token: String, pin: String, symbol: String): Single<List<Student>> {
        return sdk.getStudentsFromMobileApi(token, pin, symbol, "").map { mapStudents(it, "", "") }
    }

    fun getStudentsScrapper(email: String, password: String, scrapperBaseUrl: String, symbol: String): Single<List<Student>> {
        return sdk.getStudentsFromScrapper(email, password, scrapperBaseUrl, symbol).map { mapStudents(it, email, password) }
    }

    fun getStudentsHybrid(email: String, password: String, scrapperBaseUrl: String, symbol: String): Single<List<Student>> {
        return sdk.getStudentsHybrid(email, password, scrapperBaseUrl, "", symbol).map { mapStudents(it, email, password) }
    }
}
