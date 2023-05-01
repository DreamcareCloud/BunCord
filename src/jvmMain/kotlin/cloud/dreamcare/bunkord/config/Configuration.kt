package cloud.dreamcare.bunkord.config

import cloud.dreamcare.bunkord.entity.Emoji
import cloud.dreamcare.bunkord.serializers.*
import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import dev.kord.common.entity.Snowflake
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.io.File
import java.nio.file.Path

private lateinit var configPath: File
internal lateinit var configuration: Configuration
private val objectMapper = jacksonObjectMapper().apply {
    setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    SimpleModule().run {
        addSerializer(Instant::class.java, InstantSerializer())
        addDeserializer(Instant::class.java, InstantDeserializer())
        addSerializer(Emoji::class.java, EmojiSerializer())
        addDeserializer(Emoji::class.java, EmojiDeserializer())
        addSerializer(Snowflake::class.java, SnowflakeSerializer())
        addDeserializer(Snowflake::class.java, SnowflakeDeserializer())
        registerModule(this)
    }
}

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public data class Configuration(
    public val guilds: MutableMap<Snowflake, Guild> = mutableMapOf(),
    public val users: MutableMap<Snowflake, User> = mutableMapOf(),
    private var saveDate: Instant = Clock.System.now()
) {
    public fun save() {
        saveDate = Clock.System.now()
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(configPath, this)
    }

    public fun load(path: Path): Configuration {
        configPath = File("${path}/config.json")
        if (!configPath.exists()) {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(configPath, this)
        }

        return objectMapper.readValue<Configuration>(File("${path}/config.json")).also {
            configuration = it
        }
    }

    public fun reload() {
        configuration = objectMapper.readValue<Configuration>(configPath).also {
            configuration = it
        }
    }

    public fun guild(id: Snowflake): Guild {
        return guilds.getOrPut(id) { Guild(id) }
    }
}
