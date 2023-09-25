package io.github.wulkanowy.data.pojos

import kotlinx.serialization.Serializable

@Serializable
data class LoginEvent(
    val uuid: String,
    val schoolName: String,
    val schoolShort: String,
    val schoolAddress: String,
    val scraperBaseUrl: String,
    val symbol: String,
    val schoolId: String,
    val loginType: String,
)

@Serializable
data class IntegrityRequest<T>(
    val tokenString: String,
    val data: T,
)
