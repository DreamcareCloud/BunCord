package cloud.dreamcare.buncord

import cloud.dreamcare.buncord.manager.ReactiveEventManagerFactory
import io.github.cdimascio.dotenv.dotenv
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.springframework.boot.Banner
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BunCord(val reactiveEventManagerFactory: ReactiveEventManagerFactory) : CommandLineRunner {
    override fun run(vararg args: String?) {
        val dotenv = dotenv {
            ignoreIfMissing = true
        }

        val intents = listOf(
            GatewayIntent.GUILD_MEMBERS,
            GatewayIntent.GUILD_MESSAGE_REACTIONS,
            GatewayIntent.GUILD_MESSAGES,
            GatewayIntent.GUILD_MODERATION,
            GatewayIntent.GUILD_PRESENCES,
            GatewayIntent.GUILD_VOICE_STATES,
            GatewayIntent.MESSAGE_CONTENT
        )

        JDABuilder.create(dotenv.get("DISCORD_TOKEN"), intents)
            .disableCache(CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.SCHEDULED_EVENTS)
            .setStatus(OnlineStatus.DO_NOT_DISTURB)
            .setEventManager(reactiveEventManagerFactory.create(this.javaClass.packageName))
            .build()
    }
}

fun main(args: Array<String>) {
    runApplication<BunCord>(*args) {
        setBannerMode(Banner.Mode.OFF)
    }
}
