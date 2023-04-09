package cloud.dreamcare.buncord.commands

import cloud.dreamcare.buncord.dsl.command
import cloud.dreamcare.buncord.dsl.event
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.application.GlobalChatInputCommand
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import kotlinx.coroutines.Job

public class TestCommand {
    public suspend fun registerA(): GlobalChatInputCommand = command("one") {}

    public suspend fun registerB(): GlobalChatInputCommand = command("two") {}

    public suspend fun testA(): Job = event<ChatInputCommandInteractionCreateEvent>("one") {
        val response = interaction.deferPublicResponse()

        response.respond { content = "Hi number one!" }
    }

    public suspend fun testB(): Job = event<ChatInputCommandInteractionCreateEvent>("two") {
        val response = interaction.deferPublicResponse()

        response.respond { content = "Hi number two!" }
    }

//    public suspend fun sdfdsf(interaction: ChatInputCommandInteraction) {
//        val response = interaction.deferPublicResponse()
//
//
//        response.respond { content = "abc" }
//    }

//    public fun subCommand(): SubCommandBuilder = command("sub", "command") {
//
//    }
//
//    public fun groupCommand(): GroupCommandBuilder = command("group", "sub", "command") {
//        println(32)
//    }
}
