package cloud.dreamcare.bunkord.extensions

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import dev.kord.core.Kord
import dev.kord.core.entity.Message

public suspend fun Message.toHistory(fetch: Boolean = false, conversation: MutableList<Message> = mutableListOf()): List<Message> {
    conversation.add(this)

    return if (fetch) { this.fetchMessageOrNull()?.referencedMessage } else { this.referencedMessage }?.toHistory(fetch, conversation) ?: conversation.reversed()
}

@OptIn(BetaOpenAI::class)
public fun List<Message>.toChatMessages(kord: Kord): List<ChatMessage> {
    return map {
        ChatMessage(
            if (it.author?.id == kord.selfId) { ChatRole.Assistant } else { ChatRole.User },
            it.content,
            it.author?.id.toString()
        )
    }
}
