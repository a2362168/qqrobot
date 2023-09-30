package ventre.qqrobot.message

import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.At
import ventre.qqrobot.service.ChatGPTService
import ventre.qqrobot.service.ChatGPTService2
import ventre.qqrobot.service.QunYunKeService
import ventre.qqrobot.utils.contentSplit
import ventre.qqrobot.utils.reply

class ChatHandlerFactory : GroupMessageHandleFactoryAt {
    override fun match() : (String) -> Boolean = { true }
    override fun tags() : List<String> = listOf("聊天")
    override fun help(): String = "没有匹配任何关键字就会进行聊天"
    override fun makeHandle(messageEvent: MessageEvent) : GroupMessageHandleAt =
        ChatHandler(messageEvent)
}

class ChatHandler(messageEvent: MessageEvent) : GroupMessageHandleAt(messageEvent) {
    //val service = QunYunKeService
    //val service = ChatGPTService2
    val service = ChatGPTService

    override suspend fun handleMessage(): Boolean {
        print("ChatHandler.handleMessage\n")
        val tags : List<String> = messageEvent.contentSplit(Regex("\\s+"),2)
        messageEvent.reply(At(messageEvent.sender) + "chatGPT暂时被封禁，kuro酱目前失去了说话能力")
        return true

        val response = service.chat(messageEvent.sender.id, tags[1])
        return if(!response.isNullOrEmpty()) {
            messageEvent.reply(At(messageEvent.sender) + response)
            true
        } else {
            messageEvent.reply(At(messageEvent.sender) + service.dump())
            true
        }
    }

}
