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
            host = URL(student.endpoint).run { host + ":$port".removeSuffix(":-1") }
            ssl = student.endpoint.startsWith("https")
            loginType = Api.LoginType.valueOf(student.loginType)
        }
    }

    fun initApi(email: String, password: String, symbol: String, endpoint: String) {
        initApi(Student(email = email, password = password, symbol = symbol, endpoint = endpoint, loginType = "AUTO"))
    }
}

