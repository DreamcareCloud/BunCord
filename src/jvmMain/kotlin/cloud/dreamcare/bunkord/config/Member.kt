package cloud.dreamcare.bunkord.config

import com.fasterxml.jackson.annotation.JsonIgnore
import dev.kord.common.entity.Snowflake
import kotlinx.datetime.Instant

public data class Member(
    public val id: Snowflake,
    public var active: Boolean = true,
    public var displayName: String? = null,
    public var joinedAt: Instant? = null,
    public var leftAt: Instant? = null,
    public val roles: MutableMap<Snowflake, Role> = mutableMapOf(),
) {
    @JsonIgnore
    public fun getUser(): User {
        return configuration.users.getOrPut(id) { User(id) }
    }

    public fun getRole(id: Snowflake): Role {
        return roles.getOrPut(id) { Role(id) }
    }

    public fun getHighestPositionedRole(): Role {
        return roles.values.toList().sortedByDescending { it.position }.first()
    }
}
