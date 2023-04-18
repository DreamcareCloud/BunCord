package cloud.dreamcare.buncord.commands

import cloud.dreamcare.buncord.dsl.command
import dev.kord.rest.builder.interaction.integer
import dev.kord.rest.builder.interaction.role

public class SetupCommand {
    public fun command(): Any = command("setup", "Bot setup options") {
        integer("number", "pwetty number")
        role("role", "select your role")
    }
}
