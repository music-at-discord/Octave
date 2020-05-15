package gg.octave.bot.commands.music.search

import me.devoxin.flight.api.Context
import me.devoxin.flight.api.annotations.Command
import me.devoxin.flight.api.annotations.Greedy
import me.devoxin.flight.api.entities.Cog
import java.awt.Color

class Youtube : Cog {
    @Command(aliases = ["yt"], description = "Search and see YouTube results.")
    fun youtube(ctx: Context, @Greedy query: String) = GenericSearchCommand(ctx, query, "ytsearch", "YouTube", Color(141, 20, 0),
        "https://www.youtube.com", "https://www.youtube.com/favicon.ico", "https://octave.gg/assets/img/youtube.png")
}
