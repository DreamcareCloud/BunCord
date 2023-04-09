package cloud.dreamcare.buncord.commands

import cloud.dreamcare.buncord.dsl.Commands
import cloud.dreamcare.buncord.dsl.command
import dev.kord.gateway.Command

public class TestCommand {
    public fun command(): Commands = command("command") {
        on<Command> {
            println(12)
        }
    }
    public fun subCommand(): Commands = command("sub", "command") {
        println(22)
    }

    public fun groupCommand(): Commands = command("group", "sub", "command") {
        println(32)
    }
}
