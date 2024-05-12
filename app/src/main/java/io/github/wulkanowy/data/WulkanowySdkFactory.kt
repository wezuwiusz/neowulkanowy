package io.github.wulkanowy.data

import com.chuckerteam.chucker.api.ChuckerInterceptor
import io.github.wulkanowy.data.db.dao.StudentDao
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.StudentIsEduOne
import io.github.wulkanowy.data.repositories.WulkanowyRepository
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.RemoteConfigHelper
import io.github.wulkanowy.utils.WebkitCookieManagerProxy
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WulkanowySdkFactory @Inject constructor(
    private val chuckerInterceptor: ChuckerInterceptor,
    private val remoteConfig: RemoteConfigHelper,
    private val webkitCookieManagerProxy: WebkitCookieManagerProxy,
    private val studentDb: StudentDao,
    private val wulkanowyRepository: WulkanowyRepository,
) {

    private val eduOneMutex = Mutex()
    private val migrationFailedStudentIds = mutableSetOf<Long>()

    private val sdk = Sdk().apply {
        androidVersion = android.os.Build.VERSION.RELEASE
        buildTag = android.os.Build.MODEL
        userAgentTemplate = remoteConfig.userAgentTemplate
        setSimpleHttpLogger { Timber.d(it) }
        setAdditionalCookieManager(webkitCookieManagerProxy)

        // for debug only
        addInterceptor(chuckerInterceptor, network = true)
    }

    fun createBase() = sdk

    suspend fun create(): Sdk {
        val mapping = wulkanowyRepository.getMapping()

        return createBase().apply {
            if (mapping != null) {
                endpointsMapping = mapping.endpoints
                vTokenMapping = mapping.vTokens
            }
        }
    }

    suspend fun create(student: Student, semester: Semester? = null): Sdk {
        val overrideIsEduOne = checkEduOneAndMigrateIfNecessary(student)
        return buildSdk(student, semester, overrideIsEduOne)
    }

    private suspend fun buildSdk(
        student: Student,
        semester: Semester?,
        isStudentEduOne: Boolean
    ): Sdk {
        return create().apply {
            email = student.email
            password = student.password
            symbol = student.symbol
            schoolSymbol = student.schoolSymbol
            studentId = student.studentId
            classId = student.classId
            emptyCookieJarInterceptor = true
            isEduOne = isStudentEduOne

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

    private suspend fun checkEduOneAndMigrateIfNecessary(student: Student): Boolean {
        if (student.isEduOne != null) return student.isEduOne

        if (student.id in migrationFailedStudentIds) {
            Timber.i("Migration eduOne: skipping because of previous failure")
            return false
        }

        eduOneMutex.withLock {
            if (student.id in migrationFailedStudentIds) {
                Timber.i("Migration eduOne: skipping because of previous failure")
                return false
            }

            val studentFromDatabase = studentDb.loadById(student.id)
            if (studentFromDatabase?.isEduOne != null) {
                Timber.i("Migration eduOne: already done")
                return studentFromDatabase.isEduOne
            }

            Timber.i("Migration eduOne: flag missing. Running migration...")
            val initializedSdk = buildSdk(
                student = student,
                semester = null,
                isStudentEduOne = false, // doesn't matter
            )
            val newCurrentStudent = runCatching { initializedSdk.getCurrentStudent() }
                .onFailure { Timber.e(it, "Migration eduOne: can't get current student") }
                .getOrNull()

            if (newCurrentStudent == null) {
                Timber.i("Migration eduOne: failed, so skipping")
                migrationFailedStudentIds.add(student.id)
                return false
            }

            Timber.i("Migration eduOne: success. New isEduOne flag: ${newCurrentStudent.isEduOne}")

            val studentIsEduOne = StudentIsEduOne(
                id = student.id,
                isEduOne = newCurrentStudent.isEduOne
            )
            studentDb.update(studentIsEduOne)
            return newCurrentStudent.isEduOne
        }
    }
}
