package ventre.qqrobot.service.api

import retrofit2.http.*


data class LoliconImage(
    val error : String,
    val data : List<LoliconData>
)

data class LoliconData(
    val pid: String = "",
    val author: String = "",
    val title: String = "",
    val urls: ImageUrl
)

data class ImageUrl(
    val original: String
)


data class RequestBody(
    val tag : List<String> = listOf(""),
    val r18 : Int = 0,
    val proxy : String = "false"
)

interface LoliconApi {
    companion object {
        const val baseUrl = "https://api.lolicon.app/"
    }

    /**
     * 查询图片
     */
    @Headers("user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36",
        "Content-Type: application/json"
    )
    @POST("setu/v2")
    suspend fun search(
        @Body info : RequestBody
    ): LoliconImage
}