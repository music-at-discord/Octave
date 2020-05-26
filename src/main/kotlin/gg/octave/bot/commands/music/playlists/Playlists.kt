package gg.octave.bot.commands.music.playlists

import com.sedmelluq.discord.lavaplayer.player.FunctionalResultHandler
import gg.octave.bot.Launcher
import gg.octave.bot.db.music.CustomPlaylist
import gg.octave.bot.utils.extensions.DEFAULT_SUBCOMMAND
import gg.octave.bot.utils.extensions.db
import gg.octave.bot.utils.extensions.iterate
import me.devoxin.flight.api.CommandFunction
import me.devoxin.flight.api.Context
import me.devoxin.flight.api.annotations.Command
import me.devoxin.flight.api.annotations.Greedy
import me.devoxin.flight.api.annotations.SubCommand
import me.devoxin.flight.api.entities.Cog
import net.dv8tion.jda.api.EmbedBuilder
import java.net.URL
import java.util.function.Consumer
import kotlin.math.ceil

class Playlists : Cog {
    override fun localCheck(ctx: Context, command: CommandFunction): Boolean {
        return ctx.author.idLong in ctx.commandClient.ownerIds
    }

    @Command(aliases = ["pl", "cpl"], description = "Manage your custom playlists.", hidden = true)
    fun playlists(ctx: Context) = DEFAULT_SUBCOMMAND(ctx)

    @SubCommand(description = "Lists all of your custom playlists.")
    fun list(ctx: Context, page: Int = 1) {
        val playlists = ctx.db.getCustomPlaylists(ctx.author.id).takeIf { it.isNotEmpty() }
            ?: return ctx.send {
                setColor(0x9571D3)
                setTitle("No Playlists :(")
                setDescription("That's OK! You can create a new one with `${ctx.trigger}playlists create <name>`\n*Without the `<>` of course.*")
            }

        val pages = ceil(playlists.size.toDouble() / 10).toInt()
        val start = 10 * (page - 1).coerceAtLeast(0)
        val end = (start + 10).coerceAtMost(playlists.size)
        val showing = end - start
        val joined = playlists.iterate(start..end).joinToString("\n") { (index, pl) -> "`${index + 1}.` ${pl.name}" }

        ctx.send {
            setColor(0x9571D3)
            setTitle("Your Playlists")
            setDescription(joined)
            setFooter("Showing $showing of ${playlists.size} playlists • Page $page/$pages")
        }
    }

    @SubCommand(aliases = ["new", "add", "+"], description = "Create a new custom playlist.")
    fun create(ctx: Context, @Greedy name: String) {
        val existingPlaylist = ctx.db.getCustomPlaylist(ctx.author.id, name)

        if (existingPlaylist != null) {
            return ctx.send("You already have a playlist with this name.")
        }

        CustomPlaylist.createWith(ctx.author.id, name)
            .save()

        ctx.send {
            setColor(0x9571D3)
            setTitle("Playlist Created")
            setDescription("Your shiny new playlist has been created.")
        }
    }

    @SubCommand(aliases = ["del", "remove", "-"], description = "Delete one of your custom playlists.")
    fun delete(ctx: Context, @Greedy name: String) {
        val existingPlaylist = ctx.db.getCustomPlaylist(ctx.author.id, name)
            ?: return ctx.send("You don't have any playlists with that name.")

        existingPlaylist.delete()

        ctx.send {
            setColor(0x9571D3)
            setTitle("Playlist Deleted")
            setDescription("Your custom playlist has been removed.")
        }
    }

    @SubCommand(aliases = ["manage"], description = "Edit an existing playlist (move/remove/...)")
    fun edit(ctx: Context, @Greedy name: String) {
        val existingPlaylist = ctx.db.getCustomPlaylist(ctx.author.id, name)
            ?: return ctx.send("You don't have any playlists with that name.")

        ctx.messageChannel.sendMessage(EmbedBuilder().apply {
            setColor(0x9571D3)
            setDescription("Loading playlist...")
        }.build()).queue({
            PlaylistManager(existingPlaylist, ctx, it)
        }, {
            ctx.send("Failed to load playlist: `${it.localizedMessage}`")
        })
    }

    @SubCommand(description = "Import a playlist from YouTube/SoundCloud/...")
    fun import(ctx: Context, url: URL, @Greedy name: String?) {
        val loader = FunctionalResultHandler(
            Consumer { ctx.send("This is not a playlist.") },
            Consumer {
                val importName = name
                    ?: it.name
                    ?: return@Consumer ctx.send("The playlist does not have a name. You need to specify one instead.")
                // Last bit shouldn't happen but better safe than sorry.

                val existing = ctx.db.getCustomPlaylist(ctx.author.id, importName)

                if (existing != null) {
                    return@Consumer ctx.send("A playlist with that name already exists. Specify a different one.")
                    // Maybe we could append tracks to a playlist here? TODO, or, INVESTIGATE
                }

                val playlist = CustomPlaylist.createWith(ctx.author.id, importName)
                playlist.setTracks(it.tracks)
                playlist.save()

                ctx.send("Playlist imported as `$importName` successfully!")
            },
            Runnable { ctx.send("The URL doesn't lead to a valid playlist.") },
            Consumer { ctx.send("Failed to load the media resource.\n`${it.localizedMessage}`") }
        )

        Launcher.players.playerManager.loadItem(url.toString(), loader)
    }

    // fun share(ctx: Context, @Greedy name: String)
    // fun privacy(ctx: Context, setting: ..., @Greedy name: String) // Changes whether a playlist can be viewed by other users.
    // fun snoop(ctx: Context, user: User) // snoop on other user's custom playlists.

    // fun addall(playlist link)
    // fun addsingle(track link)
    // fun saveto(now playing)
    // maybe something more intuitive

    // methods to remove a track or multiple from playlist
    // also method to use playlist for radio
}
