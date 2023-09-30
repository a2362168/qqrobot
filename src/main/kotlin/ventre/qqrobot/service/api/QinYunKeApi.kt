package ventre.qqrobot.service.api

import retrofit2.http.GET
import retrofit2.http.Query

data class QinYunKeResponse(
    val result: Int = 0,
    val content: String?
)

interface QinYunKeApi {
    companion object {
        const val baseUrl = "http://api.qingyunke.com/"
    }

    @GET("api.php")
    suspend fun send(
        @Query("msg") msg: String,
        @Query("key") key: String = "free" //固定值
    ): QinYunKeResponse
}