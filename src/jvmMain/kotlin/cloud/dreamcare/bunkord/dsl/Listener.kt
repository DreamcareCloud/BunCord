package cloud.dreamcare.bunkord.dsl

import cloud.dreamcare.bunkord.config.Configuration
import cloud.dreamcare.bunkord.internal.util.BuilderRegister
import dev.kord.core.Kord
import dev.kord.core.entity.interaction.GroupCommand
import dev.kord.core.entity.interaction.InteractionCommand
import dev.kord.core.entity.interaction.RootCommand
import dev.kord.core.entity.interaction.SubCommand
import dev.kord.core.event.Event
import dev.kord.core.event.interaction.*
import dev.kord.core.on
import kotlinx.coroutines.Job

public fun listener(construct: ListenerBuilder.() -> Unit): Listener = Listener(construct)

public class ListenerBuilder(public val kord: Kord, public val configuration: Configuration) {
    public inline fun <reified T: Event> on(noinline consumer: suspend T.() -> Unit): Job = kord.on<T>(kord, consumer)

    public inline fun <reified T: ChatInputCommandInteractionCreateEvent> onCommand(name: String, noinline listener: suspend T.() -> Unit): Job = kord.on<T> { if (interaction.command.isTriggered(name)) { listener(this) } }
    public inline fun <reified T: ChatInputCommandInteractionCreateEvent> onCommand(name: String, subCommand: String, noinline listener: suspend T.() -> Unit): Job = kord.on<T> { if (interaction.command.isTriggered(name, subCommand)) { listener(this) } }
    public inline fun <reified T: ChatInputCommandInteractionCreateEvent> onCommand(name: String, group: String, subCommand: String, noinline listener: suspend T.() -> Unit): Job = kord.on<T> { if (interaction.command.isTriggered(name, group, subCommand)) { listener(this) } }

    public inline fun <reified T: AutoCompleteInteractionCreateEvent> onAutoComplete(name: String, noinline listener: suspend T.() -> Unit): Job = kord.on<T> { if (interaction.command.isTriggered(name)) { listener(this) } }
    public inline fun <reified T: AutoCompleteInteractionCreateEvent> onAutoComplete(name: String, subCommand: String, noinline listener: suspend T.() -> Unit): Job = kord.on<T> { if (interaction.command.isTriggered(name, subCommand)) { listener(this) } }
    public inline fun <reified T: AutoCompleteInteractionCreateEvent> onAutoComplete(name: String, group: String, subCommand: String, noinline listener: suspend T.() -> Unit): Job = kord.on<T> { if (interaction.command.isTriggered(name, group, subCommand)) { listener(this) } }
}

public fun InteractionCommand.isTriggered(name: String): Boolean {
    if (this !is RootCommand) {
        return false
    }

    return this.rootName == name
}

public fun InteractionCommand.isTriggered(name: String, subCommand: String): Boolean {
    if (this !is SubCommand) {
        return false
    }

    return this.rootName == name && this.name == subCommand
}

public fun InteractionCommand.isTriggered(name: String, group: String, subCommand: String): Boolean {
    if (this !is GroupCommand) {
        return false
    }

    return this.rootName == name && this.groupName == group && this.name == subCommand
}

public class Listener(private val collector: ListenerBuilder.() -> Unit): BuilderRegister {
    override fun register(kord: Kord, configuration: Configuration) {
        collector.invoke(ListenerBuilder(kord, configuration))
    }
}
