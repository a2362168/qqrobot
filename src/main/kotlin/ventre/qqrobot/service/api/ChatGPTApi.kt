package ventre.qqrobot.service.api

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

data class ChatGPTResponse (
    val id: String,
    val created: String,
    val model: String,
    val choices: List<Choice>?,
    val usage: Usage?
)

data class Choice (
    val text: String,
    val index: String,
    val finish_reason: String,
)

data class Usage (
    val prompt_tokens : Int,
    val completion_tokens: Int,
    val total_tokens: Int,
)

data class RequestBody1(
    val prompt : String,
    val model : String = "text-davinci-003",
    val temperature : Double = 0.9,
    val max_tokens : Int = 1000,
    val top_p : Int = 1,
    val frequency_penalty : Double = 0.0,
    val presence_penalty : Double = 0.6,
    val stop : List<String> = listOf(" Human:", " AI:")
)

interface ChatGPTApi {
    companion object {
        const val baseUrl = "https://api.openai.com/"
    }
    @Headers(
        *arrayOf("user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36",
            "Authorization: Bearer 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",
            "Content-Type: application/json")
    )
    @POST("v1/completions")
    suspend fun send(
        @Body info : RequestBody1
    ): ChatGPTResponse
}