package cloud.dreamcare.bunkord

import cloud.dreamcare.bunkord.annotations.Service
import cloud.dreamcare.bunkord.config.Configuration
import cloud.dreamcare.bunkord.dsl.globalCommands
import cloud.dreamcare.bunkord.internal.services.InjectionService
import cloud.dreamcare.bunkord.internal.util.ReflectionUtils
import cloud.dreamcare.bunkord.internal.util.simplerName
import dev.kord.common.entity.PresenceStatus
import dev.kord.core.Kord
import dev.kord.core.behavior.requestMembers
import dev.kord.core.entity.application.GlobalApplicationCommand
import dev.kord.core.event.Event
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.event.guild.GuildCreateEvent
import dev.kord.core.on
import dev.kord.gateway.*
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.*
import mu.KLogger
import mu.KotlinLogging
import kotlin.concurrent.timer
import kotlin.io.path.Path
import kotlin.time.DurationUnit

internal val injectionService: InjectionService = InjectionService()
public lateinit var configuration: Configuration

public class BunKord {
    private val logger: KLogger = KotlinLogging.logger { }

    @OptIn(PrivilegedIntent::class)
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
            logger.info {
                "${self.tag} <@${self.id}> logged in (${guildIds.size} guilds)! (${
                    gateway.ping.value!!.toString(
                        DurationUnit.MILLISECONDS
                    )
                })"
            }
            kord.editPresence {
                status = PresenceStatus.Online
                watching("${guildIds.size} guild(s)")
            }

            var counter = 0
            timer("test", true, 0L, 60000L) {
                kord.launch {
                    kord.editPresence {
                        watching("test ${++counter}")
                    }
                }
            }
        }

        kord.on<GuildCreateEvent> {
            configuration.guild(guild.id).apply {
                name = guild.name

                guild.roles.collect { role ->
                    getRole(role.id).apply {
                        name = role.name
                        position = role.getPosition()
                    }
                }

                guild.requestMembers().collect {
                    it.members.forEach { member ->
                        member(member.id).apply {
                            displayName = member.displayName
                            active = true
                            joinedAt = member.joinedAt

                            member.roles.collect { role ->
                                getRole(role.id).apply {
                                    name = role.name
                                    position = role.getPosition()
                                }
                            }

                            getUser().apply {
                                username = member.username
                                discriminator = member.discriminator
                            }
                        }
                    }
                }
            }.also {
                configuration.save()
            }
        }

        kord.on<Event> {
            logger.debug { "Event: ${this::class.simplerName}" }
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

    configuration = Configuration().load(Path(environment["CONFIG_PATH"]))

    BunKord().run(
        token = args.getOrElse(0) { environment["DISCORD_TOKEN"] },
    )
}
