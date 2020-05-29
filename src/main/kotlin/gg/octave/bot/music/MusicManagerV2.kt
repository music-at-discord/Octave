package gg.octave.bot.music

import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame
import gg.octave.bot.Launcher
import gg.octave.bot.music.filters.DSPFilter
import gg.octave.bot.music.utils.DiscordFMTrackContext
import gg.octave.bot.music.utils.TrackContext
import gg.octave.bot.utils.Task
import gg.octave.bot.utils.extensions.insertAt
import net.dv8tion.jda.api.audio.AudioSendHandler
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.TextChannel
import org.redisson.api.RQueue
import java.nio.ByteBuffer
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

class MusicManagerV2(val guildId: Long, val player: AudioPlayer) : AudioSendHandler, AudioEventAdapter() {
    // Misc
    val guild: Guild? get() = Launcher.shardManager.getGuildById(guildId)
    val isAlone: Boolean get() = guild?.selfMember?.voiceState?.channel?.members?.none { !it.user.isBot } ?: true
    val isIdle: Boolean get() = player.playingTrack == null && queue.isEmpty()

    // Playback/Music related.
    val queue: RQueue<String> = Launcher.db.redisson.getQueue("playerQueue:$guildId")
    val dspFilter = DSPFilter(player)
    var lastTrack: AudioTrack? = null
    var discordFMTrack: DiscordFMTrackContext? = null

    // Settings/internals.
    private val leaveTask = Task(30, TimeUnit.SECONDS) { destroy() }
    private val isLeaveQueued: Boolean get() = leaveTask.isRunning

    private val dbAnnouncementChannel: String? get() = Launcher.db.getGuildData(guildId.toString())?.music?.announcementChannel
    private val currentRequestChannel: TextChannel?
        get() = (player.playingTrack ?: lastTrack)?.getUserData(TrackContext::class.java)
            ?.requestedChannel?.let { guild?.getTextChannelById(it) }
    private val announcementChannel: TextChannel?
        get() = dbAnnouncementChannel?.let { guild?.getTextChannelById(it) } ?: currentRequestChannel

    var lastVoteTime = 0L
    var isVotingToSkip = false
    var isVotingToPlay = false
    var lastPlayVoteTime = 0L
    var lastPlayedAt = 0L

    var loops = 0L
        private set

    // ---------- End Properties ----------

    init {
        player.addListener(this)
    }

    fun enqueue(track: AudioTrack, isNext: Boolean) {
        if (!player.startTrack(track, true)) {
            val encoded = Launcher.players.playerManager.encodeAudioTrack(track)
            if (isNext) {
                queue.insertAt(0, encoded)
            } else {
                queue.offer(encoded)
            }
        }
    }

    fun destroy() = Launcher.players.destroy(guildId)

    fun cleanup() {
        player.destroy()
        dspFilter.clearFilters()
        queue.expire(4, TimeUnit.HOURS)

        //closeAudioConnection()
    }

    override fun onTrackEnd(player: AudioPlayer, track: AudioTrack, endReason: AudioTrackEndReason) {
        lastPlayedAt = System.currentTimeMillis()
    }


    // *----------- AudioSendHandler -----------*
    private val frameBuffer = ByteBuffer.allocate(StandardAudioDataFormats.DISCORD_OPUS.maximumChunkSize())
    private val lastFrame = MutableAudioFrame().also { it.setBuffer(frameBuffer) }

    override fun canProvide() = player.provide(lastFrame)
    override fun provide20MsAudio() = frameBuffer.flip()
    override fun isOpus() = true
}
