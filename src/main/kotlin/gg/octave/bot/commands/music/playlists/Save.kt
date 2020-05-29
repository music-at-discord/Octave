package gg.octave.bot.commands.music.playlists

import gg.octave.bot.Launcher
import gg.octave.bot.commands.music.embedTitle
import gg.octave.bot.commands.music.embedUri
import gg.octave.bot.entities.framework.MusicCog
import gg.octave.bot.utils.extensions.db
import gg.octave.bot.utils.extensions.existingManager
import me.devoxin.flight.api.Context
import me.devoxin.flight.api.annotations.Command
import me.devoxin.flight.api.annotations.Greedy

class Save : MusicCog {
    override fun requirePlayer() = true
    override fun requirePlayingTrack() = true

    @Command(aliases = ["+"], description = "Saves the current track to a custom playlist.")
    fun saveto(ctx: Context, @Greedy playlist: String) {
        val track = ctx.existingManager?.player?.playingTrack
            ?: return ctx.send("There's no player here.") // Shouldn't happen.

        val existingPlaylist = ctx.db.findCustomPlaylist(ctx.author.id, playlist)
            ?: return ctx.send("No custom playlists found with that name.")

        existingPlaylist.addTrack(track)
        existingPlaylist.save()

        ctx.send {
            setColor(0x9571D3)
            setTitle("Track Added")
            setDescription("Added [${track.info.embedTitle}](${track.info.embedUri}) to **${existingPlaylist.name}**")
        }
    }

    @Command(aliases = ["sq"], description = "Saves the entire queue to a custom playlist.")
    fun savequeue(ctx: Context, @Greedy playlist: String) {
        val manager = ctx.existingManager
            ?: return ctx.send("There's no player here.") // Shouldn't happen.

        val existingPlaylist = ctx.db.findCustomPlaylist(ctx.author.id, playlist)
            ?: return ctx.send("No custom playlists found with that name.")

        if (manager.queue.isEmpty()) {
            return ctx.send("There's nothing to save - the queue is empty.")
        }

        val tracks = manager.queue.map(Launcher.players.playerManager::decodeTrack)
        existingPlaylist.addTracks(tracks)
        existingPlaylist.save()

        ctx.send {
            setColor(0x9571D3)
            setTitle("Tracks Added")
            setDescription("Added `${tracks.size}` tracks to **${existingPlaylist.name}**")
        }
    }
}
