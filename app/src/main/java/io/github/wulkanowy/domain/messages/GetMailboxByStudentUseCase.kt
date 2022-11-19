package io.github.wulkanowy.domain.messages

import io.github.wulkanowy.data.db.dao.MailboxDao
import io.github.wulkanowy.data.db.entities.Mailbox
import io.github.wulkanowy.data.db.entities.Student
import javax.inject.Inject

class GetMailboxByStudentUseCase @Inject constructor(
    private val mailboxDao: MailboxDao,
) {

    suspend operator fun invoke(student: Student): Mailbox? {
        return mailboxDao.loadAll(student.email)
            .filterByStudent(student)
    }

    private fun List<Mailbox>.filterByStudent(student: Student): Mailbox? {
        val normalizedStudentName = student.studentName.normalizeStudentName()

        return find {
            it.studentName.normalizeStudentName() == normalizedStudentName
        } ?: singleOrNull {
            it.studentName.getFirstAndLastPart() == normalizedStudentName.getFirstAndLastPart()
        } ?: singleOrNull {
            it.studentName.getReversedName() == normalizedStudentName
        } ?: singleOrNull {
            it.studentName.getUnauthorizedVersion() == normalizedStudentName
        }
    }

    private fun String.normalizeStudentName(): String {
        return trim().split(" ")
            .filter { it.isNotBlank() }
            .joinToString(" ") { part ->
                part.lowercase().replaceFirstChar { it.uppercase() }
            }
    }

    private fun String.getFirstAndLastPart(): String {
        val parts = normalizeStudentName().split(" ")

        val endParts = parts.filterIndexed { i, _ ->
            i == 0 || parts.size - 1 == i
        }
        return endParts.joinToString(" ")
    }

    private fun String.getReversedName(): String {
        val parts = normalizeStudentName().split(" ")

        return parts
            .asReversed()
            .joinToString(" ")
    }

    private fun String.getUnauthorizedVersion(): String {
        return normalizeStudentName().split(" ")
            .joinToString(" ") {
                it.first() + "*".repeat(it.length - 1)
            }
    }
}
