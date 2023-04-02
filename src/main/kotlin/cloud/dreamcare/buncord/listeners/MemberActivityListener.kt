package cloud.dreamcare.buncord.listeners

import cloud.dreamcare.buncord.dsl.listeners
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.SubscribeEvent
import org.springframework.stereotype.Component

@Component
class MemberActivityListener {
    private val userMessages = HashMap<Member, Message>()

    @SubscribeEvent
    fun autoExpand() = listeners {
        on<MessageReceivedEvent>()
            .filter { !it.isFromGuild || !it.author.isBot || !it.author.isSystem || !it.isWebhookMessage }
            .filter { it.message.contentRaw.indexOf(" ") >= 3 }
            .filter { it.message.contentRaw != userMessages[it.member]?.contentRaw }
            .mapNotNull { it.member }
            .subscribe() {
                println("test")
            }
    }
}
