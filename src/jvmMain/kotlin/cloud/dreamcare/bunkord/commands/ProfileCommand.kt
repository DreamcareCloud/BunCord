package cloud.dreamcare.bunkord.commands

import cloud.dreamcare.bunkord.dsl.Command
import cloud.dreamcare.bunkord.dsl.Listener
import cloud.dreamcare.bunkord.dsl.createGlobalChatInputCommand
import cloud.dreamcare.bunkord.dsl.listener
import cloud.dreamcare.bunkord.value.EmojiValue
import dev.kord.common.DiscordTimestampStyle
import dev.kord.common.entity.Permission
import dev.kord.common.toMessageFormat
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.Image
import dev.kord.rest.builder.interaction.user
import dev.kord.rest.builder.message.modify.embed

public class ProfileCommand {
    public fun register(): Command = createGlobalChatInputCommand("profile", "Shows user profile") {
        user("user", "User to lookup")
        defaultMemberPermissions.apply {
            Permission.SendMessages
        }
    }

    public fun command(): Listener = listener {
        onCommand<GuildChatInputCommandInteractionCreateEvent>("profile") {
            val response = interaction.deferPublicResponse()
            val command = interaction.command
            val member = command.users.getOrDefault("user", interaction.user)
                .withStrategy(EntitySupplyStrategy.cachingRest).fetchUser().asMember(interaction.guildId)
            val memberEntity = configuration.guild(interaction.guildId).member(member.id)

            response.respond {
                embed {
                    title = "${member.displayName}'s profile"
                    color = member.accentColor
                    thumbnail {
                        url = (member.memberAvatar ?: member.avatar)?.cdnUrl?.toUrl()
                            ?: member.defaultAvatar.cdnUrl.toUrl {
                                format = Image.Format.PNG
                            }
                    }

                    field {
                        inline = true
                        name = "Rank"
                        value = interaction.guild.getRole(memberEntity.getHighestPositionedRole().id).mention
                        if (member.isOwner()) {
                            value += " ${EmojiValue.CROWN.emoji}"
                        }
                        if (member.isBot) {
                            value += " ${EmojiValue.BOT.emoji}"
                        }
                    }
                    field {
                        inline = true
                        name = "Booster ${EmojiValue.BOOST.emoji}"
                        value = if (null == member.premiumSince) {
                            EmojiValue.NO.emoji
                        } else {
                            "${EmojiValue.YES.emoji} ${member.premiumSince!!.toMessageFormat(DiscordTimestampStyle.LongDate)}"
                        }
                    }
                    field {
                        inline = true
                        name = "Birthday"
                        value = member.joinedAt.toMessageFormat(DiscordTimestampStyle.LongDate)
                    }

                    field {
                        inline = true
                        name = "Member Since"
                        value = member.joinedAt.toMessageFormat(DiscordTimestampStyle.LongDate)
                    }
                    field { inline = true }
                    field { inline = true }

                    field {
                        name = "Roles"
                        value = memberEntity.roles.values.toList()
                            .sortedByDescending { it.position }
                            .map { interaction.guild.getRole(it.id) }
                            .joinToString(" ") { it.mention }
                    }
                }
            }
        }
    }
}
