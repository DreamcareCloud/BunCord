package cloud.dreamcare.bunkord.listeners

import cloud.dreamcare.bunkord.config.greeter.Greeter
import cloud.dreamcare.bunkord.dsl.Listener
import cloud.dreamcare.bunkord.dsl.listener
import cloud.dreamcare.bunkord.extensions.asChannelMention
import cloud.dreamcare.bunkord.extensions.asRoleMention
import cloud.dreamcare.bunkord.value.EmojiValue
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.getChannelOf
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.Member
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.event.guild.MemberJoinEvent
import dev.kord.core.event.guild.MemberUpdateEvent
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.Image
import dev.kord.rest.builder.message.create.embed
import dev.kord.rest.builder.message.modify.embed

public class GreeterListener {
    public fun menu(): Listener = listener {
        onCommand<GuildChatInputCommandInteractionCreateEvent>("setup", "greeter") {
            val response = interaction.deferEphemeralResponse()
            val command = interaction.command

            configuration.guild(interaction.guildId).greeter = Greeter().apply {
                channel = command.channels["channel"]?.id
                onJoin = command.booleans["on-join"] ?: false
                onRole = command.roles["on-role"]?.let { cloud.dreamcare.bunkord.config.Role(it.id, it.name, it.getPosition()) }
                message = command.strings["message"] ?: ""
            }

            configuration.save()

            val greeter = configuration.guild(interaction.guildId).greeter!!

            response.respond {
                embed {
                    title = "Greeter"

                    field {
                        inline = false
                        name = "Channel"
                        value = greeter.channel?.asChannelMention() ?: EmojiValue.NO.emoji
                    }

                    field {
                        inline = false
                        name = "On Join"
                        value = if (greeter.onJoin) { EmojiValue.YES.emoji } else { EmojiValue.NO.emoji }
                    }

                    field {
                        inline = false
                        name = "On Role Assignment"
                        value = if (null != greeter.onRole) {
                            "${EmojiValue.YES.emoji} ${greeter.onRole!!.id.asRoleMention()}"
                        } else {
                            EmojiValue.NO.emoji
                        }
                    }
                }
            }
        }
    }

    public fun greet(): Listener = listener {
        on<MemberJoinEvent> {
            val greeter = configuration.guild(guildId).greeter ?: return@on
            if (null == greeter.channel) { return@on }
            if (!greeter.onJoin) { return@on }
            val member = member.withStrategy(EntitySupplyStrategy.cachingRest).fetchMember(guildId)

            greet(guild.getChannelOf<TextChannel>(greeter.channel!!), member, greeter)
        }

        on<MemberUpdateEvent> {
            if (null == old) { return@on }
            val greeter = configuration.guild(guildId).greeter ?: return@on
            if (null == greeter.channel) { return@on }
            val member = member.withStrategy(EntitySupplyStrategy.cachingRest).fetchMember(guildId)

            if (!member.roleIds.minus(old!!.roleIds).contains(greeter.onRole?.id)) {
                return@on
            }

            greet(guild.getChannelOf<TextChannel>(greeter.channel!!), member, greeter)
        }
    }

    private suspend fun greet(channel: TextChannel, member: Member, config: Greeter) {
        channel.createMessage {
            content = "Welcome to the **${member.getGuild().name}** server, ${member.mention}!"

            embed {
                title = "Welcome ${member.displayName}!"
                description = config.message

                color = member.accentColor
                thumbnail {
                    url = (member.memberAvatar ?: member.avatar)?.cdnUrl?.toUrl()
                        ?: member.defaultAvatar.cdnUrl.toUrl {
                            format = Image.Format.PNG
                        }
                }

                if (null != member.getGuild().rulesChannelId) {
                    field {
                        name = "Rules"
                        value = "Please read all the rules in ${member.getGuild().rulesChannelId!!.asChannelMention()}!"
                    }
                }
            }
        }
    }
}
