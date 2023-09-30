package ventre.qqrobot.utils

import kotlinx.coroutines.Job
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.MessageReceipt
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.utils.ExternalResource.Companion.toExternalResource
import java.io.File
import java.util.concurrent.ThreadLocalRandom
import kotlin.random.Random

@JvmSynthetic
suspend fun MessageEvent.reply(message: Message): MessageReceipt<Contact> =
    subject.sendMessage(message)

@JvmSynthetic
suspend fun MessageEvent.reply(plain: String): MessageReceipt<Contact> =
    subject.sendMessage(plain)

@JvmSynthetic
suspend fun MessageEvent.sendImage(image: File): MessageReceipt<Contact> {
    val img = subject.uploadImage(image.inputStream().toExternalResource())
    return subject.sendMessage(img)
}

@JvmSynthetic
suspend fun MessageEvent.sendImages(images: List<File>): MessageReceipt<Contact> {
    val imgs = ArrayList<Image>()
    for(image in images) {
        val img = subject.uploadImage(image.inputStream().toExternalResource())
        imgs.add(img)
    }

    return subject.sendMessage(imgs.toMessageChain())
}

val MessageEvent.isAt : Boolean
    get() {
        if (this is FriendMessageEvent) return true
        val at : At? by message.orNull()
        return at?.target == bot.id
    }

fun MessageEvent.contentSplit(regex : Regex, limit: Int = 0) : List<String> {
    return if (this is FriendMessageEvent) "@robot ${message.content}".split(regex, limit)
    else message.content.split(regex, limit)
}

@JvmSynthetic
public fun Random.randomInt(min:Int, max:Int): Int {
    return ThreadLocalRandom.current().nextInt(max-min) + min
}

@JvmSynthetic
public fun Random.randomInt(max:Int): Int {
    return Random.randomInt(0, max)
}