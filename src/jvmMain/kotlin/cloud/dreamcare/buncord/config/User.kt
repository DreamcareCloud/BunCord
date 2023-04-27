package cloud.dreamcare.buncord.config

public data class User(
    public val id: Long,
    public var username: String? = null,
    public var discriminator: String? = null,
)
