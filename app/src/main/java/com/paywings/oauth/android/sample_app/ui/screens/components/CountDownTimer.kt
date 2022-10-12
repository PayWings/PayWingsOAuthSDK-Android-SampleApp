package com.paywings.oauth.android.sample_app.ui.screens.components

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paywings.oauth.android.sample_app.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToLong


private class CountDownTimer(durationMillis: Long, startMillis: Long, onCompleted: () -> Unit) {

    var currentPosition: Float = 0f
    var durationMillis: Long = 0
    val progress = mutableStateOf(currentPosition)
    private val mHandler: Handler by lazy { Handler(Looper.getMainLooper()) }

    init {
        this.currentPosition = startMillis.toFloat()
        this.durationMillis = durationMillis
        startCountdown(durationMillis, onCompleted)
    }

    @SuppressLint("SimpleDateFormat")
    fun getTime(millis: Long): String {
        val format: String = when {
            millis > 3600 * 1000 -> {
                "HH:mm:ss"
            }
            millis > 60 * 1000 -> {
                "mm:ss"
            }
            else -> {
                "ss"
            }
        }
        val sdf = SimpleDateFormat(format)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return (sdf.format(Date(millis)))
    }

    private fun startCountdown(max: Long, onCompleted: () -> Unit) {
        mHandler.removeCallbacksAndMessages(null)
        if (currentPosition < max) {
            mHandler.postDelayed({
                currentPosition += 1000
                progress.value = 1f - ((currentPosition * 100) / max) / 100
                if (currentPosition >= max) {
                    done(onCompleted)
                } else {
                    startCountdown(max, onCompleted)
                }
            }, 1000)
        } else {
            done(onCompleted)
        }
    }

    private fun done(onCompleted: () -> Unit) {
        onCompleted()
        mHandler.removeCallbacksAndMessages(null)
        println("CountDownTimer: done")
    }
}

@Composable
fun CountDownTimer(
    modifier: Modifier,
    durationMillis: Long,
    startTimeMillis: Long = 0L,
    showText: Boolean = true,
    onCompleted: () -> Unit
) {
    val countDownTimer by remember {
        mutableStateOf(
            CountDownTimer(
                durationMillis = durationMillis,
                startMillis = startTimeMillis,
                onCompleted = onCompleted
            )
        )
    }

    val millis = (countDownTimer.durationMillis - countDownTimer.currentPosition).roundToLong()
    val days = TimeUnit.MILLISECONDS.toDays(millis)

    Box(
        modifier = modifier.defaultMinSize(70.dp, 70.dp),
        contentAlignment = Alignment.Center,
    ) {

        if (showText) {
            Column(
                modifier = modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (days > 0)
                    Text(
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(horizontal = 8.dp),
                        text = stringResource(id = R.string.countdown_timer_days, days),
                        color = MaterialTheme.colors.onBackground,
                        style = MaterialTheme.typography.overline
                    )
                Text(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(horizontal = 8.dp),
                    text = countDownTimer.getTime(millis),
                    color = MaterialTheme.colors.onBackground,
                    style = MaterialTheme.typography.overline
                )
            }
        }

        CircularProgressIndicator(
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier.matchParentSize(),
            progress = 1f,
            strokeWidth = 2.dp
        )

        val value by countDownTimer.progress
        CircularProgressIndicator(
            color = MaterialTheme.colors.primary,
            modifier = Modifier.matchParentSize(),
            progress = value,
            strokeWidth = 2.dp

        )
    }
}