package gg.octave.bot.music.sources.attachment

import com.sedmelluq.discord.lavaplayer.container.MediaContainerDescriptor
import com.sedmelluq.discord.lavaplayer.tools.Units
import com.sedmelluq.discord.lavaplayer.tools.io.PersistentHttpStream
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo
import com.sedmelluq.discord.lavaplayer.track.DelegatedAudioTrack
import com.sedmelluq.discord.lavaplayer.track.InternalAudioTrack
import com.sedmelluq.discord.lavaplayer.track.playback.LocalAudioTrackExecutor
import java.net.URI

class DiscordAttachmentAudioTrack(
    discordTrackInfo: AudioTrackInfo,
    internal val containerTrackFactory: MediaContainerDescriptor,
    private val sourceManager: DiscordAttachmentAudioSourceManager
) : DelegatedAudioTrack(discordTrackInfo) {
    override fun getSourceManager() = sourceManager
    override fun makeShallowClone() = DiscordAttachmentAudioTrack(trackInfo, containerTrackFactory, sourceManager)

    override fun process(executor: LocalAudioTrackExecutor) {
        sourceManager.httpInterface.use {
            PersistentHttpStream(it, URI(trackInfo.identifier), Units.CONTENT_LENGTH_UNKNOWN).use { inputStream ->
                processDelegate(containerTrackFactory.createTrack(trackInfo, inputStream) as InternalAudioTrack, executor)
            }
        }
    }
}
