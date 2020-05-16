package gg.octave.bot.music.sources.attachment

import com.sedmelluq.discord.lavaplayer.container.MediaContainerDescriptor
import com.sedmelluq.discord.lavaplayer.container.MediaContainerDetection
import com.sedmelluq.discord.lavaplayer.container.MediaContainerDetectionResult
import com.sedmelluq.discord.lavaplayer.container.MediaContainerHints
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager
import com.sedmelluq.discord.lavaplayer.source.ProbingAudioSourceManager
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import com.sedmelluq.discord.lavaplayer.tools.Units
import com.sedmelluq.discord.lavaplayer.tools.io.HttpClientTools
import com.sedmelluq.discord.lavaplayer.tools.io.HttpClientTools.NoRedirectsStrategy
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterface
import com.sedmelluq.discord.lavaplayer.tools.io.PersistentHttpStream
import com.sedmelluq.discord.lavaplayer.tools.io.ThreadLocalHttpInterfaceManager
import com.sedmelluq.discord.lavaplayer.track.AudioItem
import com.sedmelluq.discord.lavaplayer.track.AudioReference
import com.sedmelluq.discord.lavaplayer.track.AudioTrack
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo
import com.sedmelluq.discord.lavaplayer.track.info.AudioTrackInfoBuilder
import gg.octave.bot.music.utils.LimitedContainerRegistry
import org.apache.http.HttpResponse
import org.apache.http.HttpStatus
import java.io.DataInput
import java.io.DataOutput
import java.io.IOException
import java.net.URI

class DiscordAttachmentAudioSourceManager : ProbingAudioSourceManager(LimitedContainerRegistry()) {
    private val httpInterfaceManager = ThreadLocalHttpInterfaceManager(
        HttpClientTools
            .createSharedCookiesHttpBuilder()
            .setRedirectStrategy(NoRedirectsStrategy()),
        HttpClientTools.DEFAULT_REQUEST_CONFIG
    )
    internal val httpInterface: HttpInterface
        get() = httpInterfaceManager.`interface`

    override fun getSourceName() = "attachment"

    override fun loadItem(manager: DefaultAudioPlayerManager, reference: AudioReference): AudioItem? {
        if (!reference.identifier.startsWith(uriPrefix)) {
            return null
        }

        return if (reference.containerDescriptor != null) {
            createTrack(AudioTrackInfoBuilder.create(reference, null).build(), reference.containerDescriptor)
        } else {
            handleLoadResult(detectContainer(reference.identifier.substring(uriPrefix.length), reference))
        }
    }

    private fun detectContainer(url: String, reference: AudioReference): MediaContainerDetectionResult? {
        return try {
            httpInterface.use { detectContainerWithClient(url, httpInterface, reference) }
        } catch (e: IOException) {
            throw FriendlyException("Connecting to the URL failed.", FriendlyException.Severity.SUSPICIOUS, e)
        }
    }

    @Throws(IOException::class)
    private fun detectContainerWithClient(url: String, httpInterface: HttpInterface, reference: AudioReference): MediaContainerDetectionResult? {
        val uri = inlineTry("Not a valid URL.") { URI(url) }

        // We could probably scrape content-length from headers.
        PersistentHttpStream(httpInterface, uri, Units.CONTENT_LENGTH_UNKNOWN).use { inputStream ->
            val statusCode = inputStream.checkStatusCode()
            if (statusCode == HttpStatus.SC_NOT_FOUND) {
                return null
            } else if (!HttpClientTools.isSuccessWithContent(statusCode)) {
                throw FriendlyException("That URL is not playable.", FriendlyException.Severity.COMMON, IllegalStateException("Status code $statusCode"))
            }
            val hints = MediaContainerHints.from(getHeaderValue(inputStream.currentResponse, "Content-Type"), null)
            return MediaContainerDetection(containerRegistry, reference, inputStream, hints).detectContainer()
        }
    }

    private fun getHeaderValue(response: HttpResponse, name: String) = response.getFirstHeader(name)?.value

    override fun isTrackEncodable(track: AudioTrack) = false

    override fun decodeTrack(trackInfo: AudioTrackInfo, input: DataInput): AudioTrack? {
        val containerTrackFactory = decodeTrackFactory(input)
            ?: return null

        return DiscordAttachmentAudioTrack(trackInfo, containerTrackFactory, this)
    }

    override fun encodeTrack(track: AudioTrack, output: DataOutput) {
        encodeTrackFactory((track as DiscordAttachmentAudioTrack).containerTrackFactory, output)
    }

    override fun createTrack(trackInfo: AudioTrackInfo, containerTrackFactory: MediaContainerDescriptor): AudioTrack {
        return DiscordAttachmentAudioTrack(trackInfo, containerTrackFactory, this)
    }

    override fun shutdown() {
        // Do nothing.
    }

    private fun <R> inlineTry(friendlyError: String, block: () -> R): R {
        return try {
            block()
        } catch (e: Throwable) {
            throw FriendlyException(friendlyError, FriendlyException.Severity.COMMON, e)
        }
    }

    companion object {
        internal val uriPrefix = "discord://"
    }
}
