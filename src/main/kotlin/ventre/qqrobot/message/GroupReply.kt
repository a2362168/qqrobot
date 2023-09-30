package ventre.qqrobot.message

import com.alibaba.fastjson.JSONObject
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.*
import ventre.qqrobot.mainConfig
import ventre.qqrobot.utils.contentSplit
import ventre.qqrobot.utils.isAt
import ventre.qqrobot.utils.reply
import java.util.*

object GroupReply {
    private val factoryMapAt = ArrayList<Pair<(String)->Boolean, GroupMessageHandleFactoryAt>>()
    private val factoryMapNotAt = ArrayList<GroupMessageHandleNotAt>()
    private val features = mainConfig["features"] as JSONObject

    init {
        if(features["eroImage"] as Boolean) {
            val imageFactory = EroImageHandleFactory()
            factoryMapAt.add(Pair(imageFactory.match(),imageFactory))
        }
        if(features["chat"] as Boolean) {
            val charFactory = ChatHandlerFactory()
            factoryMapAt.add(Pair(charFactory.match(),charFactory))
        }
        if(features["repeat"] as Boolean) {
            factoryMapNotAt.add(RepeatHander)
        }
    }

    suspend fun reply(e:MessageEvent){
        val tags = e.contentSplit(Regex("\\s+"))

        if (e.isAt) {
            if (tags.size == 1 || (tags.size == 2 && tags[1] == "help")) {
                replyHelp(e)
                return
            }

            val integrable = factoryMapAt.iterator()
            while(integrable.hasNext()) {
                val integer = integrable.next()
                if (integer.first(tags[1])) {
                    if(tags.size > 2 && tags[2] == "help") {
                        e.reply(integer.second.help())
                        return
                    }
                    if(integer.second.makeHandle(e).handleMessage()) return
                }
            }
            replyHelp(e)
        } else {
            val integrable = factoryMapNotAt.iterator()
            while(integrable.hasNext()) {
                val integer = integrable.next()
                if (integer.handleMessage(e)) return
            }
        }
    }

    suspend fun replyHelp(e:MessageEvent): Unit {
        val stringBuilder = StringBuilder()

        stringBuilder.append("${e.bot.nick} 的食用方法：\n")
            .append("@${e.bot.nick} 关键字  目前已有的关键字：\n")
        var index = 1
        for (factory in factoryMapAt) {
            val tags = factory.second.tags()
            for(tag in tags) {
                stringBuilder.append("$index. ${tag}\n")
                index++
            }
        }
        stringBuilder.append("如果要查看具体功能，请输入 @${e.bot.nick} 关键字 help\n")
            .append("例子  @${e.bot.nick} 涩图 help")
        e.reply(stringBuilder.toString().trimIndent())
    }
}


