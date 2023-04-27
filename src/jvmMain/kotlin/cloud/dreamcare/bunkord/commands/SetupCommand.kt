package cloud.dreamcare.bunkord.commands

import cloud.dreamcare.bunkord.dsl.Command
import cloud.dreamcare.bunkord.dsl.createGlobalChatInputCommand
import dev.kord.rest.builder.interaction.group
import dev.kord.rest.builder.interaction.string

public class SetupCommand {
    public fun register(): Command = createGlobalChatInputCommand("setup", "Bot Setup") {
        group("roles", "Configure roles") {
            subCommand("menu", "Add/Edit menu") {
                string("menu", "Menu") {
                    required = true
                    autocomplete = true
                }
                string("title", "Menu title") {
                    required = true
                }
                string("description", "Menu description")
            }
        }
    }
}
