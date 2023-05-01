package cloud.dreamcare.bunkord.serializers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import dev.kord.common.entity.Snowflake

public class SnowflakeSerializer public constructor(t: Class<Snowflake?>? = null) : StdSerializer<Snowflake>(t) {
    override fun serialize(snowflake: Snowflake, jsonGenerator: JsonGenerator, serializerProvider: SerializerProvider) {
        jsonGenerator.writeNumber(snowflake.value.toLong())
    }
}

@Suppress("VulnerableCodeUsages")
public class SnowflakeDeserializer public constructor(vc: Class<*>? = null) : StdDeserializer<Snowflake?>(vc) {
    override fun deserialize(parser: JsonParser, ctxt: DeserializationContext?): Snowflake {
        return Snowflake(parser.valueAsLong)
    }
}
