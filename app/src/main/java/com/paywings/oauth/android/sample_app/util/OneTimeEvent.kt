package com.paywings.oauth.android.sample_app.util

import java.util.concurrent.atomic.AtomicBoolean

class OneTimeEvent<T>(
    private val value: T) {
    private val isConsumed = AtomicBoolean(false)

    internal fun getValue(): T? =
        if (isConsumed.compareAndSet(false, true)) value
        else null
}

fun <T> T.asOneTimeEvent() = OneTimeEvent(this)

fun <T> OneTimeEvent<T>.consume(block: (T) -> Unit): T? = getValue()?.also(block)
