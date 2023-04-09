package cloud.dreamcare.buncord.commands

import cloud.dreamcare.buncord.dsl.command
import cloud.dreamcare.buncord.dsl.event
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.application.GlobalChatInputCommand
import dev.kord.core.entity.interaction.IntegerOptionValue
import dev.kord.core.entity.interaction.RoleOptionValue
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.interaction.integer
import dev.kord.rest.builder.interaction.role
import kotlinx.coroutines.Job
import mu.KLogger
import mu.KotlinLogging

public class TestCommand {
    private val logger: KLogger = KotlinLogging.logger { }

    public suspend fun registerA(): GlobalChatInputCommand = command("one") {
        integer("number", "pwetty number")
        role("role", "select your role")
    }
    public suspend fun registerB(): GlobalChatInputCommand = command("two") {}

    public suspend fun testA(): Job = event<ChatInputCommandInteractionCreateEvent>("one") {
        val response = interaction.deferPublicResponse()
        logger.info { (interaction.command.options["number"] as IntegerOptionValue).value }
        logger.info { (interaction.command.options["role"] as RoleOptionValue).resolvedObject?.name }

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
