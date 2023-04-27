package cloud.dreamcare.bunkord.commands.roles

import cloud.dreamcare.bunkord.dsl.Listener
import cloud.dreamcare.bunkord.dsl.listener
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.behavior.interaction.suggestString
import dev.kord.core.event.interaction.GlobalChatInputCommandInteractionCreateEvent
import dev.kord.core.event.interaction.GuildAutoCompleteInteractionCreateEvent

public class SetupRolesMenuCommand {
    public fun command(): Listener = listener {
        onAutoComplete<GuildAutoCompleteInteractionCreateEvent>("setup", "roles", "menu") {
            interaction.suggestString {
                choice("<NEW MENU>", "")
            }
            println(interaction.focusedOption.focused)
        }

        onCommand<GlobalChatInputCommandInteractionCreateEvent>("setup", "roles", "menu") {
            val response = interaction.deferEphemeralResponse()

            response.respond { content = "blub" }
        }
    }
}
