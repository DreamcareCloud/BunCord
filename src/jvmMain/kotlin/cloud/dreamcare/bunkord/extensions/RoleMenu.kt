package cloud.dreamcare.bunkord.extensions

import cloud.dreamcare.bunkord.config.configuration
import cloud.dreamcare.bunkord.config.role.RoleMenu
import cloud.dreamcare.bunkord.config.role.RoleOption
import dev.kord.core.behavior.channel.asChannelOf
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.edit
import dev.kord.core.entity.Guild
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.rest.builder.message.EmbedBuilder

public suspend fun RoleMenu.publish(guild: Guild) {
    val channel = guild.getChannel(channelId).asChannelOf<MessageChannel>()

    when (messageId) {
        null -> { channel.createMessage { embeds.add(toEmbedBuilder()) }.apply { messageId = id } }
        else -> { channel.getMessage(messageId!!).edit { embeds = mutableListOf(toEmbedBuilder()) } }
    }.also {
        configuration.save()
    }.run {
        if (reactions.map { reaction -> reaction.emoji } == toReactionEmojis()) {
            return
        }

        deleteAllReactions().also {
            toReactionEmojis().forEach { addReaction(it) }
        }
    }
}

public fun RoleMenu.getSelectedOption(emoji: ReactionEmoji): RoleOption? {
    return options.map { it.value }.find { it.emoji.toReactionEmoji() == emoji }
}

private fun RoleMenu.toEmbedBuilder(): EmbedBuilder {
    return EmbedBuilder().also {
        it.title = title
        if (description?.isEmpty() == false) {
            it.description = "$description\n\n"
        } else {
            it.description = ""
        }

        options.forEach { (_, option) ->
            it.description += "${option.emoji.mention}・${option.description ?: "<@&${option.role!!.id}>"}\n"
        }
    }
}

private fun RoleMenu.toReactionEmojis(): List<ReactionEmoji> {
    return options.map { it.value.emoji.toReactionEmoji() }
}
