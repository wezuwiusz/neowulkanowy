package io.github.wulkanowy.data.serializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.nullable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate

@OptIn(ExperimentalSerializationApi::class)
object LocalDateSerializer : KSerializer<LocalDate?> {

    override val descriptor = PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.LONG).nullable

    override fun serialize(encoder: Encoder, value: LocalDate?) {
        if (value == null) {
            encoder.encodeNull()
        } else {
            encoder.encodeNotNullMark()
            encoder.encodeLong(value.toEpochDay())
        }
    }

    override fun deserialize(decoder: Decoder): LocalDate? =
        if (decoder.decodeNotNullMark()) {
            LocalDate.ofEpochDay(decoder.decodeLong())
        } else {
            decoder.decodeNull()
        }
}