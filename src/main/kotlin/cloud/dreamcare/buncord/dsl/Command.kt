package cloud.dreamcare.buncord.dsl

import cloud.dreamcare.buncord.internal.utils.BuilderRegister
import cloud.dreamcare.buncord.kord
import dev.kord.core.Kord
import dev.kord.core.entity.application.GlobalChatInputCommand
import dev.kord.rest.builder.interaction.GlobalChatInputCreateBuilder

public suspend fun command(command: String, construct: GlobalChatInputCreateBuilder.() -> Unit): GlobalChatInputCommand = kord.createGlobalChatInputCommand(command, "description", construct)
//public fun command(command: String, subCommand: String, construct: SubCommandBuilder.() -> Unit): SubCommandBuilder = Commands(command, null, subCommand, construct)
//public fun command(command: String, group: String, subCommand: String, construct: GroupCommandBuilder.() -> Unit): GroupCommandBuilder = Commands(command, group, subCommand, construct)

public data class ListenerBuilderA(val kord: Kord, val command: String, val group: String?, val subCommand: String?) {
//    public inline fun <reified T : Any> on(crossinline listener: suspend T.() -> Unit) {
//        println(command)
////        kord.createGlobalChatInputCommand(command, "abc") {
////            println("sad")
////        }
//    }
}

public class Commands(private val command: String, private val group: String?, private val subCommand: String?, private val collector: ListenerBuilderA.() -> Unit) : BuilderRegister {
    override fun register(kord: Kord) {
        collector.invoke(ListenerBuilderA(kord, command, group, subCommand))
    }
}
