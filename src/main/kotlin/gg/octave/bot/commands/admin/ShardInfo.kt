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

package gg.octave.bot.commands.admin

import gg.octave.bot.Launcher
import gg.octave.bot.utils.TextSplitter
import me.devoxin.flight.api.Context
import me.devoxin.flight.api.annotations.Command
import me.devoxin.flight.api.entities.Cog
import net.dv8tion.jda.api.JDA

class ShardInfo : Cog {
    @Command(aliases = ["shards", "shard"], description = "View shard information.", developerOnly = true)
    suspend fun shardinfo(ctx: Context) {
        val status = ctx.jda.shardManager!!.shards.reversed().joinToString("\n", transform = ::formatInfo)
        val pages = TextSplitter.split(status, 1920)

        for (page in pages) {
            ctx.sendAsync("```prolog\n ID |    STATUS |    PING | GUILDS |  USERS |  VC\n$page```")
        }
    }

    private fun formatInfo(jda: JDA): String {
        val shardId = jda.shardInfo.shardId
        val totalShards = jda.shardInfo.shardTotal

        return "%3d | %9.9s | %7.7s | %6d | %6d | %3d".format(
            shardId,
            jda.status,
            "${jda.gatewayPing}ms",
            jda.guildCache.size(),
            jda.userCache.size(),
            Launcher.players.registry.values.count { getShardIdForGuild(it.guildId, totalShards) == shardId }
        )
    }

    private fun getShardIdForGuild(guildId: String, shardCount: Int): Int {
        return ((guildId.toLong() shr 22) % shardCount).toInt()
    }
}
