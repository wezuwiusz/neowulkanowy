package io.github.wulkanowy.ui.modules.message.send

import android.graphics.drawable.Drawable
import android.net.Uri
import com.pchmn.materialchips.model.ChipInterface
import io.github.wulkanowy.data.db.entities.Recipient

class RecipientChip(var recipient: Recipient) : ChipInterface {

    override fun getAvatarDrawable(): Drawable? = null

    override fun getAvatarUri(): Uri? = null

    override fun getId(): Any = recipient.id

    override fun getLabel(): String = recipient.name

    override fun getInfo(): String {
        return recipient.realName.run {
            substringBeforeLast("-").let { sub ->
                when {
                    (sub == this) -> this
                    (sub.indexOf('(') != -1) -> indexOf("(").let { substring(if (it != -1) it else 0) }
                    (sub.indexOf('[') != -1) -> indexOf("[").let { substring(if (it != -1) it else 0) }
                    else -> substringAfter('-')
                }
            }.trim()
        }
    }
}
