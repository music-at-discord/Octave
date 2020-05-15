package gg.octave.bot.commands.music.search

import me.devoxin.flight.api.Context
import me.devoxin.flight.api.annotations.Command
import me.devoxin.flight.api.annotations.Greedy
import me.devoxin.flight.api.entities.Cog
import java.awt.Color

class Soundcloud : Cog {
    @Command(aliases = ["sc"], description = "Search and see SoundCloud results.")
    fun soundcloud(ctx: Context, @Greedy query: String) = GenericSearchCommand(ctx, query, "scsearch", "SoundCloud", Color(255, 110, 0),
        "https://soundcloud.com", "https://soundcloud.com/favicon.ico", "https://octave.gg/assets/img/soundcloud.png")
}
