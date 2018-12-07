package io.github.wulkanowy.utils

import io.github.wulkanowy.data.db.entities.AttendanceSummary

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
    return calculatePercentage(sumByDouble { it.allPresences }, sumByDouble { it.allAbsences })
}

private fun calculatePercentage(presence: Double, absence: Double): Double {
    return if ((presence + absence) == 0.0) 0.0 else (presence / (presence + absence)) * 100
}


