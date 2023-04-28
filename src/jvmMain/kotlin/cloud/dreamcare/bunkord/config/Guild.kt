package cloud.dreamcare.bunkord.config

import dev.kord.common.entity.Snowflake

public data class Guild(
    public val id: Long,
    public var name: String? = null,
    public val roles: MutableMap<Long, Role> = mutableMapOf(),
    public val members: MutableMap<Long, Member> = mutableMapOf()
) {
    public fun delete() {
        configuration.guilds.remove(id)
    }

    public fun member(id: Snowflake): Member {
        return member(id.value.toLong())
    }

    public fun member(id: Long): Member {
        return members.getOrPut(id) { Member(id) }
    }

    public fun getRole(id: Snowflake): Role {
        return getRole(id.value.toLong())
    }

    public fun getRole(id: Long): Role {
        return roles.getOrPut(id) { Role(id) }
    }
}
