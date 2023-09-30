package ventre.qqrobot.service

import com.alibaba.fastjson.JSONObject
import ventre.qqrobot.network.HttpFunctions
import ventre.qqrobot.service.api.PixivApi
import ventre.qqrobot.service.api.PixivImage
import ventre.qqrobot.service.api.PixivImageSearchResult
import ventre.qqrobot.utils.CacheMap
import ventre.qqrobot.utils.randomInt
import ventre.qqrobot.utils.parseJson
import kotlin.random.Random


object PixivService : ImageService {
    private val retrofit = HttpFunctions.createRetrofit(PixivApi.baseUrl).create(PixivApi::class.java)
    private val configMap = parseJson("EroImageConfig.json")!!
    private val pixivConfig = configMap["Pixiv"] as JSONObject
    private val page = pixivConfig["page"] as Int
    private val count = pixivConfig["count"] as Int
    private val orig = pixivConfig["original"] as Boolean
    private val recall = pixivConfig["recall"] as Int
    private val pageCache = CacheMap<String, Int>(1000*60*60*24*3)
    private val pidCache = CacheMap<String, Int>(1000*60*60*24*7)

    enum class ImageSize(val urlPrefix : String){
        ORIGINAL("https://i.pximg.net/img-original"),
        REGULAR("https://i.pximg.net/img-master"),
        THUMB("https://i.pximg.net/c/540x540_70/img-master")
    }

    private suspend fun hasPic(page: Int,tag: String):Boolean {
        val response = retrofit.search(tag, page)
        val images = (response.body ?: PixivImageSearchResult()).illust.data
        return images.isNotEmpty()
    }

    private suspend fun getPages(tag: String): Int {
        if(pageCache[tag] != null) {
            print("getPages hit: $tag\n")
            return pageCache.get(tag)!!
        }
        if(!hasPic(1,tag)) return 0
        var page = page
        var min = 1
        var max = page
        while (max-min > 1) {
            if (hasPic(page,tag)) {
                min = page
                page = (min + max)/2
            } else {
                max = page
                page = (min + max)/2
            }
        }
        pageCache.put(tag, page)
        return page
    }

    private fun parseImageUrl(image: PixivImage): List<String> {
        val imageList = ArrayList<String>()
        val size = if (orig && image.pageCount == 1) {
            ImageSize.ORIGINAL
        } else if(image.pageCount > count) {
            ImageSize.THUMB
        } else {
            ImageSize.REGULAR
        }

        val url = image.url
        val startIndex = url.indexOf("/img/")
        val str = "${image.id}_p0"
        //缩略图(不止这一种格式): https://i.pximg.net/c/250x250_80_a2/img-master/img/2020/09/21/10/19/03/84511119_p0_square1200.jpg
        //原图 https://i.pximg.net/img-original/img/2020/09/21/10/19/03/84511119_p0.jpg
        val lastIndex = url.lastIndexOf(str)
        val uri = url.substring(startIndex, lastIndex)
        val suffix = url.substring(url.lastIndexOf("."))
        for (i in 0 until image.pageCount) {
            val uri = "$uri${image.id}_p${i}"
            when(size) {
                ImageSize.ORIGINAL -> imageList.add("${size.urlPrefix}$uri$suffix")
                ImageSize.REGULAR -> imageList.add("${size.urlPrefix}${uri}_master1200$suffix")
                ImageSize.THUMB -> imageList.add("${size.urlPrefix}${uri}_master1200$suffix")
            }
        }
        return imageList
    }

    private fun filterImage(images: ArrayList<PixivImage>) {
        val integrable = images.iterator()
        while (integrable.hasNext()) {
            val image = integrable.next()
            if((!(image.url.contains("p0_square1200") || image.url.contains("p0_custom1200")))
                || image.pageCount > count * 2)
                integrable.remove()
        }
    }

    override fun recall(): Int = recall

    override fun getHeads() : List<Pair<String, String>> {
        return arrayListOf(Pair("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36"),
                           Pair("referer", "https://www.pixiv.net/"))
    }

    override fun clearCache() {
        pidCache.clear()
        pageCache.clear()
    }

    override suspend fun randomPic(tags: List<String>): List<String>? {
        val tag = tags.joinToString(" ", "", "")
        var page = getPages(tag)
        if (page == 0) {
            return null
        }
        for (page_index in 1..page) {
            val response = retrofit.search(tag, page_index)
            val images = ArrayList((response.body ?: PixivImageSearchResult()).illust.data)
            val resultTotal = response.body?.illust?.total ?: 0
            if (resultTotal != 0L && images.isNotEmpty()) {
                print(images + "\n")
                filterImage(images)
                for (image_index in 0..images.size) {
                    val image = images[image_index]
                    val pixivId = image.id
                    if (pidCache[pixivId] != null) continue
                    else pidCache[pixivId] = 1
                    print("tag:$tag, page:$page_index, index:$image_index, size${images.size}: image:$image\n")
                    return parseImageUrl(image)
                }
            }
        }
        return null
    }
}