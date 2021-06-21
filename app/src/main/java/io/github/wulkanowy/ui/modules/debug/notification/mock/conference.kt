package io.github.wulkanowy.ui.modules.debug.notification.mock

import io.github.wulkanowy.data.db.entities.Conference
import java.time.LocalDateTime

val debugConferenceItems = listOf(
    generateConference(
        title = "Spotkanie z rodzicami/opiekunami",
        subject = "Podsumowanie I semestru - średnia klasy, oceny, frekwencja, zachowanie"
    ),
    generateConference(
        title = "ZSW",
        subject = "Pierwsze - organizacyjne zebranie z rodzicami klas pierwszych"
    ),
    generateConference(
        title = "Spotkanie z rodzicami w sprawie bójki",
        subject = "Pierwsze - i miejmy nadzieję ostatnie - zebranie w takiej sprawie"
    ),
    generateConference(
        title = "Spotkanie z rodzicami w sprawie kolejnej bójki",
        subject = "Kolejne - ale miejmy jeszcze nadzieję, że ostatnie - zebranie w takiej sprawie"
    ),
    generateConference(
        title = "Spotkanie z rodzicami w sprawie jeszcze jednej bójki",
        subject = "Proszę państwa, proszę uspokoić swoje dzieci"
    ),
    generateConference(
        title = "Spotkanie w sprawie wydalenia części uczniów",
        subject = "Proszę państwa, to jest krok ostateczny, którego nikt nie chciał się podjąć, ale ktoś musi"
    ),
    generateConference(
        title = "Spotkanie organizacyjne w drugim semestrze",
        subject = "Prezentacja na temat projektu 'Spokojnej szkoły'"
    ),
    generateConference(
        title = "Spotkanie z pierwszakami",
        subject = "Mamy sobie do pogadania"
    ),
    generateConference(
        title = "Spotkanie z rodzicami szóstoklaistów",
        subject = "Musimy przygotować dzieci do ważnej uroczystości"
    ),
    generateConference(
        title = "Spotkanie podsumowujące pracę w ciągu ostatniego roku szkolnego",
        subject = "Proszę państwa, zapraszam serdecznie na spotkanie"
    ),
)

private fun generateConference(title: String, subject: String) = Conference(
    title = title,
    subject = subject,
    studentId = 0,
    diaryId = 0,
    agenda = "",
    conferenceId = 0,
    date = LocalDateTime.now(),
    presentOnConference = "",
)
