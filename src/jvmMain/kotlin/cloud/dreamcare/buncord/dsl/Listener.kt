package cloud.dreamcare.buncord.dsl

import cloud.dreamcare.buncord.internal.util.BuilderRegister
import dev.kord.core.Kord
import dev.kord.core.entity.interaction.GroupCommand
import dev.kord.core.entity.interaction.RootCommand
import dev.kord.core.entity.interaction.SubCommand
import dev.kord.core.event.Event
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.core.on
import kotlinx.coroutines.Job

public fun listener(construct: ListenerBuilder.() -> Unit): Listener = Listener(construct)

public class ListenerBuilder(public val kord: Kord) {
    public inline fun <reified T: Event> on(noinline consumer: suspend T.() -> Unit): Job = kord.on<T>(kord, consumer)

    public fun onCommand(name: String, listener: suspend ChatInputCommandInteractionCreateEvent.() -> Unit) {
        kord.on<ChatInputCommandInteractionCreateEvent> {
            if (interaction.command !is RootCommand) {
                return@on
            }

            if (interaction.command.rootName != name) {
                return@on
            }

            listener(this)
        }
    }

    public fun onCommand(name: String, subCommand: String, listener: suspend ChatInputCommandInteractionCreateEvent.() -> Unit) {
        kord.on<ChatInputCommandInteractionCreateEvent> {
            if (interaction.command !is SubCommand) {
                return@on
            }

            if (interaction.command.rootName != name) {
                return@on
            }

            if ((interaction.command as SubCommand).name != subCommand) {
                return@on
            }

            listener(this)
        }
    }

    public fun onCommand(name: String, group: String, subCommand: String, listener: suspend ChatInputCommandInteractionCreateEvent.() -> Unit) {
        kord.on<ChatInputCommandInteractionCreateEvent> {
            if (interaction.command !is GroupCommand) {
                return@on
            }

            if (interaction.command.rootName != name) {
                return@on
            }

            if ((interaction.command as SubCommand).name != subCommand) {
                return@on
            }

            if ((interaction.command as GroupCommand).name != group) {
                return@on
            }

            listener(this)
        }
    }
}

public class Listener(private val collector: ListenerBuilder.() -> Unit): BuilderRegister {
    override fun register(kord: Kord) {
        collector.invoke(ListenerBuilder(kord))
    }
}
