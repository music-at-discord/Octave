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

package com.jagrosh.jdautilities.menu

import com.google.common.collect.Lists
import com.jagrosh.jdautilities.waiter.EventWaiter

class PaginatorBuilder(waiter: EventWaiter) : MenuBuilder<PaginatorBuilder>(waiter) {
    private val items: MutableList<String> = mutableListOf()
    private var emptyMessage: String? = null

    private var itemsPerPage = 10

    inline fun entry(lazy: () -> String): PaginatorBuilder {
        return addEntry(lazy())
    }

    fun addEntry(item: String): PaginatorBuilder {
        this.items.add(item)
        return this
    }

    inline fun empty(lazy: () -> String): PaginatorBuilder {
        return setEmptyMessage(lazy())
    }

    fun setEmptyMessage(emptyMessage: String?): PaginatorBuilder {
        this.emptyMessage = emptyMessage
        return this
    }

    fun addAll(items: Collection<String>): PaginatorBuilder {
        this.items.addAll(items)
        return this
    }

    fun setItemsPerPage(itemsPerPage: Int): PaginatorBuilder {
        this.itemsPerPage = itemsPerPage
        return this
    }

    override fun build(): Paginator {
        return Paginator(waiter, user, title, description, color, fields, emptyMessage,
            if (items.isEmpty()) emptyList() else Lists.partition(items, itemsPerPage), timeout, unit, finally)
    }
}