package io.github.wulkanowy.utils

import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Attendance
import io.github.wulkanowy.data.db.entities.AttendanceSummary
import io.github.wulkanowy.sdk.scrapper.attendance.AttendanceCategory

/**
 * [UONET+ - Zasady tworzenia podsumowań liczb uczniów obecnych i nieobecnych w tabeli frekwencji]
 * (https://www.vulcan.edu.pl/vulcang_files/user/AABW/AABW-PDF/uonetplus/uonetplus_Frekwencja-liczby-obecnych-nieobecnych.pdf)
 */

private inline val AttendanceSummary.allPresences: Double
    get() = presence.toDouble() + absenceForSchoolReasons + lateness + latenessExcused

private inline val AttendanceSummary.allAbsences: Double
    get() = absence.toDouble() + absenceExcused

fun AttendanceSummary.calculatePercentage() = calculatePercentage(allPresences, allAbsences)

fun List<AttendanceSummary>.calculatePercentage(): Double {
    return calculatePercentage(sumOf { it.allPresences }, sumOf { it.allAbsences })
}

private fun calculatePercentage(presence: Double, absence: Double): Double {
    return if ((presence + absence) == 0.0) 0.0 else (presence / (presence + absence)) * 100
}

inline val Attendance.description
    get() = when (AttendanceCategory.getCategoryByName(name)) {
        AttendanceCategory.PRESENCE -> R.string.attendance_present
        AttendanceCategory.ABSENCE_UNEXCUSED -> R.string.attendance_absence_unexcused
        AttendanceCategory.ABSENCE_EXCUSED -> R.string.attendance_absence_excused
        AttendanceCategory.UNEXCUSED_LATENESS -> R.string.attendance_unexcused_lateness
        AttendanceCategory.EXCUSED_LATENESS -> R.string.attendance_excused_lateness
        AttendanceCategory.ABSENCE_FOR_SCHOOL_REASONS -> R.string.attendance_absence_school
        AttendanceCategory.EXEMPTION -> R.string.attendance_exemption
        AttendanceCategory.DELETED -> R.string.attendance_deleted
        else -> R.string.attendance_unknown
    }
