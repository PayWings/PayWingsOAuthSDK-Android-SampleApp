package com.paywings.oauth.android.sample_app.ui.screens.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.paywings.oauth.android.sample_app.ui.theme.*


@Composable
fun ShowErrorText(modifier: Modifier = Modifier, @StringRes errorResId: Int?) {
    errorResId?.let {
        Text(
            modifier = modifier.padding(start = 4.dp, end = 4.dp),
            text = stringResource(id = it),
            color = MaterialTheme.colors.error,
            style = MaterialTheme.typography.caption
        )
    }
}

@Composable
fun ScreenTitle(modifier: Modifier = Modifier, @StringRes stringResId: Int) {
    ScreenTitle(title = stringResource(stringResId), modifier = modifier)
}

@Composable
fun ScreenTitle(modifier: Modifier = Modifier, title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.h5,
        modifier = modifier
    )
}

@Composable
fun ScreenTitleWithBackButtonIcon(
    modifier: Modifier = Modifier,
    title: String,
    onClose: () -> Unit,
    enabled: Boolean = true
) {
    Box(
        modifier = Modifier
            .wrapContentHeight()
            .height(IntrinsicSize.Max)
    ) {
        IconButton(
            modifier = modifier
                .then(Modifier.size(24.dp))
                .wrapContentSize(Alignment.Center)
                .align(
                    Alignment.TopStart
                ),
            onClick = onClose,
            enabled = enabled
        ) {
            Icon(
                modifier = modifier,
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = Icons.Filled.ArrowBack.name,
                tint = MaterialTheme.colors.primary
            )
        }
        Column(
            modifier = modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h6,
                color = MaterialTheme.colors.primary,
                modifier = modifier.padding(start = 28.dp, end = 28.dp)
            )
        }
    }
}

@Composable
fun SpacerDialogTitleBody() {
    Spacer(Modifier.height(MaterialTheme.shapes.dialogTitleBodyDefaultPadding))
}

@Composable
fun ProcessingButton(
    modifier: Modifier = Modifier,
    @StringRes textResId: Int,
    onClick: () -> Unit,
    isLoading: Boolean = false,
    isEnabled: Boolean = true
) {
    Button(
        modifier = modifier.fillMaxWidth(),
        enabled = isEnabled,
        onClick = { if (!isLoading) onClick() }) {
        when (isLoading) {
            true -> CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colors.onPrimary,
                strokeWidth = 2.dp
            )
            false -> Text(text = stringResource(textResId))
        }
    }
}

@Composable
fun ScreenTitleWithCloseButtonIcon(
    modifier: Modifier = Modifier,
    @StringRes titleResId: Int,
    onClose: () -> Unit,
    enabled: Boolean = true
) {
    Box(
        modifier = Modifier
            .wrapContentHeight()
            .height(IntrinsicSize.Max)
    ) {
        IconButton(
            modifier = modifier
                .then(Modifier.size(24.dp))
                .wrapContentSize(Alignment.Center)
                .align(
                    Alignment.TopStart
                ),
            onClick = onClose,
            enabled = enabled
        ) {
            Icon(
                modifier = modifier,
                imageVector = Icons.Filled.Close,
                contentDescription = Icons.Filled.Close.name
            )
        }
        Column(
            modifier = modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(titleResId),
                style = MaterialTheme.typography.h6,
                modifier = modifier.padding(start = 28.dp, end = 28.dp)
            )
        }
    }
}
