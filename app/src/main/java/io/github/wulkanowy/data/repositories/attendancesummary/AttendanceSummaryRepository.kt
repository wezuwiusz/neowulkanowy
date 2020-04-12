package io.github.wulkanowy.data.repositories.attendancesummary

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.AttendanceSummary
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.utils.uniqueSubtract
import io.reactivex.Single
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AttendanceSummaryRepository @Inject constructor(
    private val settings: InternetObservingSettings,
    private val local: AttendanceSummaryLocal,
    private val remote: AttendanceSummaryRemote
) {

    fun getAttendanceSummary(student: Student, semester: Semester, subjectId: Int, forceRefresh: Boolean = false): Single<List<AttendanceSummary>> {
        return local.getAttendanceSummary(semester, subjectId).filter { !forceRefresh }
            .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                .flatMap {
                    if (it) remote.getAttendanceSummary(student, semester, subjectId)
                    else Single.error(UnknownHostException())
                }.flatMap { new ->
                    local.getAttendanceSummary(semester, subjectId).toSingle(emptyList())
                        .doOnSuccess { old ->
                            local.deleteAttendanceSummary(old.uniqueSubtract(new))
                            local.saveAttendanceSummary(new.uniqueSubtract(old))
                        }
                }.flatMap { local.getAttendanceSummary(semester, subjectId).toSingle(emptyList()) })
    }
}
