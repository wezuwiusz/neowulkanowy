package io.github.wulkanowy.data

import com.chuckerteam.chucker.api.ChuckerInterceptor
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.RemoteConfigHelper
import io.github.wulkanowy.utils.WebkitCookieManagerProxy
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WulkanowySdkFactory @Inject constructor(
    private val chuckerInterceptor: ChuckerInterceptor,
    private val remoteConfig: RemoteConfigHelper,
    private val webkitCookieManagerProxy: WebkitCookieManagerProxy
) {

    private val sdk = Sdk().apply {
        androidVersion = android.os.Build.VERSION.RELEASE
        buildTag = android.os.Build.MODEL
        userAgentTemplate = remoteConfig.userAgentTemplate
        setSimpleHttpLogger { Timber.d(it) }
        setAdditionalCookieManager(webkitCookieManagerProxy)

        // for debug only
        addInterceptor(chuckerInterceptor, network = true)
    }

    fun create() = sdk

    fun create(student: Student, semester: Semester? = null): Sdk {
        return create().apply {
            email = student.email
            password = student.password
            symbol = student.symbol
            schoolSymbol = student.schoolSymbol
            studentId = student.studentId
            classId = student.classId
            emptyCookieJarInterceptor = true
            isEduOne = student.isEduOne

            if (Sdk.Mode.valueOf(student.loginMode) == Sdk.Mode.HEBE) {
                mobileBaseUrl = student.mobileBaseUrl
            } else {
                scrapperBaseUrl = student.scrapperBaseUrl
                domainSuffix = student.scrapperDomainSuffix
                loginType = Sdk.ScrapperLoginType.valueOf(student.loginType)
            }

            mode = Sdk.Mode.valueOf(student.loginMode)
            mobileBaseUrl = student.mobileBaseUrl
            keyId = student.certificateKey
            privatePem = student.privateKey

            if (semester != null) {
                diaryId = semester.diaryId
                kindergartenDiaryId = semester.kindergartenDiaryId
                schoolYear = semester.schoolYear
                unitId = semester.unitId
            }
        }
    }
}
