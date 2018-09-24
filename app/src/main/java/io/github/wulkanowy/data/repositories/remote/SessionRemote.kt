package io.github.wulkanowy.data.repositories.remote

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRemote @Inject constructor(private val api: Api) {

    fun getConnectedStudents(email: String, password: String, symbol: String): Single<List<Student>> {
        return Single.just(initApi(Student(email = email, password = password, symbol = symbol)))
                .flatMap { _ ->
                    api.getPupils().map { students ->
                        students.map {
                            Student(email = email,
                                    password = password,
                                    symbol = it.symbol,
                                    studentId = it.studentId,
                                    studentName = it.studentName,
                                    schoolId = it.schoolId,
                                    schoolName = it.schoolName)
                        }
                    }
                }
    }

    fun getSemesters(student: Student): Single<List<Semester>> {
        return Single.just(initApi(student)).flatMap { _ ->
            api.getSemesters().map { semesters ->
                semesters.map {
                    Semester(studentId = student.studentId,
                            diaryId = it.diaryId,
                            diaryName = it.diaryName,
                            semesterId = it.semesterId.toString(),
                            semesterName = it.semesterNumber,
                            current = it.current)
                }

            }
        }
    }

    fun initApi(student: Student, checkInit: Boolean = false) {
        if (if (checkInit) api.studentId.isEmpty() else true) {
            api.run {
                email = student.email
                password = student.password
                symbol = student.symbol
                host = "vulcan.net.pl"
                schoolId = student.schoolId
                studentId = student.studentId
                notifyDataChanged()
            }
        }
    }
}
