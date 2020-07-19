package io.github.wulkanowy.services.sync.works

import io.github.wulkanowy.data.Resource
import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.reactivex.Completable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.takeWhile

interface Work {

    fun create(student: Student, semester: Semester): Completable

    suspend fun <T> Flow<Resource<T>>.waitForResult() = takeWhile {
        it.status == Status.LOADING
    }.collect()
}
