/*
 * MIT License
 *
 * Copyright (c) 2020 Melms Media LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package gg.octave.bot.entities.sharding

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.utils.SessionController.SessionConnectNode
import net.dv8tion.jda.api.utils.SessionController.ShardedGateway
import net.dv8tion.jda.api.utils.SessionControllerAdapter
import java.util.*
import java.util.concurrent.PriorityBlockingQueue
import javax.annotation.Nonnull

//created by napster (https://github.com/napstr)
class PrioritizingSessionController(private val homeGuildId: Long) : SessionControllerAdapter(), Comparator<SessionConnectNode> {
    init {
        connectQueue = PriorityBlockingQueue(1, this)
    }

    override fun compare(s1: SessionConnectNode, s2: SessionConnectNode): Int {
        //if one of the shards is containing the home guild, do it first always
        if (isHomeShard(s1))
            return -1

        if (isHomeShard(s2))
            return 1

        //if both or none are reconnecting, order by their shard ids
        if (s1.isReconnect && s2.isReconnect || !s1.isReconnect && !s2.isReconnect)
            return s1.shardInfo.shardId - s2.shardInfo.shardId

        //otherwise prefer the one that is reconnecting
        return when {
            s1.isReconnect -> -1
            else -> 1
        }
    }

    private fun getHomeShardId(shardTotal: Int): Long {
        return (homeGuildId shr 22) % shardTotal
    }

    private fun isHomeShard(node: SessionConnectNode): Boolean {
        return homeGuildId != -1L && getHomeShardId(node.shardInfo.shardTotal) == node.shardInfo.shardId.toLong()
    }

    @Nonnull
    override fun getShardedGateway(@Nonnull api: JDA): ShardedGateway {
        throw UnsupportedOperationException()
    }

    @Nonnull
    override fun getGateway(@Nonnull api: JDA): String {
        throw UnsupportedOperationException()
    }

    override fun getGlobalRatelimit(): Long {
        throw UnsupportedOperationException()
    }

    override fun setGlobalRatelimit(ratelimit: Long) {
        throw UnsupportedOperationException()
    }
}
