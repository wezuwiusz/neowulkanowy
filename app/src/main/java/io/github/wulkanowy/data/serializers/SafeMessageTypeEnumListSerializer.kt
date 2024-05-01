package io.github.wulkanowy.data.serializers

import io.github.wulkanowy.data.enums.MessageType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@OptIn(ExperimentalSerializationApi::class)
object SafeMessageTypeEnumListSerializer : KSerializer<List<MessageType>> {

    private val serializer = ListSerializer(String.serializer())

    override val descriptor = serializer.descriptor

    override fun serialize(encoder: Encoder, value: List<MessageType>) {
        encoder.encodeNotNullMark()
        serializer.serialize(encoder, value.map { it.name })
    }

    override fun deserialize(decoder: Decoder): List<MessageType> =
        serializer.deserialize(decoder).mapNotNull { enumName ->
            MessageType.entries.find { it.name == enumName }
        }
}
