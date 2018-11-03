package io.github.wulkanowy.data.repositories.remote

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.reactivex.Single
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRemote @Inject constructor(private val api: Api) {

    fun getConnectedStudents(email: String, password: String, symbol: String, endpoint: String): Single<List<Student>> {
        return Single.just(
            initApi(
                Student(
                    email = email,
                    password = password,
                    symbol = symbol,
                    endpoint = endpoint,
                    loginType = "AUTO"
                ), true
            )
        ).flatMap {
            api.getPupils().map { students ->
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

    fun getSemesters(student: Student): Single<List<Semester>> {
        return Single.just(initApi(student)).flatMap {
            api.getSemesters().map { semesters ->
                semesters.map { semester ->
                    Semester(
                        studentId = student.studentId,
                        diaryId = semester.diaryId,
                        diaryName = semester.diaryName,
                        semesterId = semester.semesterId,
                        semesterName = semester.semesterNumber,
                        current = semester.current
                    )
                }

            }
        }
    }

    fun getCurrentSemester(student: Student): Single<Semester> {
        return api.getCurrentSemester().map {
            Semester(
                studentId = student.studentId,
                diaryId = it.diaryId,
                diaryName = it.diaryName,
                semesterId = it.semesterId,
                semesterName = it.semesterNumber,
                current = it.current
            )
        }
    }

    fun initApi(student: Student, reInitialize: Boolean = false) {
        if (if (reInitialize) true else 0 == api.studentId) {
            api.run {
                logLevel = HttpLoggingInterceptor.Level.NONE
                email = student.email
                password = student.password
                symbol = student.symbol
                host = URL(student.endpoint).run { host + ":$port".removeSuffix(":-1") }
                ssl = student.endpoint.startsWith("https")
                schoolSymbol = student.schoolSymbol
                studentId = student.studentId
                loginType = Api.LoginType.valueOf(student.loginType)
                notifyDataChanged()
                setInterceptor(HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
                    Timber.d(it)
                }).setLevel(HttpLoggingInterceptor.Level.BASIC))
            }
        }
    }
}
