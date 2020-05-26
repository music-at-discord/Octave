package gg.octave.bot.commands.music.playlists

import gg.octave.bot.db.music.CustomPlaylist
import gg.octave.bot.utils.extensions.DEFAULT_SUBCOMMAND
import gg.octave.bot.utils.extensions.db
import me.devoxin.flight.api.CommandFunction
import me.devoxin.flight.api.Context
import me.devoxin.flight.api.annotations.Command
import me.devoxin.flight.api.annotations.Greedy
import me.devoxin.flight.api.annotations.SubCommand
import me.devoxin.flight.api.entities.Cog
import net.dv8tion.jda.api.EmbedBuilder

class Playlists : Cog {
    override fun localCheck(ctx: Context, command: CommandFunction): Boolean {
        return ctx.author.idLong in ctx.commandClient.ownerIds
    }

    @Command(aliases = ["pl", "cpl"], description = "Manage your custom playlists.", hidden = true)
    fun playlists(ctx: Context) = DEFAULT_SUBCOMMAND(ctx)

    @SubCommand
    fun list(ctx: Context) {
        val playlists = ctx.db.getCustomPlaylists(ctx.author.id).takeIf { it.isNotEmpty() }
            ?: return ctx.send {
                setColor(0x9571D3)
                setTitle("No Playlists :(")
                setDescription("That's OK! You can create a new one with `${ctx.trigger}playlists create <name>`\n*Without the `<>` of course.*")
            }

        val joined = playlists.joinToString("\n") { it.name }
        ctx.send("You have **${playlists.size}** custom playlists. Here they are:\n$joined")
    }

    @SubCommand(aliases = ["new", "add", "+"])
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

    @SubCommand(aliases = ["del", "remove", "-"])
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

    @SubCommand(aliases = ["manage"])
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
