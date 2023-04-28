package cloud.dreamcare.bunkord

import cloud.dreamcare.bunkord.annotations.Service
import cloud.dreamcare.bunkord.config.Configuration
import cloud.dreamcare.bunkord.dsl.globalCommands
import cloud.dreamcare.bunkord.internal.services.InjectionService
import cloud.dreamcare.bunkord.internal.util.ReflectionUtils
import dev.kord.common.entity.PresenceStatus
import dev.kord.core.Kord
import dev.kord.core.entity.application.GlobalApplicationCommand
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.Intents
import dev.kord.gateway.PrivilegedIntent
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.launch
import mu.KLogger
import mu.KotlinLogging
import kotlin.concurrent.timer
import kotlin.io.path.Path
import kotlin.io.path.createDirectories

internal val injectionService: InjectionService = InjectionService()
private val logger: KLogger = KotlinLogging.logger { }

public class BunKord {
    public suspend fun run(token: String, configuration: Configuration) {
        val kord = Kord(token)

        logger.info { "Starting BunKord" }

        injectionService.inject(this)
        injectionService.inject(configuration)

        ReflectionUtils(this::class.java.packageName).apply {
            detectClassesWith<Service>().apply { injectionService.buildAllRecursively(this) }
            registerFunctions(kord, configuration)
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

        var counter = 0
        kord.on<ReadyEvent> {
            logger.info {
                "${self.tag} <@${self.id}> logged in! (${gateway.ping.value})"
            }
            kord.editPresence {
                status = PresenceStatus.Online
                watching("${guildIds.size} guild(s)")
            }

            timer("test", true, 0L, 60000L) {
                kord.launch {
                    kord.editPresence {
                        watching("test ${++counter}")
                    }
                }
            }
        }

        kord.login {
            name = "BunKord"
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

    BunKord().run(
        token = args.getOrElse(0) { environment["DISCORD_TOKEN"] },
        configuration = Path(environment["CONFIG_PATH"]).createDirectories().toRealPath().run { Configuration().load(this) }
    )
}
