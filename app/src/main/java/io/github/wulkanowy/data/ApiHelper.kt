package io.github.wulkanowy.data

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.data.db.entities.Student
import java.net.URL
import javax.inject.Inject

class ApiHelper @Inject constructor(private val api: Api) {

    fun initApi(student: Student) {
        api.apply {
            email = student.email
            password = student.password
            symbol = student.symbol
            schoolSymbol = student.schoolSymbol
            studentId = student.studentId
            classId = student.classId
            host = URL(student.endpoint).run { host + ":$port".removeSuffix(":-1") }
            ssl = student.endpoint.startsWith("https")
            loginType = Api.LoginType.valueOf(student.loginType)
            useNewStudent = true
        }
    }

    fun initApi(email: String, password: String, symbol: String, endpoint: String) {
        api.apply {
            this.email = email
            this.password = password
            this.symbol = symbol
            host = URL(endpoint).run { host + ":$port".removeSuffix(":-1") }
            ssl = endpoint.startsWith("https")
            useNewStudent = true
        }
    }
}
