package ventre.qqrobot.service

import com.alibaba.fastjson.JSONObject
import ventre.qqrobot.mainConfig
import ventre.qqrobot.network.HttpFunctions
import ventre.qqrobot.service.api.ChatGPTApi
import ventre.qqrobot.service.api.ChatGPTResponse
import ventre.qqrobot.service.api.RequestBody1
import ventre.qqrobot.utils.CacheMap

data class ChatContext(val human:String, val ai:String)

class PromptHelper() {
    private val chatContexts = mutableListOf<ChatContext>()
    private val config = mainConfig["chatGPT"] as JSONObject
    private val maxLength = config["max_lenght"] as Int

    fun addContext(human:String, ai:String) {
        if (ai.length > maxLength/4) return
        chatContexts.add(ChatContext(human, ai))

        if (calculateSize() > maxLength) {
            print("reduce size from ${calculateSize()}\n")
            val iterator = chatContexts.iterator()
            var count :Int = chatContexts.size*2/3
            while (iterator.hasNext() && count > 0) {
                val temp = iterator.next()
                print("remove human:${temp.human} ai:${temp.ai}\n")
                iterator.remove()
                count--
            }
            print("to ${calculateSize()}\n")
        }
    }

    private fun calculateSize() : Int = genPrompt("").length

    fun genPrompt(prompt:String) : String {
        var ret = StringBuilder()
        chatContexts.forEach {
            ret.append("Human:${it.human}\nAI:${it.ai}\n")
        }
        ret.append("Human:$prompt\nAI:")

        //print("genPrompt ${ret.toString()}\n")
        return ret.toString()
    }
}

object ChatGPTService {
    private val retrofit = HttpFunctions.createRetrofit(ChatGPTApi.baseUrl).create(ChatGPTApi::class.java)
    private lateinit var lastResponse : ChatGPTResponse;
    private val promptHelper = CacheMap<Long, PromptHelper>(1000*60*10)

    suspend fun chat(qqid:Long, prompt:String) : String? {
        var context = promptHelper[qqid]
        if (context == null) {
            context = PromptHelper()
            promptHelper[qqid] = context
        }

        try {
            lastResponse = retrofit.send(RequestBody1(context.genPrompt(prompt)))
        } catch (e:retrofit2.HttpException) {
            print(e.message)
            if (e.code() == 429) { //too many request
                Thread.sleep(8000)
                lastResponse = retrofit.send(RequestBody1(context.genPrompt(prompt)))
            }
        }
        if (lastResponse.choices != null && lastResponse.choices!!.isNotEmpty()) {
            //print("${lastResponse.choices!![0].text}\n")
            val responseText = lastResponse.choices!![0].text
            context.addContext(prompt, responseText)
            return responseText
        }
        return null;
    }

    fun dump() : String {
        return lastResponse.toString();
    }

}