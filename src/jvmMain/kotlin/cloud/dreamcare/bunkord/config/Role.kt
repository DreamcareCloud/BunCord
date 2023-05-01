package cloud.dreamcare.bunkord.config

import dev.kord.common.entity.Snowflake

public data class Role(
    public val id: Snowflake,
    public var name: String? = null,
    public var position: Int? = null,
)
