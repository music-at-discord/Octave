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

            //Remove null guilds.
            players.filter { it.guild == null }.forEach { players.remove(it) }

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