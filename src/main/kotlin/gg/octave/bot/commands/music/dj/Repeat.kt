/*
 * MIT License
 *
 * Copyright (c) 2020 Melms Media LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
