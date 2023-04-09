package cloud.dreamcare.buncord.listeners

import cloud.dreamcare.buncord.dsl.event
import dev.kord.core.event.guild.GuildCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import io.github.oshai.KLogging

class Test {
    private companion object : KLogging()

    fun testA() = event<MessageCreateEvent> {
    }

    fun testB() = event<GuildCreateEvent> {
        logger.warn(guild.name)
    }
}
