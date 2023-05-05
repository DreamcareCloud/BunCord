package cloud.dreamcare.bunkord.listeners

import cloud.dreamcare.bunkord.config.role.RoleMenu
import cloud.dreamcare.bunkord.config.role.RoleReaction
import cloud.dreamcare.bunkord.dsl.Listener
import cloud.dreamcare.bunkord.dsl.listener
import cloud.dreamcare.bunkord.entity.Emoji
import cloud.dreamcare.bunkord.extensions.getSelectedOption
import cloud.dreamcare.bunkord.extensions.isAvailableEmoji
import cloud.dreamcare.bunkord.extensions.publish
import dev.kord.common.entity.Permission
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.behavior.interaction.suggestString
import dev.kord.core.event.guild.GuildCreateEvent
import dev.kord.core.event.interaction.GuildAutoCompleteInteractionCreateEvent
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.message.*
import dev.kord.core.exception.EntityNotFoundException

public class RolesListener {
    public fun menu(): Listener = listener {
        onAutoComplete<GuildAutoCompleteInteractionCreateEvent>("setup", "roles", "menu") {
            interaction.suggestString {
                choice("<NEW MENU>", "0")
                configuration.guild(interaction.guildId).roleMenus
                    .filter { it.value.title?.contains(interaction.focusedOption.value, true) != false }
                    .forEach { (id, roleMenu) -> choice(roleMenu.title!!, id.value.toString()) }
            }
        }

        onCommand<GuildChatInputCommandInteractionCreateEvent>("setup", "roles", "menu") {
            val response = interaction.deferEphemeralResponse()
            val command = interaction.command

            val menu: RoleMenu = configuration.guild(interaction.guildId).roleMenus.getOrDefault(
                Snowflake(command.strings["menu"]!!),
                RoleMenu(interaction.channel.id)
            )

            menu.apply {
                title = command.strings["title"]
                description = command.strings["description"]
            }.run {
                try {
                    publish(interaction.getGuild())
                    configuration.guild(interaction.guildId).roleMenus[messageId!!] = menu

                } catch (_: EntityNotFoundException) {
                    configuration.guild(interaction.guildId).roleMenus.remove(messageId!!)
                }
            }.also {
                configuration.save()
            }

            response.delete()
        }

        on<GuildCreateEvent> {
            val menus = configuration.guild(guild.id).roleMenus

            menus.toSortedMap().forEach { (messageId, menu) ->
                try {
                    menu.publish(guild)
                } catch (_: EntityNotFoundException) {
                    menus.remove(messageId).also {
                        configuration.save()
                    }
                }
            }
        }

        on<MessageBulkDeleteEvent> {
            if (null == guildId) {
                return@on
            }

            messageIds.forEach {
                configuration.guild(guildId!!).roleMenus.remove(it)
            }.also {
                configuration.save()
            }
        }

        on<MessageDeleteEvent> {
            if (null == guildId) {
                return@on
            }

            configuration.guild(guildId!!).roleMenus.remove(messageId).also {
                configuration.save()
            }
        }
    }

    public fun option(): Listener = listener {
        onAutoComplete<GuildAutoCompleteInteractionCreateEvent>("setup", "roles", "reaction") {
            interaction.suggestString {
                configuration.guild(interaction.guildId).roleMenus
                    .filter { it.value.title?.contains(interaction.focusedOption.value, true) != false }
                    .forEach { (id, roleMenu) -> choice(roleMenu.title!!, id.value.toString()) }
            }
        }

        onCommand<GuildChatInputCommandInteractionCreateEvent>("setup", "roles", "reaction") {
            val response = interaction.deferEphemeralResponse()
            val command = interaction.command
            val emoji = Emoji.from(command.strings["emoji"]!!)

            if (!kord.isAvailableEmoji(emoji.toReactionEmoji())) {
                response.respond {
                    content = "This emoji is not available (bot doesn't have access to this emoji)"
                }

                return@onCommand
            }

            val menu = configuration.guild(interaction.guildId).roleMenus[Snowflake(command.strings["menu"]!!)]!!
            val option = menu.options.getOrPut(emoji.markdown) { RoleReaction(emoji) }

            option.apply {
                role = configuration.guild(interaction.guildId).getRole(command.roles["role"]!!.id)
                description = command.strings["description"]
            }.run {
                menu.publish(interaction.getGuild())
            }.also {
                configuration.save()
                response.delete()
            }
        }

        // remove the option permanently when the last one is removed
        on<ReactionRemoveEvent> {
            if (null == guildId) { return@on }
            if (null == userAsMember) { return@on }
            if (false == userAsMember?.asMember()?.getPermissions()?.contains(Permission.ManageMessages)) { return@on }
            val menu = configuration.guild(guildId!!).roleMenus[messageId] ?: return@on
            if (message.fetchMessage().reactions.any { it.emoji == emoji }) {  return@on }

            menu.apply {
                options.remove(Emoji.from(emoji.mention).markdown)
            }.run {
                menu.publish(guild!!.asGuild())
            }.also {
                configuration.save()
            }
        }
    }

    public fun reactions(): Listener = listener {
        on<ReactionAddEvent> {
            if (userId == kord.selfId) { return@on }
            if (null == guildId) { return@on }
            if (null == userAsMember) { return@on }

            configuration.guild(guildId!!).roleMenus[messageId]?.getSelectedOption(emoji)?.apply {
                userAsMember!!.addRole(role!!.id, "User reacted to ${emoji.markdown}")
            }
        }

        on<ReactionRemoveEvent> {
            if (userId == kord.selfId) { return@on }
            if (null == guildId) { return@on }
            if (null == userAsMember) { return@on }

            configuration.guild(guildId!!).roleMenus[messageId]?.getSelectedOption(emoji)?.apply {
                userAsMember!!.removeRole(role!!.id, "User removed reaction ${emoji.markdown}")
            }
        }
    }
}
