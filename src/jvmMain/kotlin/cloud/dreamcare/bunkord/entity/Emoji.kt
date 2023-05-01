package cloud.dreamcare.bunkord.entity

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.ReactionEmoji
import io.github.seisuke.kemoji.EmojiManager

public sealed class Emoji {
    /**
     * Either the unicode representation if it's a [Unicode] emoji or the emoji name if it's a [Custom] emoji.
     */
    public abstract val name: String
    public abstract val mention: String
    public abstract val markdown: String

    public abstract fun toReactionEmoji(): ReactionEmoji

    public data class Custom(val id: Snowflake, override val name: String, val isAnimated: Boolean) : Emoji() {
        override val mention: String
            get() = if (isAnimated) "<a:$name:$id>" else "<:$name:$id>"
        override val markdown: String get() = mention

        override fun toReactionEmoji(): ReactionEmoji {
            return ReactionEmoji.Custom(id, name, isAnimated)
        }

        override fun toString(): String = "Custom(id=$id, name=$name, isAnimated=$isAnimated)"
    }

    public data class Unicode(override val name: String) : Emoji() {
        override val mention: String get() = name
        override val markdown: String get() = ":${EmojiManager.getByUnicode(name)!!.aliases.first()}:"

        override fun toReactionEmoji(): ReactionEmoji {
            return ReactionEmoji.Unicode(name)
        }

        override fun toString(): String = "Unicode(emoji=$name, name=$markdown)"
    }

    public companion object {
        public fun from(emoji: String): Emoji = when (emoji.startsWith("<") && emoji.endsWith(">") && 3 == emoji.split(":").size) {
            true -> Custom(
                Snowflake(emoji.removeSurrounding("<", ">").split(":")[2]),
                emoji.removeSurrounding("<", ">").split(":")[1],
                "a" == emoji.removeSurrounding("<", ">").split(":")[0])
            false -> when(emoji.startsWith(":") && emoji.endsWith(":")) {
                true -> Unicode(EmojiManager.getForAlias(emoji.removeSurrounding(":"))!!.emoji)
                false -> Unicode(emoji)
            }
        }
    }
}
