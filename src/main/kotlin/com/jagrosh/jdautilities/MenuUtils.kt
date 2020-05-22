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

package com.jagrosh.jdautilities

import com.jagrosh.jdautilities.menu.Paginator
import com.jagrosh.jdautilities.menu.PaginatorBuilder
import com.jagrosh.jdautilities.menu.Selector
import com.jagrosh.jdautilities.menu.SelectorBuilder
import com.jagrosh.jdautilities.waiter.EventWaiter

inline fun EventWaiter.selector(action: SelectorBuilder.() -> Unit): Selector {
    return SelectorBuilder(this).apply(action).build()
}

inline fun EventWaiter.paginator(action: PaginatorBuilder.() -> Unit): Paginator {
    return PaginatorBuilder(this).apply(action).build()
}