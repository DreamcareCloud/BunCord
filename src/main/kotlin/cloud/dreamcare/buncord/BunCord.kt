package cloud.dreamcare.buncord

import cloud.dreamcare.buncord.internal.service.InjectionService
import cloud.dreamcare.buncord.internal.utils.ReflectionUtils
import cloud.dreamcare.buncord.listeners.Test
import dev.kord.common.entity.PresenceStatus
import dev.kord.common.entity.SubCommand
import dev.kord.core.Kord
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.on
import dev.kord.gateway.Intent
import dev.kord.gateway.Intents
import dev.kord.gateway.PrivilegedIntent
import dev.kord.rest.builder.interaction.SubCommandBuilder
import dev.kord.rest.builder.interaction.group
import dev.kord.rest.builder.interaction.integer
import dev.kord.rest.builder.interaction.subCommand
import io.github.cdimascio.dotenv.dotenv
import io.github.oshai.KLogging
import kotlinx.coroutines.flow.forEach
import kotlin.concurrent.thread

public lateinit var kord: Kord
internal val diService: InjectionService = InjectionService()

internal class BunCord {
    private companion object: KLogging()

    internal suspend fun run(token: String) {
        kord = Kord(token)

//        Test().testA()
//        Test().testB()

        ReflectionUtils().registerFunctions("cloud.dreamcare.buncord.listeners", kord)
        ReflectionUtils().registerFunctions("cloud.dreamcare.buncord.commands", kord)

        kord.createGlobalChatInputCommand(
            "sum",
            "A slash command that sums two numbers",
        ) {
            group("moduke", "Sdf") {
                subCommand("abc", "sdfdsf") {
                    integer("first_number", "The first operand") {
                        required = true
                    }
                    integer("second_number", "The second operand") {
                        required = true
                    }
                }
                subCommand("efg", "sdfdsf") {
                    integer("first_number", "The first operand") {
                        required = true
                    }
                    integer("second_number", "The second operand") {
                        required = true
                    }
                }
            }
            group("sdfdsf", "Sdf") {
                subCommand("abc", "sdfdsf") {
                    integer("first_number", "The first operand") {
                        required = true
                    }
                    integer("second_number", "The second operand") {
                        required = true
                    }
                }
                subCommand("efg", "sdfdsf") {
                    integer("first_number", "The first operand") {
                        required = true
                    }
                    integer("second_number", "The second operand") {
                        required = true
                    }
                }
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

    BunCord().run(args.getOrElse(0) { environment["DISCORD_TOKEN"] })
}
