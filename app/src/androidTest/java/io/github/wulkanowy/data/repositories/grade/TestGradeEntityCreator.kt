package io.github.wulkanowy.data.repositories.grade

import io.github.wulkanowy.api.toDate
import org.threeten.bp.LocalDate
import io.github.wulkanowy.api.grades.Grade as GradeRemote
import io.github.wulkanowy.data.db.entities.Grade as GradeLocal

fun createGradeLocal(value: Int, weight: Int, date: LocalDate, desc: String, semesterId: Int = 1): GradeLocal {
    return GradeLocal(
        semesterId = semesterId,
        studentId = 1,
        modifier = .0,
        teacher = "",
        subject = "",
        date = date,
        color = "",
        comment = "",
        description = desc,
        entry = "",
        gradeSymbol = "",
        value = value,
        weight = "",
        weightValue = weight
    )
}

fun createGradeApi(value: Int, weight: Int, date: LocalDate, desc: String): GradeRemote {
    return GradeRemote().apply {
        this.value = value
        this.weightValue = weight
        this.date = date.toDate()
        this.description = desc
    }
}
