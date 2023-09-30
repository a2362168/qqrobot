package ventre.qqrobot.service.api

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

data class ChatGPTResponse2 (
    val id: String,
    val created: Long,
    val model: String,
    val choices: List<Choice2>?,
    val usage: Usage?
)

data class Choice2 (
    val message: ChatMessages,
    val finish_reason: String,
    val index: Int,
)

data class RequestBody2(
    val messages : List<ChatMessages>,
    val model : String = "gpt-3.5-turbo"
)

data class ChatMessages(
    val role:String,
    val content:String
)

interface ChatGPTApi2 {
    companion object {
        const val baseUrl = "https://api.openai.com/"
    }
    @Headers(
        *arrayOf("user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36",
            "Authorization: Bearer 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",
            "Content-Type: application/json")
    )
    @POST("v1/chat/completions")
    suspend fun send(
        @Body info : RequestBody2
    ): ChatGPTResponse2
}