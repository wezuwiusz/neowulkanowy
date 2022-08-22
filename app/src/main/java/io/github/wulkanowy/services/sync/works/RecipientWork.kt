package io.github.wulkanowy.services.sync.works

import io.github.wulkanowy.data.db.entities.MailboxType
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.MailboxRepository
import io.github.wulkanowy.data.repositories.RecipientRepository
import javax.inject.Inject

class RecipientWork @Inject constructor(
    private val mailboxRepository: MailboxRepository,
    private val recipientRepository: RecipientRepository
) : Work {

    override suspend fun doWork(student: Student, semester: Semester, notify: Boolean) {
        mailboxRepository.refreshMailboxes(student)

        val mailbox = mailboxRepository.getMailbox(student)

        recipientRepository.refreshRecipients(student, mailbox, MailboxType.EMPLOYEE)
    }
}
