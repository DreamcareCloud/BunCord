package cloud.dreamcare.bunkord.commands

import cloud.dreamcare.bunkord.dsl.Command
import cloud.dreamcare.bunkord.dsl.createGlobalChatInputCommand
import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import dev.kord.rest.builder.interaction.group
import dev.kord.rest.builder.interaction.role
import dev.kord.rest.builder.interaction.string

public class SetupCommand {
    public fun register(): Command = createGlobalChatInputCommand("setup", "Bot Setup") {
        defaultMemberPermissions = Permissions { Permission.Administrator }
        dmPermission = false
        group("roles", "configure roles") {
            subCommand("menu", "Add/Edit menu") {
                string("menu", "Menu") { required = true; autocomplete = true }
                string("title", "Menu title") { required = true }
                string("description", "Menu description")
            }

            subCommand("reaction", "Add/Edit role reaction option") {
                string("menu", "Menu to add a reaction option to") { required = true; autocomplete = true }
                string("emoji", "The emoji to use") { required = true }
                role("role", "The role to assign") { required = true }
                string("description", "Description to display (default: roll name)")
            }
        }
    }
}
