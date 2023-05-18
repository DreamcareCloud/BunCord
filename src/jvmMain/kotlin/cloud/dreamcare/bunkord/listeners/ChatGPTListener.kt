package cloud.dreamcare.bunkord.listeners

import cloud.dreamcare.bunkord.config.openai.ChatGPT
import cloud.dreamcare.bunkord.dsl.Command
import cloud.dreamcare.bunkord.dsl.Listener
import cloud.dreamcare.bunkord.dsl.createGlobalChatInputCommand
import cloud.dreamcare.bunkord.dsl.listener
import cloud.dreamcare.bunkord.extensions.toChatMessages
import cloud.dreamcare.bunkord.extensions.toHistory
import cloud.dreamcare.bunkord.value.EmojiValue
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.image.ImageCreation
import com.aallam.openai.api.image.ImageSize
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.moderation.ModerationRequest
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import dev.kord.core.behavior.channel.withTyping
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.behavior.interaction.suggestString
import dev.kord.core.behavior.reply
import dev.kord.core.entity.Message
import dev.kord.core.event.interaction.GuildAutoCompleteInteractionCreateEvent
import dev.kord.core.event.interaction.GuildChatInputCommandInteractionCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.rest.builder.interaction.string
import dev.kord.rest.builder.message.modify.embed
import kotlin.time.Duration.Companion.minutes

public class ChatGPTListener {
    public fun command(): Command = createGlobalChatInputCommand("imagine", "Imagine") {
        string("prompt", "The prompt to imagine") { required = true }
    }

    public fun menu(): Listener = listener {
        onAutoComplete<GuildAutoCompleteInteractionCreateEvent>("setup", "openai", "chatgpt") {
            interaction.suggestString {
                choice("gpt-3.5-turbo", "gpt-3.5-turbo")
                choice("gpt-4", "gpt-4")
            }
        }

        onCommand<GuildChatInputCommandInteractionCreateEvent>("setup", "openai", "token") {
            val response = interaction.deferEphemeralResponse()
            val command = interaction.command

            configuration.guild(interaction.guildId).chatGPT = ChatGPT().apply {
                token = command.strings["token"]
            }.also {
                configuration.save()
            }

            response.delete()
        }

        onCommand<GuildChatInputCommandInteractionCreateEvent>("setup", "openai", "chatgpt") {
            val response = interaction.deferEphemeralResponse()
            val command = interaction.command

            val chatGPT: ChatGPT? = configuration.guild(interaction.guildId).chatGPT

            if (null == chatGPT) {
                response.respond { content = "Please configure a token first: `/setup openai token`" }

                return@onCommand
            }

            chatGPT.apply {
                model = command.strings["model"] ?: "gpt-3.5-turbo"
                personality = command.strings["personality"]
            }

            configuration.save()

            response.respond { content = "Configured with model `${chatGPT.model}`" }
        }
    }

    @OptIn(BetaOpenAI::class)
    public fun chat(): Listener = listener {
        onCommand<GuildChatInputCommandInteractionCreateEvent>("imagine") {
            val config = configuration.guild(interaction.guildId).chatGPT ?: return@onCommand
            val token = config.token ?: return@onCommand

            val response = interaction.deferPublicResponse()

            val openAI = OpenAI(
                OpenAIConfig(
                    token = token,
                    logLevel = LogLevel.None,
                    timeout = Timeout(2.minutes),
                )
            )

            val images = openAI.imageURL( // or openAI.imageJSON
                creation = ImageCreation(
                    prompt = interaction.command.strings["prompt"] ?: return@onCommand,
                    size = ImageSize.is1024x1024
                )
            )

            response.respond { images.forEach { embed { image = it.url } } }
        }

        on<MessageCreateEvent> {
            if (null == guildId) {
                return@on
            }
            if (false != member?.isBot) {
                return@on
            }
            val config = configuration.guild(guildId!!).chatGPT ?: return@on
            val token = config.token ?: return@on

            if (!message.mentionedUserIds.contains(kord.selfId)) {
                return@on
            }

            val openAI = OpenAI(
                OpenAIConfig(
                    token = token,
                    logLevel = LogLevel.None,
                    timeout = Timeout(2.minutes),
                )
            )

            message.channel.withTyping {
                if (openAI.moderations(ModerationRequest(listOf(message.content))).results.any { it.flagged }) {
                    message.reply {
                        content = "${EmojiValue.NO.emoji} *Your message contains inappropiate content, violating the https://openai.com Terms of Service*"
                        suppressEmbeds = true
                    }

                    return@withTyping
                }

                var reference: Message = message

                val messages = mutableListOf<ChatMessage>()

                if (null != config.personality) {
                    messages.add(ChatMessage(ChatRole.System, config.personality!!))
                }
                messages.addAll(message.toHistory(true).toChatMessages(kord))

                runCatching {
                    val chatCompletionRequest = ChatCompletionRequest(
                        model = ModelId(config.model),
                        user = member!!.tag,
                        messages = messages
                    )

                    openAI.chatCompletion(chatCompletionRequest).choices.map { it.message }.map { it?.content }.forEach {
                        it?.chunked(2000)?.forEach {
                            reference = reference.reply {
                                content = it
                            }
                        }
                    }
                }.onFailure {
                    message.reply { content = it.message }
                }
            }
        }
    }
}
