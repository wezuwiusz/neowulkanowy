package io.github.wulkanowy.ui.modules.debug.notification.mock

import io.github.wulkanowy.data.db.entities.Homework
import java.time.LocalDate

val debugHomeworkItems = listOf(
    generateHomework("Chemia", "Test diagnozujący i Rozdział I do 30.10"),
    generateHomework("Etyka", "Notatka własna do zajęć o ks. Jerzym Popiełuszko"),
    generateHomework("Język angielski", "Zadania egzaminacyjne"),
    generateHomework("Metodologia programowania", "Wszystkie instrukcje"),
    generateHomework("Język polski", "Notatka własna na temat Wokulskiego z lektury Lalka"),
    generateHomework("Systemy operacyjne", "Sprawozdanie z wykonania ćwiczenia nr 21.137"),
    generateHomework("Matematyka", "Zadania od strony 1 do 128"),
    generateHomework("Język niemiecki", "Opis swoich wakacji - dialog z kolegą"),
    generateHomework("Język angielski", "Opis swoich wakacji - dialog z kolegą"),
    generateHomework("Wychowanie fizyczne", "Notatka na temat skoku w dald"),
    generateHomework("Biologia", "Notatka na temat grzechotnika"),
)

private fun generateHomework(subject: String, content: String) = Homework(
    subject = subject,
    content = content,
    semesterId = 0,
    studentId = 0,
    date = LocalDate.now(),
    entryDate = LocalDate.now(),
    teacher = "",
    teacherSymbol = "",
    attachments = listOf(),
)
