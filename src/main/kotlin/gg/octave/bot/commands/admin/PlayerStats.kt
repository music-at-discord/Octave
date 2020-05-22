package gg.octave.bot.commands.admin

import gg.octave.bot.Launcher
import gg.octave.bot.music.MusicManager
import gg.octave.bot.music.settings.BoostSetting
import me.devoxin.flight.api.Context
import me.devoxin.flight.api.annotations.Command
import me.devoxin.flight.api.entities.Cog
import org.json.JSONObject

class PlayerStats : Cog {
    @Command(aliases = ["ps"], description = "Shows (est) encoding, and total players", developerOnly = true)
    fun playerstats(ctx: Context) {
        val players = Launcher.players.registry.values
        var musicPlayers = 0L

        Launcher.database.jedisPool.resource.use {
            val nodeStats = it.hgetAll("node-stats")
            for (node in nodeStats) {
                val jsonStats = JSONObject(node.value);
                musicPlayers += jsonStats.getLong("music_players")
            }
        }

        val paused = players.filter { it.player.isPaused }.size
        val encoding = players.filter(::isEncoding).size
        val alone = players.filter { it.guild?.audioManager?.connectedChannel?.members?.count { m -> !m.user.isBot } == 0 }.size
        val bySource = sources.associateBy({ it }, { players.filter { m -> isSource(it, m) } })
        val bySourceFormatted = bySource.map { "• ${it.key.capitalize()}: **${it.value.size}**" }.joinToString("\n")

        ctx.send {
            setColor(0x9570D3)
            setTitle("Player Statistics")
            setDescription("**This node**: ${players.size}\n" +
                    "**All nodes**: $musicPlayers")
            addField("Source Insight", "This node:\n$bySourceFormatted", true)
            addField("Statistics", "• **$encoding** encoding\n• **$paused** paused\n• **$alone** alone", true)
        }
    }

    private fun isEncoding(manager: MusicManager): Boolean {
        val hasDspFx = manager.dspFilter.let {
            it.karaokeEnable || it.timescaleEnable || it.tremoloEnable || it.bassBoost != BoostSetting.OFF
        }

        return manager.player.playingTrack != null &&
            (manager.player.volume != 100 || hasDspFx || !opusSources.any { it in manager.player.playingTrack.info.uri })
    }

    private fun isSource(source: String, manager: MusicManager): Boolean {
        return manager.player.playingTrack != null && manager.player.playingTrack.sourceManager.sourceName == source
    }

    companion object {
        private val opusSources = listOf("youtube", "soundcloud")
        private val sources = listOf("youtube", "soundcloud", "getyarn.io", "bandcamp", "vimeo", "twitch", "beam.pro")
    }
}
