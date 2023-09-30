package ventre.qqrobot.message

import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.MessageChain
import ventre.qqrobot.utils.reply
import java.util.*

object RepeatHander : GroupMessageHandleNotAt {
    private val msgList = LinkedList<MessageEvent>()
    private val SIZE = 5
    private val REPEAT = 2

    private fun sameMessage(m1: MessageChain, m2: MessageChain):Boolean{
        if(m1.size > 2 || m2.size > 2 || m1.size != m2.size) return false
        for(i in 1 until m1.size) {
            //if (!m1[i].contentEquals(m2[i], false, true)) return false
            if (m1[i].toString() != m2[i].toString()) return false
        }
        return true
    }

    private fun repeat(messageEvent: MessageEvent) : Boolean {
        var count = 0
        for(msg in msgList) {
            if(sameMessage(messageEvent.message, msg.message)) count++
        }
        return count >= REPEAT
    }

    override suspend fun handleMessage(messageEvent: MessageEvent): Boolean {
        print("RepeatHander.handleMessage\n")
        while(msgList.size > SIZE) {
            msgList.remove()
        }

        if(repeat(messageEvent)) {
            messageEvent.reply(messageEvent.message)
            msgList.clear()
        } else
            msgList.add(messageEvent)
        return false
    }
}