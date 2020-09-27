package io.github.wulkanowy.data.pojos

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Contributor(
    val displayName: String,
    val githubUsername: String
)
