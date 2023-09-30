package ventre.qqrobot.service

import com.alibaba.fastjson.JSONObject
import ventre.qqrobot.mainConfig
import ventre.qqrobot.network.HttpFunctions
import ventre.qqrobot.service.api.*
import ventre.qqrobot.utils.CacheMap

class MessageHelper() {
    private val chatContexts = mutableListOf<ChatMessages>()
    private val config = mainConfig["chatGPT"] as JSONObject
    private val maxLength = config["max_lenght"] as Int
    private val system = config["system"] as String
    private val others = config["others"] as String

    fun addContext(message:ChatMessages) {
        if (message.content.length > maxLength/2) return
        chatContexts.add(message)

        if (calculateSize() > maxLength) {
            print("reduce size from ${calculateSize()}\n")
            val iterator = chatContexts.iterator()
            var count :Int = chatContexts.size*2/3
            while (iterator.hasNext() && count > 0) {
                val temp = iterator.next()
                iterator.remove()
                count--
            }
            print("to ${calculateSize()}\n")
        }
    }

    private fun calculateSize() : Int {
        var ret = 0
        genPrompt(111, "").forEach {
            ret += it.content.length
        }
        return  ret
    }

    fun genPrompt(qqid:Long, prompt:String) : List<ChatMessages> {
        val ret  = mutableListOf<ChatMessages>()
        if (qqid == 3279994851)
            ret.add(ChatMessages("system", system))
        else
            ret.add(ChatMessages("system", others))
        ret.addAll(chatContexts)
        ret.add(ChatMessages("user", prompt))
        print("$ret\n")
        return ret
    }
}

object ChatGPTService2 {
    private val retrofit = HttpFunctions.createRetrofit(ChatGPTApi2.baseUrl).create(ChatGPTApi2::class.java)
    private lateinit var lastResponse : ChatGPTResponse2
    private val promptHelper = CacheMap<Long, MessageHelper>(1000*60*10)

    suspend fun chat(qqid:Long, prompt:String) : String? {
        var context = promptHelper[qqid]
        if (context == null) {
            context = MessageHelper()
            promptHelper[qqid] = context
        }

        try {
            lastResponse = retrofit.send(RequestBody2(context.genPrompt(qqid, prompt)))
        } catch (e:retrofit2.HttpException) {
            print(e.message)
            if (e.code() == 429) { //too many request
                Thread.sleep(8000)
                lastResponse = retrofit.send(RequestBody2(context.genPrompt(qqid, prompt)))
            }
        }
        if (lastResponse.choices != null && lastResponse.choices!!.isNotEmpty()) {
            print("${lastResponse.choices!![0].message}\n")
            val responseText = lastResponse.choices!![0].message
            context.addContext(ChatMessages("user", prompt))
            context.addContext(responseText)
            return responseText.content
        }
        return null;
    }

    fun dump() : String {
        return lastResponse.toString();
    }

}