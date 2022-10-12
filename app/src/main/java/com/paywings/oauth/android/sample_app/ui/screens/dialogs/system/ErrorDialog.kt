package com.paywings.oauth.android.sample_app.ui.screens.dialogs.system

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.paywings.oauth.android.sample_app.R
import com.paywings.oauth.android.sample_app.ui.screens.components.ScreenTitle
import com.paywings.oauth.android.sample_app.ui.screens.components.SpacerDialogTitleBody
import com.paywings.oauth.android.sample_app.ui.theme.dialog
import com.paywings.oauth.android.sample_app.ui.theme.dialogEdgeDefaultPadding
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState

@ExperimentalComposeUiApi
@Composable
fun ErrorDialog(
    dialogState: MaterialDialogState,
    detailedMessage: String,
    onCancel: () -> Unit
) {
    var showDetails by remember { mutableStateOf(false) }
    MaterialDialog(
        dialogState = dialogState,
        autoDismiss = false,
        shape = MaterialTheme.shapes.dialog,
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false, usePlatformDefaultWidth = false),
        buttons = {
            negativeButton(res = R.string.button_close, onClick = { onCancel() })
        }
    ) {
        ErrorDialogContent(
            showDetails = showDetails,
            detailedMessage = detailedMessage,
            onShowDetailsChange = { show -> showDetails = show })
    }
}

@Composable
private fun ErrorDialogContent(
    showDetails: Boolean,
    detailedMessage: String,
    onShowDetailsChange: (show : Boolean) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(all = MaterialTheme.shapes.dialogEdgeDefaultPadding)) {
        ScreenTitle(
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
            stringResId = R.string.error_dialog_title
        )
        SpacerDialogTitleBody()
        Text(
            style = MaterialTheme.typography.body1,
            text = stringResource(id = R.string.error_dialog_description)
        )
        if (detailedMessage.isNotBlank()) {
            Spacer(Modifier.height(16.dp))
            TextButton(
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                onClick = { onShowDetailsChange(!showDetails) }) {
                Text(text = stringResource(id = when (showDetails) {
                    true -> R.string.button_hide_details
                    false -> R.string.button_show_details
                }).uppercase())
            }
            if (showDetails) {
                Text(
                    style = MaterialTheme.typography.body2,
                    text = detailedMessage
                )
            }
        }
    }
}