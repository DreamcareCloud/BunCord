package cloud.dreamcare.buncord

import cloud.dreamcare.buncord.annotations.Service
import cloud.dreamcare.buncord.dsl.globalCommands
import cloud.dreamcare.buncord.internal.services.InjectionService
import cloud.dreamcare.buncord.internal.util.ReflectionUtils
import dev.kord.common.entity.PresenceStatus
import dev.kord.core.Kord
import dev.kord.core.entity.application.GlobalApplicationCommand
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.Intents
import dev.kord.gateway.PrivilegedIntent
import io.github.cdimascio.dotenv.dotenv
import mu.KLogger
import mu.KotlinLogging
import kotlin.time.DurationUnit

internal val injectionService: InjectionService = InjectionService()

public class BunCord {
    private val logger: KLogger = KotlinLogging.logger { }

    public suspend fun run(token: String) {
        val kord = Kord(token)

        injectionService.inject(this)

        ReflectionUtils(this::class.java.packageName).apply {
            detectClassesWith<Service>().apply { injectionService.buildAllRecursively(this) }
            registerFunctions(kord)
        }

        kord.getGlobalApplicationCommands().collect { command: GlobalApplicationCommand ->
            if (globalCommands.commands.any { it.name == command.name }) {
                return@collect
            }

            logger.debug { "Removed command ${command.name}." }
            command.delete()
        }

        kord.createGlobalApplicationCommands {
            commands.addAll(globalCommands.commands)
        }

        kord.on<ReadyEvent> {
            logger.info { "${self.tag} <@${self.id}> logged in! (latency ${gateway.ping.value!!.toString(DurationUnit.SECONDS, 2)})" }
            kord.editPresence {
                status = PresenceStatus.Online
                watching("${guildIds.size} guild(s)")
            }
        }

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

public suspend fun main(args: Array<String>) {
    val environment = dotenv { ignoreIfMissing = true }

    BunCord().run(
        token = args.getOrElse(0) { environment["DISCORD_TOKEN"] }
    )
}
