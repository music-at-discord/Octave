package gg.octave.bot.music

import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame
import gg.octave.bot.Launcher
import gg.octave.bot.music.filters.DSPFilter
import net.dv8tion.jda.api.audio.AudioSendHandler
import org.redisson.api.RQueue
import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit

class MusicManagerV2(val guildId: Long, val player: AudioPlayer) : AudioSendHandler, AudioEventAdapter() {
    val queue: RQueue<String> = Launcher.db.redisson.getQueue("playerQueue:$guildId")
    val dspFilter = DSPFilter(player)

    init {
        player.addListener(this)
    }


    fun destroy() = Launcher.players.destroy(guildId)

    fun cleanup() {
        player.destroy()
        dspFilter.clearFilters()
        queue.expire(4, TimeUnit.HOURS)

        //closeAudioConnection()
    }

    // *----------- Scheduler/Event Handling -----------*


    // *----------- AudioSendHandler -----------*
    private val frameBuffer = ByteBuffer.allocate(StandardAudioDataFormats.DISCORD_OPUS.maximumChunkSize())
    private val lastFrame = MutableAudioFrame().also { it.setBuffer(frameBuffer) }

    override fun canProvide() = player.provide(lastFrame)
    override fun provide20MsAudio() = frameBuffer.flip()
    override fun isOpus() = true
}