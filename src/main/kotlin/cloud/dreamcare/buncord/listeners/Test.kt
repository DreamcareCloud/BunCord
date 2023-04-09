package cloud.dreamcare.buncord.listeners

import cloud.dreamcare.buncord.dsl.event
import dev.kord.common.Color
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.rest.builder.message.modify.embed
import io.github.oshai.KLogging
import kotlinx.coroutines.Job

public class Test {
    private companion object : KLogging()

    public fun testA(): Job = event<MessageCreateEvent> {
        if (false != message.author?.isBot) return@event

        logger.info("{}: {}", member?.displayName, message.content)
    }

    public fun testB(): Job = event<ChatInputCommandInteractionCreateEvent> {
        val response = interaction.deferPublicResponse()

        response.respond {
            embed {
                title = "blub"
                description = "bla die bla"
                color = Color(0x9bf6ff)
                field {
                    inline = false
                    name = "title A"
                    value = "Value A"
                }
                field {
                    inline = false
                    name = "title B"
                    value = "Value B"
                }
            }
        }
    }
}
