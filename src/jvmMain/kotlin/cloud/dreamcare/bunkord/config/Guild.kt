package cloud.dreamcare.bunkord.config

import cloud.dreamcare.bunkord.config.role.RoleMenu
import dev.kord.common.entity.Snowflake

public data class Guild(
    public val id: Snowflake,
    public var name: String? = null,
    public val roles: MutableMap<Snowflake, Role> = mutableMapOf(),
    public val members: MutableMap<Snowflake, Member> = mutableMapOf(),
    public val roleMenus: MutableMap<Snowflake, RoleMenu> = mutableMapOf(),
) {
    public fun delete() {
        configuration.guilds.remove(id)
    }

    public fun member(id: Snowflake): Member {
        return members.getOrPut(id) { Member(id) }
    }

    public fun getRole(id: Snowflake): Role {
        return roles.getOrPut(id) { Role(id) }
    }

    public fun removeRoleMenu(id: Snowflake) {
        roleMenus.remove(id)
    }
}
