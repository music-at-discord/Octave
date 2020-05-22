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

@file:Suppress("NOTHING_TO_INLINE")

package com.jagrosh.jdautilities.menu

import com.jagrosh.jdautilities.waiter.EventWaiter
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User
import java.awt.Color
import java.util.concurrent.TimeUnit

@Suppress("UNCHECKED_CAST")
abstract class MenuBuilder<T : MenuBuilder<T>>(val waiter: EventWaiter) {
    companion object {
        @JvmStatic
        val DEFAULT_FINALLY: (Message?) -> Unit = {} //{ it?.delete()?.queue() }
    }

    protected var user: User? = null
    protected var title: String? = "Menu"
    protected var description: String? = null
    protected var color: Color? = null
    protected var finally: (Message?) -> Unit = DEFAULT_FINALLY
    protected var timeout: Long = 20
    protected var unit: TimeUnit = TimeUnit.SECONDS
    protected val fields: MutableList<MessageEmbed.Field> = arrayListOf()

    fun setTitle(title: String?): T {
        this.title = title
        return this as T
    }

    fun setDescription(description: String?): T {
        this.description = description
        return this as T
    }

    fun setColor(color: Color?): T {
        this.color = color
        return this as T
    }

    fun setUser(user: User): T {
        this.user = user
        return this as T
    }

    fun finally(action: (Message?) -> Unit): T {
        this.finally = action
        return this as T
    }

    fun setTimeout(timeout: Long, unit: TimeUnit): T {
        this.timeout = timeout
        this.unit = unit
        return this as T
    }

    fun addField(field: MessageEmbed.Field?): T {
        return if (field == null) this as T else addField(field.name, field.value, field.isInline)
    }

    fun addField(name: String?, value: String?, inline: Boolean): T {
        if (name == null && value == null) {
            return this as T
        }
        fields.add(MessageEmbed.Field(name, value, inline))
        return this as T
    }

    fun addBlankField(inline: Boolean): T {
        fields.add(MessageEmbed.Field(EmbedBuilder.ZERO_WIDTH_SPACE, EmbedBuilder.ZERO_WIDTH_SPACE, inline))
        return this as T
    }

    inline fun field(inline: Boolean = false) = addBlankField(inline)

    inline fun field(name: String?, inline: Boolean = false, value: () -> Any?): T {
        addField(name, value().toString(), inline)
        return this as T
    }

    abstract fun build(): Any
}