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

package gg.octave.bot.music.utils

import me.devoxin.flight.api.Context
import java.awt.Color

class MusicLimitException : Exception() {
    fun sendToContext(ctx: Context) {
        ctx.send {
            setColor(Color.ORANGE)
            setTitle("Maximum Capacity")
            setDescription(
                buildString {
                    append("Music is currently at maximum capacity, please try again later.\n")
                    append("Please consider donating to our **[Patreon](https://www.patreon.com/octavebot)** ")
                    append("to help us with hosting costs.")
                }
            )
            addField(
                "Why am I seeing this?",
                buildString {
                    append("Our music feature has a limit on how many channels we can play to at once, so we can ")
                    append("keep our music quality high and our server healthy. Donating will allow you to ")
                    append("bypass the limit, help us expand and upgrade our processing power.")
                },
                false
            )
        }
    }
}
