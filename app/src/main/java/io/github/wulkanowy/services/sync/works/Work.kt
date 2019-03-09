package io.github.wulkanowy.services.sync.works

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.reactivex.Completable

interface Work {

    fun create(student: Student, semester: Semester): Completable
}

