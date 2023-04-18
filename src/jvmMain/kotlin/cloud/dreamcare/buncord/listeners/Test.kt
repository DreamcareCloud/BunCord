package cloud.dreamcare.buncord.listeners

import cloud.dreamcare.buncord.dsl.event
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.rest.builder.interaction.*
import io.github.oshai.KLogging
import kotlinx.coroutines.Job

public class Test {
    private companion object : KLogging()
    public val commands: MutableList<ApplicationCommandCreateBuilder> = mutableListOf()

    public fun testA(): Job = event<MessageCreateEvent> {
        if (false != message.author?.isBot) return@event

        logger.info("{}: {}", member?.displayName, message.content)
    }
}
