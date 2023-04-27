package cloud.dreamcare.buncord.config

import cloud.dreamcare.buncord.configuration
import dev.kord.common.entity.Snowflake

public data class Guild(
    public val id: Long,
    public var name: String? = null,
    public val member: MutableMap<Long, Member> = mutableMapOf()
) {
    public fun delete() {
        configuration.guilds.remove(id)
    }

    public fun member(id: Snowflake): Member {
        return member(id.value.toLong())
    }

    public fun member(id: Long): Member {
        return member.getOrPut(id) { Member(id) }
    }
}
