package ventre.qqrobot.service

import com.alibaba.fastjson.JSONObject
import ventre.qqrobot.network.HttpFunctions
import ventre.qqrobot.service.api.LoliconApi
import ventre.qqrobot.service.api.RequestBody
import ventre.qqrobot.utils.parseJson

object LoliconService : ImageService {
    private val retrofit = HttpFunctions.createRetrofit(LoliconApi.baseUrl).create(LoliconApi::class.java)
    private val configMap = parseJson("EroImageConfig.json")!!
    private val loliconConfig = configMap["Lolicon"] as JSONObject
    private val recall = loliconConfig["recall"] as Int

    override suspend fun randomPic(tags: List<String>): List<String>? {
        val response = retrofit.search(RequestBody(tags))
        return if (response.data != null && response.data.isNotEmpty()) {
            print(response.data + "\n")
            listOf(response.data[0].urls.original)
        } else {
            print(response.error + "\n")
            null
        }
    }

    override fun recall(): Int = recall

    override fun getHeads(): List<Pair<String, String>> = PixivService.getHeads()

    override fun clearCache() {
        //nothing
    }
}