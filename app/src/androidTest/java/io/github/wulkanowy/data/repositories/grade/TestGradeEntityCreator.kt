package io.github.wulkanowy.data.repositories.grade

import org.threeten.bp.LocalDate
import io.github.wulkanowy.sdk.pojo.Grade as GradeRemote
import io.github.wulkanowy.data.db.entities.Grade as GradeLocal

fun createGradeLocal(value: Int, weight: Double, date: LocalDate, desc: String, semesterId: Int = 1): GradeLocal {
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
        value = value.toDouble(),
        weight = "",
        weightValue = weight
    )
}

fun createGradeApi(value: Int, weight: Double, date: LocalDate, desc: String): GradeRemote {
    return GradeRemote(
        subject = "",
        color = "",
        comment = "",
        date = date,
        description = desc,
        entry = "",
        modifier = .0,
        symbol = "",
        teacher = "",
        value = value.toDouble(),
        weight = weight.toString(),
        weightValue = weight
    )
}
