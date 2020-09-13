package io.github.wulkanowy.data.repositories.semester

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.DispatchersProvider
import io.github.wulkanowy.utils.getCurrentOrLast
import io.github.wulkanowy.utils.isCurrent
import io.github.wulkanowy.utils.uniqueSubtract
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SemesterRepository @Inject constructor(
    private val remote: SemesterRemote,
    private val local: SemesterLocal,
    private val dispatchers: DispatchersProvider
) {

    suspend fun getSemesters(student: Student, forceRefresh: Boolean = false, refreshOnNoCurrent: Boolean = false) = withContext(dispatchers.backgroundThread) {
        val semesters = local.getSemesters(student)

        if (isShouldFetch(student, semesters, forceRefresh, refreshOnNoCurrent)) {
            refreshSemesters(student)
            local.getSemesters(student)
        } else semesters
    }

    private fun isShouldFetch(student: Student, semesters: List<Semester>, forceRefresh: Boolean, refreshOnNoCurrent: Boolean): Boolean {
        val isNoSemesters = semesters.isEmpty()

        val isRefreshOnModeChangeRequired = if (Sdk.Mode.valueOf(student.loginMode) != Sdk.Mode.API) {
            semesters.firstOrNull { it.isCurrent }?.diaryId == 0
        } else false

        val isRefreshOnNoCurrentAppropriate = refreshOnNoCurrent && !semesters.any { semester -> semester.isCurrent }

        return forceRefresh || isNoSemesters || isRefreshOnModeChangeRequired || isRefreshOnNoCurrentAppropriate
    }

    private suspend fun refreshSemesters(student: Student) {
        val new = remote.getSemesters(student)
        if (new.isEmpty()) return Timber.i("Empty semester list!")

        val old = local.getSemesters(student)
        local.deleteSemesters(old.uniqueSubtract(new))
        local.saveSemesters(new.uniqueSubtract(old))
    }

    suspend fun getCurrentSemester(student: Student, forceRefresh: Boolean = false) = withContext(dispatchers.backgroundThread) {
        getSemesters(student, forceRefresh).getCurrentOrLast()
    }
}
