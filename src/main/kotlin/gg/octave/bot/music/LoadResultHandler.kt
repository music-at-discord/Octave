package gg.octave.bot.music

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import me.devoxin.flight.api.Context

class LoadResultHandler(private val ctx: Context, private val musicManager: MusicManagerV2) : AudioLoadResultHandler {

    override fun trackLoaded(track: AudioTrack) {
    }

    override fun playlistLoaded(playlist: AudioPlaylist) {

    }

    override fun loadFailed(exception: FriendlyException) {

    }

    override fun noMatches() {

    }
}