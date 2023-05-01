package cloud.dreamcare.bunkord.extensions

import dev.kord.core.Kord
import dev.kord.core.entity.Emoji
import dev.kord.core.entity.GuildEmoji
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.entity.StandardEmoji
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapMerge

@OptIn(FlowPreview::class)
public suspend fun Kord.isAvailableEmoji(emoji: ReactionEmoji): Boolean {
    if (emoji is ReactionEmoji.Unicode) {
        return true
    }

    return guilds.flatMapMerge { it.emojis }
        .filter { it.id == (emoji as ReactionEmoji.Custom).id }
        .filter { it.name == (emoji as ReactionEmoji.Custom).name }
        .filter { it.isAnimated == (emoji as ReactionEmoji.Custom).isAnimated }
        .count { it.isAvailable } != 0
}

@OptIn(FlowPreview::class)
public suspend fun Kord.isAvailableEmoji(emoji: Emoji): Boolean {
    if (emoji is StandardEmoji) {
        return true
    }

    return guilds.flatMapMerge { it.emojis }
        .filter { it.id == (emoji as GuildEmoji).id }
        .filter { it.name == (emoji as GuildEmoji).name }
        .filter { it.isAnimated == (emoji as GuildEmoji).isAnimated }
        .count { it.isAvailable } != 0
}
