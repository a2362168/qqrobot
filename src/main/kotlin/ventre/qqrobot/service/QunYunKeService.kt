package ventre.qqrobot.service

import ventre.qqrobot.network.HttpFunctions
import ventre.qqrobot.service.api.QinYunKeApi

object QunYunKeService {
    private val retrofit = HttpFunctions.createRetrofit(QinYunKeApi.baseUrl).create(QinYunKeApi::class.java)

    suspend fun char(say:String) : String? {
        val response = retrofit.send(say)
        return response.content
    }
}