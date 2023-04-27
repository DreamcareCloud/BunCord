package cloud.dreamcare.bunkord.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import kotlinx.datetime.*

public class InstantSerializer public constructor(t: Class<Instant?>? = null) : StdSerializer<Instant>(t) {
    override fun serialize(instant: Instant, jsonGenerator: JsonGenerator, serializerProvider: SerializerProvider) {
        jsonGenerator.writeString(instant.toLocalDateTime(TimeZone.UTC).toString())
    }
}

@Suppress("VulnerableCodeUsages")
public class InstantDeserializer public constructor(vc: Class<*>? = null) : StdDeserializer<Instant?>(vc) {
    override fun deserialize(parser: JsonParser, ctxt: DeserializationContext?): Instant {
        return LocalDateTime.parse(parser.valueAsString).toInstant(TimeZone.UTC)
    }
}
