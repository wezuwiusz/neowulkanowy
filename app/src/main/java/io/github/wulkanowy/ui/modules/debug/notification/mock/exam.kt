package io.github.wulkanowy.ui.modules.debug.notification.mock

import io.github.wulkanowy.data.db.entities.Exam
import java.time.LocalDate

val debugExamItems = listOf(
    generateExam("Matematyka", "Figury na płaszczyźnie"),
    generateExam("Język angielski", "czasowniki nieregularne 1 część"),
    generateExam("Geografia", "Opolszczyzna - mapa"),
    generateExam("Sieci komputerowe", "Zaciskanie erjotek"),
    generateExam("Systemy operacyjne", "Instalacja ubuntu 16.04"),
    generateExam("Język niemiecki", "oral exam"),
    generateExam("Biologia", "Budowa koniczyny"),
    generateExam("Chemia", "synteza płynnego zaliczenia"),
    generateExam("Fizyka", "telekineza"),
    generateExam("Matematyka", "Liczby zespolone i pochodne piątego rzędu"),
)

private fun generateExam(subject: String, description: String) = Exam(
    subject = subject,
    description = description,
    studentId = 0,
    diaryId = 0,
    date = LocalDate.now(),
    entryDate = LocalDate.now(),
    group = "",
    type = "",
    teacher = "",
    teacherSymbol = ""
)
