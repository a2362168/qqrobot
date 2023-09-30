package ventre.qqrobot.service.api

import retrofit2.http.*

data class YanderImage(
    val id: String = "",
    val author: String = "",
    val rating: String = "",
    val sample_url: String = "",
    val jpeg_url: String = ""
)

interface YanderApi {
    companion object {
        const val baseUrl = "https://yande.re/"
    }

    /**
     * 查询图片
     */
    @Headers("user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36")
    @GET("post.json")
    suspend fun search(
        @Query("tags") tags: String,
        @Query("page") page: Int
    ): List<YanderImage>
}