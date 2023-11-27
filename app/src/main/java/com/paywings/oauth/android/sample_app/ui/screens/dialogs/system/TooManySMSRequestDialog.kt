package com.paywings.oauth.android.sample_app.ui.screens.dialogs.system

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
fun TooManySMSRequestDialog(
    dialogState: MaterialDialogState,
    onClose: () -> Unit
) {
    MaterialDialog(
        dialogState = dialogState,
        autoDismiss = false,
        shape = MaterialTheme.shapes.dialog,
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false, usePlatformDefaultWidth = false),
        buttons = {
            negativeButton(res = R.string.button_close, onClick = onClose )
        }
    ) {
        TooManySMSRequestDialogContent()
    }
}

@Composable
private fun TooManySMSRequestDialogContent(){
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(all = MaterialTheme.shapes.dialogEdgeDefaultPadding)) {
        ScreenTitle(
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
            stringResId = R.string.too_many_sms_request_dialog_title
        )
        SpacerDialogTitleBody()
        Text(
            style = MaterialTheme.typography.body2,
            text = stringResource(id = R.string.too_many_sms_request_dialog_description)
        )
    }
}