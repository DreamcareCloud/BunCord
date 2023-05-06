package cloud.dreamcare.bunkord.config.greeter

import cloud.dreamcare.bunkord.config.Role
import dev.kord.common.entity.Snowflake

public data class Greeter(
    public var channel: Snowflake? = null,
    public var onJoin: Boolean = false,
    public var onRole: Role? = null,
    public var message: String = "",
)
