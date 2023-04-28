package cloud.dreamcare.bunkord.config

import com.fasterxml.jackson.annotation.JsonIgnore
import dev.kord.common.entity.Snowflake
import kotlinx.datetime.Instant

public data class Member(
    public val id: Long,
    public var active: Boolean = true,
    public var displayName: String? = null,
    public var joinedAt: Instant? = null,
    public var leftAt: Instant? = null,
    public val roles: MutableMap<Long, Role> = mutableMapOf(),
) {
    @JsonIgnore
    public fun getUser(): User {
        return configuration.users.getOrPut(id) { User(id) }
    }

    public fun getRole(id: Snowflake): Role {
        return getRole(id.value.toLong())
    }

    public fun getRole(id: Long): Role {
        return roles.getOrPut(id) { Role(id) }
    }

    public fun getHighestPositionedRole(): Role {
        return roles.values.toList().sortedByDescending { it.position }.first()
    }
}
