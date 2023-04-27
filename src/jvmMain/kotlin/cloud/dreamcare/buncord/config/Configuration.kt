package cloud.dreamcare.buncord.config

import cloud.dreamcare.buncord.configuration
import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import dev.kord.common.entity.Snowflake
import kotlinx.datetime.Clock
import java.io.File
import java.nio.file.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.exists

private val objectMapper = jacksonObjectMapper().apply {
    setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
}

private lateinit var configPath: File

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public data class Configuration(
    public val guilds: MutableMap<Long, Guild> = mutableMapOf(),
    public val users: MutableMap<Long, User> = mutableMapOf(),
    private var saveDate: Long = Clock.System.now().epochSeconds
) {
    public fun save() {
        saveDate = Clock.System.now().epochSeconds
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(configPath, this)
    }

    public fun load(path: Path): Configuration {
        if (!path.exists()) {
            path.createDirectory()
        }

        configPath = File("${path}/config.json")
        if (!configPath.exists()) {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(configPath, this)
        }

        return objectMapper.readValue<Configuration>(File("${path}/config.json"))
    }

    public fun guild(id: Snowflake): Guild {
        return guild(id.value.toLong())
    }

    public fun guild(id: Long): Guild {
        return guilds.getOrPut(id) { Guild(id) }
    }
}
