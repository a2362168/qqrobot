package ventre.qqrobot.message

import net.mamoe.mirai.event.events.MessageEvent

suspend fun friendReply(e: MessageEvent){
    GroupReply.reply(e)
}