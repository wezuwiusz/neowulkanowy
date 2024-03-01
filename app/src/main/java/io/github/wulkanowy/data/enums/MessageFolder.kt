package io.github.wulkanowy.data.enums

enum class MessageFolder(val id: Int = 1) {
    RECEIVED(1),
    SENT(2),
    TRASHED(3),
    ;

    companion object {
        fun byId(id: Int) = entries.first { it.id == id }
    }
}
