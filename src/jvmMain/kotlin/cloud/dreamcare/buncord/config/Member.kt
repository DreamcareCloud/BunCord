package cloud.dreamcare.buncord.config

import cloud.dreamcare.buncord.configuration
import com.fasterxml.jackson.annotation.JsonIgnore

public data class Member(
    public val id: Long,
    public var displayName: String? = null,
) {
    @JsonIgnore
    public fun getUser(): User {
        return configuration.users.getOrPut(id) { User(id) }
    }
}

