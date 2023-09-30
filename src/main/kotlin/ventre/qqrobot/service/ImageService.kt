package ventre.qqrobot.service

interface ImageService {
    suspend fun randomPic(tags: List<String>): List<String>?
    fun recall(): Int
    fun getHeads() : List<Pair<String, String>>
    fun clearCache()
}