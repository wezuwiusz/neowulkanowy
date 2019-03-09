package io.github.wulkanowy.data.repositories.message

enum class MessageFolder(val id: Int = 1) {
    RECEIVED(1),
    SENT(2),
    TRASHED(3)
}
