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

package gg.octave.bot.music.utils

import gg.octave.bot.Launcher
import gg.octave.bot.db.OptionsRegistry
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class PlayerSweeper {
    private val executor = Executors.newSingleThreadScheduledExecutor()

    fun runEvery(timeUnit: TimeUnit, amount: Long) {
        executor.scheduleAtFixedRate({
            val players = Launcher.players.registry.values

            players.filter {
                //In short: If the manager is connected, if there's is NO playing track, if it hasn't been queued for
                //leave, if there has been 2 minutes without a new song playing (and nothing is playing, as said above)
                //and if allDayMusic hasn't been enabled.
                it.guild!!.audioManager.isConnected && it.player.playingTrack == null &&
                        !it.leaveQueued && System.currentTimeMillis() - it.lastPlayedAt > 120000 &&
                        !isAllDayMusic(it.guildId)
            }.forEach { it.queueLeave() } //Then queue leave.
        }, amount, amount, timeUnit)
    }

    private fun isAllDayMusic(guildId: String) : Boolean {
        val premium = Launcher.database.getPremiumGuild(guildId)
        val guildData = OptionsRegistry.ofGuild(guildId)
        val key = guildData.isPremium

        return (premium != null || key) && guildData.music.isAllDayMusic
    }
}