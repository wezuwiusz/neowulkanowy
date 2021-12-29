package io.github.wulkanowy.services.sync.works

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student

interface Work {

    suspend fun doWork(student: Student, semester: Semester, notify: Boolean)
}
