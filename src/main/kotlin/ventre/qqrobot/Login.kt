package ventre.qqrobot

import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.utils.BotConfiguration
import ventre.qqrobot.message.GroupReply
import ventre.qqrobot.message.friendReply
import ventre.qqrobot.utils.parseJson

const val jsonPath = "MainConfig.json"
val mainConfig = parseJson(jsonPath)!!

suspend fun main(args: Array<String>) {
    val bot = BotFactory.newBot((mainConfig.get("qqId") as Number).toLong(),
        mainConfig.get("password") as String,
        BotConfiguration().apply {
            fileBasedDeviceInfo()
            inheritCoroutineContext()
        });

    bot.eventChannel.subscribeAlways<GroupMessageEvent> {
        event -> GroupReply.reply(event)
    }

    bot.eventChannel.subscribeAlways<FriendMessageEvent> {
        event -> friendReply(event)
    }
    bot.login()
    bot.join()
}