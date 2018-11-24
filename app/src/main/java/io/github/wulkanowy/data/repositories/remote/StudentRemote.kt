package io.github.wulkanowy.data.repositories.remote

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.data.db.entities.Student
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudentRemote @Inject constructor(private val api: Api) {

    fun getStudents(email: String, password: String, endpoint: String): Single<List<Student>> {
        return api.getPupils().map { students ->
            students.map { pupil ->
                Student(
                    email = email,
                    password = password,
                    symbol = pupil.symbol,
                    studentId = pupil.studentId,
                    studentName = pupil.studentName,
                    schoolSymbol = pupil.schoolSymbol,
                    schoolName = pupil.schoolName,
                    endpoint = endpoint,
                    loginType = pupil.loginType.name
                )
            }
        }
    }
}
