package cloud.dreamcare.bunkord.config

import dev.kord.common.entity.Snowflake

public data class User(
    public val id: Snowflake,
    public var username: String? = null,
    public var discriminator: String? = null,
)
