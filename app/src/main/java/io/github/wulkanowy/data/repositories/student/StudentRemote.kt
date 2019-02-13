package io.github.wulkanowy.data.repositories.student

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.data.db.entities.Student
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudentRemote @Inject constructor(private val api: Api) {

    fun getStudents(email: String, password: String, endpoint: String): Single<List<Student>> {
        return api.getStudents().map { students ->
            students.map { student ->
                Student(
                    email = email,
                    password = password,
                    symbol = student.symbol,
                    studentId = student.studentId,
                    studentName = student.studentName,
                    schoolSymbol = student.schoolSymbol,
                    schoolName = student.schoolName,
                    endpoint = endpoint,
                    loginType = student.loginType.name
                )
            }
        }
    }
}
