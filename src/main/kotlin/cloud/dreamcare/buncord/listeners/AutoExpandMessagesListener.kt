package cloud.dreamcare.buncord.listeners

import cloud.dreamcare.buncord.dsl.listeners
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.SubscribeEvent
import org.springframework.stereotype.Component
import java.util.regex.Pattern

@Component
class AutoExpandMessagesListener {
    @SubscribeEvent
    fun autoExpand() = listeners {
        on<MessageReceivedEvent>()
            .filter { !it.isFromGuild || !it.author.isBot || !it.author.isSystem || !it.isWebhookMessage }
            .filter { it.message.contentRaw.isNotEmpty() }
            .subscribe { event ->
                extractUrls(event.jda, event.message.contentRaw)
                    ?.filter { event.guild == it.keys.first() } // only expand messages from own guild
                    ?.forEach {
                    val channel: MessageChannel = it.values.first().keys.first()
                    channel.retrieveMessageById(it.values.first().values.first()).queue { message ->
                        val embedBuilder: EmbedBuilder = EmbedBuilder()
                            .setAuthor(
                                "Message posted by %s".format(message.author.name),
                                message.jumpUrl,
                                "https://cdn3.emoji.gg/emojis/7889-discord-chat.png"
                            )
                            .setThumbnail(message.author.effectiveAvatarUrl)
                            .setDescription(message.contentDisplay)
                            .setFooter("Originally posted in: %s".format(channel.name))
                            .setTimestamp(message.timeEdited ?: message.timeCreated)

                        message.attachments.forEach { attachment -> embedBuilder.setImage(attachment.proxyUrl) }

                        message.embeds.forEach { messageEmbed ->
                            embedBuilder.addBlankField(false)
                            messageEmbed.fields.forEach { field -> embedBuilder.addField(field) }
                        }

                        event.channel.sendMessageEmbeds(embedBuilder.build())
                            .setMessageReference(event.messageId)
                            .mentionRepliedUser(false)
                            .queue()
                    }
                }
            }
    }

    private fun extractUrls(jda: JDA, input: String): List<Map<Guild, Map<MessageChannel, Long>>>? {
        val result: MutableList<Map<Guild, Map<MessageChannel, Long>>> = ArrayList()

        val matcher = Pattern.compile("\\bdiscord\\.com/channels/([0-9]+)/([0-9]+)/([0-9]+)\\b").matcher(input)
        while (matcher.find()) {
            val guild: Guild = jda.getGuildById(java.lang.Long.valueOf(matcher.group(1))) ?: continue
            val channel: MessageChannel = guild.getChannelById(MessageChannel::class.java, java.lang.Long.valueOf(matcher.group(2))) ?: continue

            result.add(
                java.util.Map.of(
                    guild, // Guild
                    java.util.Map.of(
                        channel, // Channel
                        java.lang.Long.valueOf(matcher.group(3)) // MessageId
                    )
                )
            )
        }
        return result
    }
}
