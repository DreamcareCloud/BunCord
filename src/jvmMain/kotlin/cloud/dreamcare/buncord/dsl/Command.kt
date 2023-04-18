package cloud.dreamcare.buncord.dsl

import cloud.dreamcare.buncord.kord
import dev.kord.core.entity.interaction.GroupCommand
import dev.kord.core.entity.interaction.RootCommand
import dev.kord.core.entity.interaction.SubCommand
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.core.on
import dev.kord.rest.builder.interaction.*
import kotlinx.coroutines.Job

internal val globalCommands: GlobalMultiApplicationCommandBuilder = GlobalMultiApplicationCommandBuilder()

public fun command(name: String, description: String, builder: GlobalChatInputCreateBuilder.() -> Unit) {
    globalCommands.also {
        it.input(name, description , builder)
    }
}

public inline fun <reified T : ChatInputCommandInteractionCreateEvent> event(command: String, noinline consumer: suspend T.() -> Unit): Job {
    return kord.on<T>(kord) {
        if (interaction.command !is RootCommand) {
            return@on
        }

        if (interaction.command.rootName != command) {
            return@on
        }

        consumer.invoke(this)
    }
}

public inline fun <reified T : ChatInputCommandInteractionCreateEvent> event(command: String, subCommand: String, noinline consumer: suspend T.() -> Unit): Job {
    return kord.on<T>(kord) {
        if (interaction.command !is SubCommand) {
            return@on
        }

        if (interaction.command.rootName != command) {
            return@on
        }

        if ((interaction.command as SubCommand).name != subCommand) {
            return@on
        }

        consumer.invoke(this)
    }
}

public inline fun <reified T : ChatInputCommandInteractionCreateEvent> event(command: String, group: String, subCommand: String, noinline consumer: suspend T.() -> Unit): Job {
    return kord.on<T>(kord) {
        if (interaction.command !is GroupCommand) {
            return@on
        }

        if (interaction.command.rootName != command) {
            return@on
        }

        if ((interaction.command as SubCommand).name != subCommand) {
            return@on
        }

        if ((interaction.command as GroupCommand).name != group) {
            return@on
        }

        consumer.invoke(this)
    }
}


