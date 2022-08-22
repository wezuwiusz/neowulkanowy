package io.github.wulkanowy.ui.modules.debug.notification.mock

import io.github.wulkanowy.data.db.entities.Message
import java.time.Instant

val debugMessageItems = listOf(
    generateMessage("Kowalski Jan", "Tytuł"),
    generateMessage("Nazwisko Imię", "Tytuł wiadomości"),
    generateMessage("Malinowski Kazimierz", "Nakrętki"),
    generateMessage("Jastębowszki Orest", "Prośba do uczniów o pomoc przy projekcie"),
    generateMessage("Metylowy Oranż", "Pozew o plagiat"),
    generateMessage("VULCAN", "Uwaga na nieautoryzowane aplikacje"),
    generateMessage("Mama", "Zacznij się w końcu uczyć do matury!!!11"),
    generateMessage("Tata", "Kupisz mi coś w sklepie?"),
    generateMessage("Wychowawca", "Upomnienie od wychowawcy za nieobecności"),
    generateMessage("Kochanowska Joanna", "Poprawa rozprawki - termin"),
)

private fun generateMessage(sender: String, subject: String) = Message(
    subject = subject,
    messageId = 123,
    date = Instant.now(),
    folderId = 0,
    unread = true,
    hasAttachments = false,
    messageGlobalKey = "",
    correspondents = sender,
    mailboxKey = "",
)
