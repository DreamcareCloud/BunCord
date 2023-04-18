package cloud.dreamcare.buncord

import cloud.dreamcare.buncord.commands.SetupCommand
import cloud.dreamcare.buncord.dsl.globalCommands
import cloud.dreamcare.buncord.listeners.Test
import dev.kord.common.entity.PresenceStatus
import dev.kord.core.Kord
import dev.kord.core.entity.application.GlobalApplicationCommand
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.Intents
import dev.kord.gateway.PrivilegedIntent
import mu.KLogger
import mu.KotlinLogging

public lateinit var kord: Kord

internal class Bot {
    private val logger: KLogger = KotlinLogging.logger { }

    internal suspend fun run(token: String) {
        kord = Kord(token)

        Test().testA()
        SetupCommand().command()

        kord.getGlobalApplicationCommands().collect { command: GlobalApplicationCommand ->
            if (globalCommands.commands.any { it.name == command.name }) {
                return@collect
            }

            logger.debug { "Removed command %s.".format(command.name) }
            command.delete()
        }

        kord.createGlobalApplicationCommands {
            commands.addAll(globalCommands.commands)
        }

        kord.on<ReadyEvent> { logger.info { "${kord.selfId} running! (latency: ${kord.gateway.averagePing})" } }

        kord.login {
            name = "BunCord"
            presence { status = PresenceStatus.DoNotDisturb }
            @OptIn(PrivilegedIntent::class)
            intents = Intents(
                Intent.Guilds,
                Intent.GuildMembers,
                Intent.GuildMessages,
                Intent.GuildMessageReactions,
                Intent.MessageContent,
            )
        }
    }
}
