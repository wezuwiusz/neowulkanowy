package io.github.wulkanowy.data

import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.sdk.Sdk
import javax.inject.Inject

class SdkHelper @Inject constructor(private val sdk: Sdk) {

    fun init(student: Student) {
        sdk.apply {
            email = student.email
            password = student.password
            symbol = student.symbol
            schoolSymbol = student.schoolSymbol
            studentId = student.studentId
            classId = student.classId

            if (Sdk.Mode.valueOf(student.loginMode) != Sdk.Mode.API) {
                scrapperBaseUrl = student.scrapperBaseUrl
                loginType = Sdk.ScrapperLoginType.valueOf(student.loginType)
            }
            loginId = student.userLoginId

            mode = Sdk.Mode.valueOf(student.loginMode)
            mobileBaseUrl = student.mobileBaseUrl
            certKey = student.certificateKey
            privateKey = student.privateKey
        }
    }
}
