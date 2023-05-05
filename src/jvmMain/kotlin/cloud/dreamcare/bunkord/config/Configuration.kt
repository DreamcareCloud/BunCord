package cloud.dreamcare.bunkord.config

import cloud.dreamcare.bunkord.extensions.emojiSerializer
import cloud.dreamcare.bunkord.extensions.instantSerializer
import cloud.dreamcare.bunkord.extensions.snowflakeSerializer
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
    SimpleModule().apply {
        emojiSerializer()
        instantSerializer()
        snowflakeSerializer()
    }.also {
        registerModule(it)
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
