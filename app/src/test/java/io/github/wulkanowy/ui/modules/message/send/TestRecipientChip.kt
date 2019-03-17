package io.github.wulkanowy.ui.modules.message.send

import io.github.wulkanowy.data.db.entities.Recipient
import org.junit.Assert.assertEquals
import org.junit.Test

class TestRecipientChip {

    @Test
    fun testRecipientChipInfo() {
        assertEquals("Uczeń", getRecipientChip("Kowalski Jan - Uczeń").info)
        assertEquals("(JK) - pracownik [Fake123456]", getRecipientChip("Kowalski Jan (JK) - pracownik [Fake123456]").info)
        assertEquals("[KK] - pracownik (Fake123456)", getRecipientChip("Kowalska Karolina [KK] - pracownik (Fake123456)").info)
        assertEquals("(BK) - Nauczyciel [Fake123456]", getRecipientChip("Kowal-Mazur Barbara (BK) - Nauczyciel [Fake123456]").info)
    }

    private fun getRecipientChip(realName: String): RecipientChip {
        return RecipientChip(Recipient(0, "", "", realName, 0, 0, 0, ""))
    }
}
