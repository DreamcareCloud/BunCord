package cloud.dreamcare.bunkord.extensions

import cloud.dreamcare.bunkord.entity.Emoji
import cloud.dreamcare.bunkord.serializers.*
import com.fasterxml.jackson.databind.module.SimpleModule
import dev.kord.common.entity.Snowflake
import kotlinx.datetime.Instant

public fun SimpleModule.emojiSerializer() {
    addSerializer(Emoji::class.java, EmojiSerializer())
    addDeserializer(Emoji::class.java, EmojiDeserializer())
}

public fun SimpleModule.instantSerializer() {
    addSerializer(Instant::class.java, InstantSerializer())
    addDeserializer(Instant::class.java, InstantDeserializer())
}

public fun SimpleModule.snowflakeSerializer() {
    addSerializer(Snowflake::class.java, SnowflakeSerializer())
    addDeserializer(Snowflake::class.java, SnowflakeDeserializer())
}
