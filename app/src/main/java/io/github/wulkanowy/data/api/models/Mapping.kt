package io.github.wulkanowy.data.api.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Mapping(

    @SerialName("endpoints")
    val endpoints: Map<String, Map<String, Map<String, String>>>,

    @SerialName("vTokens")
    val vTokens: Map<String, Map<String, Map<String, String>>>,

    @SerialName("vTokenScheme")
    val vTokenScheme: Map<String, Map<String, String>> = emptyMap(),

    @SerialName("vHeaders")
    val vHeaders: Map<String, Map<String, Map<String, String>>> = emptyMap(),

    @SerialName("responseMap")
    val responseMap: Map<String, Map<String, Map<String, Map<String, String>>>> = emptyMap(),
)
