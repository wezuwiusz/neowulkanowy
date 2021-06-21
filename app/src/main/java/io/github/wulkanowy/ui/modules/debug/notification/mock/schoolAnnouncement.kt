package io.github.wulkanowy.ui.modules.debug.notification.mock

import io.github.wulkanowy.data.db.entities.SchoolAnnouncement
import java.time.LocalDate

val debugSchoolAnnouncementItems = listOf(
    generateAnnouncement("Dzień wolny od zajęć dydaktycznych", "Dzień wolny od zajęć dydaktycznych\n03.05.2021 - poniedziałek"),
    generateAnnouncement("Zasady bezpieczeństwa", "Wszyscy uczniowie są zobowiązani do noszenia maseczek"),
    generateAnnouncement("Święto szkoły", "W najbliższych dniach obchodzimy święto szkoły, podczas którego..."),
    generateAnnouncement("Rocznica odzyskania przez szkołę sztandaru", "Juz niedługo, bo za tydzień, a dokładnie za 8 dni..."),
    generateAnnouncement("Ogłoszenie w sprawie otwarcia stołówki", "Wszyscy uczniowie zainteresowani obiadami w szkole..."),
    generateAnnouncement("Uczniowie proszeni do sekretariatu", "Kuba i Jacek z klasy czwartej proszeni do dyrektora w trybie pilnym"),
    generateAnnouncement("Dzień wolny od zajęć dydaktycznych", "Dzień wolny od zajęć dydaktycznych\n21.06.2021 - poniedziałek"),
    generateAnnouncement("Zasady bezpieczeństwa", "Wszyscy uczniowie są zobowiązani do zdjęcia maseczek"),
    generateAnnouncement("Święto państwowe", "W najbliższych dniach obchodzimy święto państwowe, podczas którego..."),
    generateAnnouncement("Uczniowie proszeni do sekretariatu", "Kuba i Jacek z klasy czwartej proszeni do dyrektora w trybie wolnym"),
)

private fun generateAnnouncement(subject: String, content: String) = SchoolAnnouncement(
    subject = subject,
    content = content,
    studentId = 0,
    date = LocalDate.now()
)
