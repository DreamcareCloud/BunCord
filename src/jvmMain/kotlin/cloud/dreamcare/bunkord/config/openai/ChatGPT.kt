package cloud.dreamcare.bunkord.config.openai

public data class ChatGPT(
    var token: String? = null,
    var model: String = "gpt-3.5-turbo",
    var personality: String? = null
)
