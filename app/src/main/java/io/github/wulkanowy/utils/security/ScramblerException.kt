package io.github.wulkanowy.utils.security

class ScramblerException : Exception {
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(message: String) : super(message)
}
