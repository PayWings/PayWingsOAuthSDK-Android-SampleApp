package com.paywings.oauth.android.sample_app.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun Long.toLocalDateTime(): String {
    return Instant.ofEpochSecond(this).atZone(ZoneId.systemDefault()).toLocalDateTime().format(
        DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm:ss a", Locale.getDefault()))
/*
    return Instant.ofEpochSecond(this).atOffset(ZoneOffset.UTC).atZoneSameInstant(ZoneId.systemDefault()).format()
        .atZone(context.get) .toString()// . atZone( ZoneId.systemDefault()).toLocalDateTime()
        //.atZone(ZoneId.systemDefault())

 */
}

