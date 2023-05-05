package cloud.dreamcare.bunkord.config.role

import cloud.dreamcare.bunkord.config.Role
import cloud.dreamcare.bunkord.entity.Emoji

public data class RoleReaction(
    public val emoji: Emoji,
    public var description: String? = null,
    public var role: Role? = null
)
