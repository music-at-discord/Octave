package gg.octave.bot.commands.music.playlists

import gg.octave.bot.utils.extensions.DEFAULT_SUBCOMMAND
import me.devoxin.flight.api.CommandFunction
import me.devoxin.flight.api.Context
import me.devoxin.flight.api.annotations.Command
import me.devoxin.flight.api.annotations.Greedy
import me.devoxin.flight.api.annotations.SubCommand
import me.devoxin.flight.api.entities.Cog

class Playlists : Cog {
    override fun localCheck(ctx: Context, command: CommandFunction): Boolean {
        return ctx.author.idLong in ctx.commandClient.ownerIds
    }

    @Command(aliases = ["pl", "cpl"], description = "Manage your custom playlists.", hidden = true)
    fun playlists(ctx: Context) = DEFAULT_SUBCOMMAND(ctx)

    @SubCommand
    fun list(ctx: Context, @Greedy name: String) {

    }

    @SubCommand(aliases = ["new", "+"])
    fun create(ctx: Context, @Greedy name: String) {

    }

    @SubCommand(aliases = ["del", "remove", "-"])
    fun delete(ctx: Context, @Greedy name: String) {

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
