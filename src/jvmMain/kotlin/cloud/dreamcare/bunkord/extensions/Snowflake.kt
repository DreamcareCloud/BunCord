package cloud.dreamcare.bunkord.extensions

import dev.kord.common.entity.Snowflake

public fun Snowflake.asChannelMention(): String {
    return "<#$value>"
}

public fun Snowflake.asRoleMention(): String {
    return "<@&$value>"
}

public fun Snowflake.asUserMention(): String {
    return "<@$value>"
}
