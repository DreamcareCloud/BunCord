package cloud.dreamcare.buncord

import io.github.cdimascio.dotenv.dotenv
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.springframework.boot.Banner
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BunCordApplication : CommandLineRunner {
    override fun run(vararg args: String?) {
        val dotenv = dotenv {
            ignoreIfMissing = true
        }

        val gatewayIntents = listOf(
            GatewayIntent.GUILD_MEMBERS,
            GatewayIntent.GUILD_MESSAGE_REACTIONS,
            GatewayIntent.GUILD_MESSAGES,
            GatewayIntent.GUILD_MODERATION,
            GatewayIntent.GUILD_PRESENCES,
            GatewayIntent.GUILD_VOICE_STATES,
            GatewayIntent.MESSAGE_CONTENT
        )

        JDABuilder.create(gatewayIntents)
            .setToken(dotenv.get("DISCORD_TOKEN"))
            .disableCache(CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.SCHEDULED_EVENTS)
            .build()
    }
}

fun main(args: Array<String>) {
    runApplication<BunCordApplication>(*args) {
        setBannerMode(Banner.Mode.OFF)
    }
}
