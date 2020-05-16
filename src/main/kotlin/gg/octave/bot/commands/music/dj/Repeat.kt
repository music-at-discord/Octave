package gg.octave.bot.commands.music.dj

import gg.octave.bot.entities.framework.CheckVoiceState
import gg.octave.bot.entities.framework.DJ
import gg.octave.bot.entities.framework.MusicCog
import gg.octave.bot.music.settings.RepeatOption
import gg.octave.bot.utils.extensions.manager
import me.devoxin.flight.api.Context
import me.devoxin.flight.api.annotations.Command

class Repeat : MusicCog {
    override fun sameChannel() = true
    override fun requirePlayer() = true

    @DJ
    @CheckVoiceState
    @Command(aliases = ["loop"], description = "Set if the music player should repeat")
    fun repeat(ctx: Context, option: RepeatOption) {
        ctx.manager.scheduler.repeatOption = option
        ctx.send("${option.emoji} Track repeating was set to __**${option.name.toLowerCase()}**__.")
    }

    @DJ
    @CheckVoiceState
    @Command(aliases = ["lq"], description = "(Alias) Repeats the queue.", hidden = true)
    fun rq(ctx: Context) = repeat(ctx, RepeatOption.QUEUE)

    @DJ
    @CheckVoiceState
    @Command(aliases = ["ls"], description = "(Alias) Repeats the song.", hidden = true)
    fun rs(ctx: Context) = repeat(ctx, RepeatOption.SONG)

    @DJ
    @CheckVoiceState
    @Command(aliases = ["ln"], description = "(Alias) Disables track repeating.", hidden = true)
    fun rn(ctx: Context) = repeat(ctx, RepeatOption.NONE)
}
