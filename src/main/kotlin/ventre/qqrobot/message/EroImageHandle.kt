package ventre.qqrobot.message

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.MessageReceipt
import net.mamoe.mirai.message.data.content
import ventre.qqrobot.service.YandereService
import ventre.qqrobot.network.HttpFunctions
import ventre.qqrobot.service.ImageService
import ventre.qqrobot.service.LoliconService
import ventre.qqrobot.service.PixivService
import ventre.qqrobot.utils.*
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.text.split

private const val jsonPath = "EroImageConfig.json"
private val configMap = parseJson(jsonPath)!!

class EroImageHandleFactory : GroupMessageHandleFactoryAt {
    private val functionTags = configMap["TAG"] as List<String>
    private val helpstr = configMap["help"] as String

    override fun help(): String = helpstr

    override fun tags(): List<String> = functionTags
    override fun match() : (String) -> Boolean = {
        functionTags.contains(it)
    }
    override fun makeHandle(messageEvent: MessageEvent) : GroupMessageHandleAt =
        EroImageHandle(messageEvent)
}

class EroImageHandle(messageEvent: MessageEvent) : GroupMessageHandleAt(messageEvent) {
    private val functionTags = configMap["TAG"] as List<String>

    companion object INSTANCE {
        private val cacheMap = CacheMap<User, MessageReceipt<Contact>>(1000*120)
    }
    private lateinit var imageService : ImageService


    override suspend fun handleMessage(): Boolean {
        print("EroImageHandle.handleMessage\n")
        val tags : List<String> = messageEvent.contentSplit(Regex("\\s+"))

        imageService = when(tags[1]) {
            functionTags[0] -> if(tags.size == 2) YandereService else PixivService
            functionTags[1] -> LoliconService
            else -> PixivService
        }

        if(tags.size > 2) {
            when (tags[2]) {
                "撤回" -> {
                    cacheMap[messageEvent.sender]?.recall()
                    return true
                }
                "clear" -> {
                    imageService.clearCache()
                    return true
                }
            }
        }


        val url = imageService.randomPic(tags.subList(2, tags.size))
        if(url.isNullOrEmpty()) {
            val tag = tags.subList(2, tags.size).joinToString(" ", "\"", "\"")
            messageEvent.reply("找不到 $tag 的图片")
            return true
        }
        sendPic(url)
        return true
    }

    private suspend fun sendPic(urls : List<String>): Boolean {
        try {
            val files = ArrayList<File>()
            for(index in urls.indices) {
                val fileName = "temp$index.jpg"
                if(HttpFunctions.httpGetImg(urls[index], imageService.getHeads(), fileName)) files.add(File(fileName))
                else {
                    var url = urls[index]
                    url = url.substring(0, url.lastIndexOf(".")) + ".png"
                    if(HttpFunctions.httpGetImg(url, imageService.getHeads(), fileName)) files.add(File(fileName))
                }
            }
            if (files.isNotEmpty()){
                val r = messageEvent.sendImages(files)
                cacheMap[messageEvent.sender] = r
                if(imageService.recall() > 0) {
                    GlobalScope.launch {
                        delay(imageService.recall().toLong())
                        r.recall()
                        cacheMap.remove(messageEvent.sender)
                    }
                }
            } else {
                messageEvent.reply("下载图片失败${urls[0]}")
            }

            return true
        } catch (e: IOException) {
            e.printStackTrace()
            //recallPics(receipts, rating)
            return false
        }
    }
}