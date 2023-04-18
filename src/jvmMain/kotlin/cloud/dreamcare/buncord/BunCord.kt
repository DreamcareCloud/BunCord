package cloud.dreamcare.buncord

import io.github.cdimascio.dotenv.dotenv

public suspend fun main(args: Array<String>) {
    val environment = dotenv { ignoreIfMissing = true }

    Bot().run(
        token = args.getOrElse(0) { environment["DISCORD_TOKEN"] }
    )
}
