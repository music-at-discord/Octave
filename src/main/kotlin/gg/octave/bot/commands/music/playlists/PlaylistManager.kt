package gg.octave.bot.commands.music.playlists

import gg.octave.bot.commands.music.embedTitle
import gg.octave.bot.commands.music.embedUri
import gg.octave.bot.db.music.CustomPlaylist
import gg.octave.bot.utils.Utils
import gg.octave.bot.utils.extensions.iterate
import gg.octave.bot.utils.extensions.section
import io.sentry.Sentry
import me.devoxin.flight.api.Context
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.jetbrains.kotlin.utils.addToStdlib.sumByLong
import java.util.concurrent.TimeoutException
import kotlin.math.ceil

class PlaylistManager(
    private val playlist: CustomPlaylist,
    private val ctx: Context,
    private val msg: Message
) {
    private val tracks = playlist.decodedTracks

    private val pages: Int
        get() = ceil(tracks.size.toDouble() / ELEMENTS_PER_PAGE).toInt()

    private var page = 1
        set(value) {
            field = value
            renderPage()
        }

    init {
        renderPage()
        waitForInput()
    }

    // COMMAND HANDLING
    private fun sendHelp() {
        ctx.send("WhAtS gOoD")
    }

    private fun remove(index: Int?) {
        if (index == null || index > tracks.size || index < 1) {
            return ctx.send("You need to specify a valid track index, between 1 and ${tracks.size}.")
        }

        tracks.removeAt(index)
        renderPage()
    }

    private fun move(index: Int?, to: Int?) {
        if (index == null || index > tracks.size || index < 1) {
            return ctx.send("You must specify the index of the track you wish to move.")
        }

        if (to == null || to > tracks.size || to < 1 || to == index) {
            return ctx.send("You must specify a valid index to move the track to.")
        }

        val temp = tracks.removeAt(index)
        tracks.add(to, temp)
        renderPage()
    }

    private fun renderPage() {
        val playlistDuration = Utils.getTimestamp(tracks.sumByLong { it.duration })
        val start = ELEMENTS_PER_PAGE * (page - 1)
        val end = (start + ELEMENTS_PER_PAGE).coerceAtMost(tracks.size)
        val trackList = tracks.iterate(start..end)
            .map { (index, track) -> "`${index + 1}.` **[${track.info.embedTitle}](${track.info.embedUri})** `[${Utils.getTimestamp(track.duration)}]`" }
            .joinToString("\n")
            .takeIf { it.isNotEmpty() }
            ?: "No tracks to display."

        val embed = EmbedBuilder().apply {
            setColor(0x9571D3)
            setTitle(playlist.name)
            setDescription(trackList)
            setFooter("Page $page/$pages - Playlist Duration: $playlistDuration - Send \"help\" to view commands")
        }.build()

        msg.editMessage(embed).queue()
    }

    // EVENT WAITING AND INPUT PROCESSING
    // ==================================
    private fun waitForInput() {
        val defaultPredicate = DEFAULT_PREDICATE(ctx.author.idLong, ctx.messageChannel.idLong)
        val response = ctx.commandClient.waitFor(defaultPredicate, 20000)

        response.thenAccept {
            handle(it.message)
            waitForInput()
        }.exceptionally {
            val exc = it.cause ?: it
            playlist.setTracks(tracks)
            playlist.save()

            if (exc !is TimeoutException) {
                Sentry.capture(it)
                ctx.send {
                    setColor(0x9571D3)
                    setTitle("Error in Playlist Management")
                    setDescription(
                        "An unknown error occurred, we apologise for any inconvenience caused.\n" +
                            "Any modifications made to your playlist have been saved."
                    )
                }
            }

            return@exceptionally null
        }
    }

    private fun handle(received: Message) {
        val (command, args) = received.contentRaw.split("\\s+".toRegex()).section()

        when (command) {
            "help" -> sendHelp()
            "remove" -> remove(args.firstOrNull()?.toIntOrNull())
            "move" -> move(args.getOrNull(0)?.toIntOrNull(), args.getOrNull(1)?.toIntOrNull())
            "page" -> {
                val page = args.firstOrNull()?.toIntOrNull()?.coerceIn(1, pages)
                    ?: return ctx.send("You need to specify the page # to jump to.")

                this.page = page
            }
            "save" -> {
                playlist.setTracks(tracks)
                playlist.save()
                ctx.send("Changes saved.")
            }
        }
    }

    companion object {
        private const val ELEMENTS_PER_PAGE = 10
        private val DEFAULT_PREDICATE: (Long, Long) -> (MessageReceivedEvent) -> Boolean = { authorId, channelId ->
            { it.author.idLong == authorId && it.channel.idLong == channelId }
        }

    }
}
