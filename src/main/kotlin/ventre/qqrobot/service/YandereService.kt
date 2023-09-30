package ventre.qqrobot.service

import com.alibaba.fastjson.JSONObject
import ventre.qqrobot.network.HttpFunctions
import ventre.qqrobot.service.api.YanderApi
import ventre.qqrobot.service.api.YanderImage
import ventre.qqrobot.utils.CacheMap
import ventre.qqrobot.utils.randomInt
import ventre.qqrobot.utils.parseJson
import kotlin.random.Random

object YandereService : ImageService {
    private val retrofit = HttpFunctions.createRetrofit(YanderApi.baseUrl).create(YanderApi::class.java)
    private val configMap = parseJson("EroImageConfig.json")!!
    private val yanderConfig = configMap["Yander"] as JSONObject
    private val page = yanderConfig["page"] as Int
    private val orig = yanderConfig["original"] as Boolean
    private val tags = yanderConfig["tags"] as List<String>
    private val recall = yanderConfig["recall"] as Int
    private var currentImage : YanderImage? = null
    private val cacheMap = CacheMap<String, Int>(1000*60*60*24*3)

    private suspend fun hasPic(page: Int, tag: String):Boolean {
        val respArray = retrofit.search(tag, page)
        return respArray.isNotEmpty()
    }

    private suspend fun getPages(tag: String): Int {
        if(cacheMap[tag] != null) {
            print("getPages hit: $tag\n")
            return cacheMap[tag]!!
        }
        if(!hasPic(1, tag)) return 0
        var page = page
        var min = 1
        var max = page
        while (max-min > 1) {
            if (hasPic(page, tag)) {
                min = page
                page = (min + max)/2
            } else {
                max = page
                page = (min + max)/2
            }
        }
        cacheMap.put(tag, page)
        return page
    }

    private fun randomTag() : String {
        return tags[Random.randomInt(tags.size)]
    }

    private fun filterImages(images : ArrayList<YanderImage>) {
        val integrable = images.iterator()
        while (integrable.hasNext()) {
            val image = integrable.next()
            if(image.rating == "e") integrable.remove()
        }
    }

    override fun recall(): Int {
        return if(currentImage == null || currentImage!!.rating == "q") recall
        else 0
    }

    override fun getHeads() : List<Pair<String, String>> {
        return listOf(Pair("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36"))
    }

    override fun clearCache() {
        cacheMap.clear()
    }

    override suspend fun randomPic(tags:List<String>): List<String>? {
        val tag = randomTag()
        var page = getPages(tag)
        if (page == 0) {
            return null
        }
        page = Random.randomInt(page) + 1
        val images = ArrayList(retrofit.search(tag, page))
        filterImages(images)
        if (images.isEmpty()) return null
        val image = images[Random.randomInt(images.size)]
        print("tag:$tag, page:$page, size${images.size}: image:$image\n")
        currentImage = image
        return if (orig) listOf(image.jpeg_url) else listOf(image.sample_url)
    }
}