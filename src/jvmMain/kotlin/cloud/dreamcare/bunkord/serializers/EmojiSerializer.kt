package cloud.dreamcare.bunkord.serializers

import cloud.dreamcare.bunkord.entity.Emoji
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer

public class EmojiSerializer public constructor(t: Class<Emoji?>? = null) : StdSerializer<Emoji>(t) {
    override fun serialize(emoji: Emoji, jsonGenerator: JsonGenerator, serializerProvider: SerializerProvider) {
        jsonGenerator.writeString(emoji.markdown)
    }
}

@Suppress("VulnerableCodeUsages")
public class EmojiDeserializer public constructor(vc: Class<*>? = null) : StdDeserializer<Emoji?>(vc) {
    override fun deserialize(parser: JsonParser, ctxt: DeserializationContext?): Emoji {
        return Emoji.from(parser.valueAsString)
    }
}
