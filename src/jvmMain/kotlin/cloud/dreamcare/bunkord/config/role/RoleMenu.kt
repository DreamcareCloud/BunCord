package cloud.dreamcare.bunkord.config.role

import dev.kord.common.entity.Snowflake

public data class RoleMenu(
    public val channelId: Snowflake,
    public var messageId: Snowflake? = null,
    public var title: String? = null,
    public var description: String? = null,
    public val options: MutableMap<String, RoleOption> = mutableMapOf()
)
