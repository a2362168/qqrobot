package ventre.qqrobot.utils

import java.util.*
import kotlin.collections.HashMap

class CacheMap<K,V>(private val time : Long) : HashMap<K, V>() {

    class CacheTimerTask(val runnable : ()->Unit) : TimerTask() {
        override fun run() {
            runnable()
        }
    }

    override fun put(key:K, value: V): V? {
        Timer().schedule(CacheTimerTask(){super.remove(key)}, time)
        return super.put(key, value)
    }

}