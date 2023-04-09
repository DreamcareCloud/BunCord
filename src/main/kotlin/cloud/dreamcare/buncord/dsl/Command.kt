package cloud.dreamcare.buncord.dsl

import cloud.dreamcare.buncord.internal.utils.BuilderRegister
import dev.kord.core.Kord
import dev.kord.core.event.Event
import dev.kord.core.on

public fun command(command: String, construct: ListenerBuilderA.() -> Unit): Commands = Commands(command, null, null, construct)
public fun command(command: String, subCommand: String, construct: ListenerBuilderA.() -> Unit): Commands = Commands(command, null, subCommand, construct)
public fun command(command: String, group: String, subCommand: String, construct: ListenerBuilderA.() -> Unit): Commands = Commands(command, group, subCommand, construct)

public data class ListenerBuilderA(val kord: Kord, val command: String, val group: String?, val subCommand: String?) {
    public inline fun <reified T : Any> on(crossinline listener: suspend T.() -> Unit) {
        println(command)
//        kord.createGlobalChatInputCommand(command, "abc") {
//            println("sad")
//        }
    }
}

public class Commands(private val command: String, private val group: String?, private val subCommand: String?, private val collector: ListenerBuilderA.() -> Unit) : BuilderRegister {
    override fun register(kord: Kord) {
        collector.invoke(ListenerBuilderA(kord, command, group, subCommand))
    }
}
