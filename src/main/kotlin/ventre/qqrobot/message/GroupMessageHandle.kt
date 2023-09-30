package ventre.qqrobot.message

import net.mamoe.mirai.event.events.MessageEvent


abstract class GroupMessageHandleAt(val messageEvent: MessageEvent) {

    abstract suspend fun handleMessage() : Boolean
}

interface GroupMessageHandleFactoryAt {
    fun tags()  : List<String>
    fun help()  : String
    fun match() : (String) -> Boolean
    fun makeHandle(messageEvent: MessageEvent) : GroupMessageHandleAt
}

interface GroupMessageHandleNotAt {
    suspend fun handleMessage(messageEvent: MessageEvent) : Boolean
}
