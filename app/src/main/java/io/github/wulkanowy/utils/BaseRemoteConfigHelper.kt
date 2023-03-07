package io.github.wulkanowy.utils

abstract class BaseRemoteConfigHelper {

    open fun initialize() = Unit

    open val userAgentTemplate: String
        get() = RemoteConfigDefaults.USER_AGENT_TEMPLATE.value as String
}
