package com.paywings.oauth.android.sample_app.ui.screens.main

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.paywings.oauth.android.sample_app.R
import com.paywings.oauth.android.sample_app.ui.nav.NavRoute
import com.paywings.oauth.android.sample_app.ui.screens.components.ScreenTitle
import com.paywings.oauth.android.sample_app.ui.screens.dialogs.system.ErrorDialog
import com.paywings.oauth.android.sample_app.ui.screens.dialogs.system.NoInternetConnectionDialog
import com.paywings.oauth.android.sample_app.ui.screens.dialogs.system.SystemDialogUiState
import com.paywings.oauth.android.sample_app.ui.theme.contentEdgePadding
import com.paywings.oauth.android.sample_app.util.consume
import com.vanpra.composematerialdialogs.rememberMaterialDialogState


/**
 * Every screen has a route, so that we don't have to add the route setup of all screens in one file.
 *
 * Inherits NavRoute, to be able to navigate away from this screen. All navigation logic is in there.
 */
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
object MainNav : NavRoute<MainViewModel> {

    override val route = "main_screen"

    @Composable
    override fun viewModel(): MainViewModel = hiltViewModel()

    @Composable
    override fun Content(
        viewModel: MainViewModel,
        arguments: Bundle?,
        onCloseApp: () -> Unit
    ) = MainScreen(viewModel = viewModel, onCloseApp = onCloseApp)
}

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun MainScreen(viewModel: MainViewModel, onCloseApp: () -> Unit) {

    val noInternetConnectionDialogState = rememberMaterialDialogState(initialValue = false)
    val errorDialogState = rememberMaterialDialogState(initialValue = false)
    var errorMessage: String by remember { mutableStateOf("") }

    val uiState = viewModel.uiState

    uiState.systemDialogUiState?.consume {
        when (it) {
            is SystemDialogUiState.ShowTooManySMSRequest -> Unit
            is SystemDialogUiState.ShowNoInternetConnection -> noInternetConnectionDialogState.show()
            is SystemDialogUiState.ShowError -> {
                errorMessage = it.errorMessage
                errorDialogState.show()
            }
        }
    }

    MainContent(
        userId = uiState.userId,
        firstName = uiState.firstName,
        lastName = uiState.lastName,
        emailAddress = uiState.email,
        phoneNumber = uiState.phoneNumber,
        accessTokenExpirationDateTime = uiState.accessTokenExpirationDateTime,
        onGetValidAccessToken = { viewModel.getNewAuthorizationData() },
        onSignOut = { viewModel.signOutUser() }
    )

    NoInternetConnectionDialog(
        dialogState = noInternetConnectionDialogState,
        cancelButtonNameResId = R.string.button_exit,
        onRecheckInternetConnection = {
            noInternetConnectionDialogState.takeIf { it.showing }?.hide()
        },
        onCancel = {
            noInternetConnectionDialogState.takeIf { it.showing }?.hide()
            onCloseApp()
        }
    )

    ErrorDialog(
        dialogState = errorDialogState,
        detailedMessage = errorMessage,
        onCancel = {
            errorDialogState.takeIf { it.showing }?.hide()
            onCloseApp()
        }
    )

    LaunchedEffect(Unit) {
        viewModel.initialization()
    }

    BackHandler(enabled = true, onBack = { onCloseApp() })

}

@Composable
fun MainContent(
    userId: String,
    firstName: String,
    lastName: String,
    emailAddress: String,
    phoneNumber: String,
    accessTokenExpirationDateTime: String,
    onGetValidAccessToken: () -> Unit,
    onSignOut: () -> Unit
) {

    Column(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(horizontal = MaterialTheme.shapes.contentEdgePadding),
    ) {
        ScreenTitle(
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
            title = stringResource(id = R.string.main_screen_title)
        )
        Spacer(Modifier.height(24.dp))
        Text(text = stringResource(id = R.string.main_screen_user_id, userId))
        Spacer(Modifier.height(8.dp))
        Text(text = stringResource(id = R.string.main_screen_first_name, firstName))
        Spacer(Modifier.height(8.dp))
        Text(text = stringResource(id = R.string.main_screen_last_name, lastName))
        Spacer(Modifier.height(8.dp))
        Text(text = stringResource(id = R.string.main_screen_email, emailAddress))
        Spacer(Modifier.height(8.dp))
        Text(text = stringResource(id = R.string.main_screen_phone_number, phoneNumber))
        Spacer(Modifier.height(16.dp))
        Text(text = stringResource(id = R.string.main_screen_access_token_expiration_time, accessTokenExpirationDateTime))
        Button(onClick = onGetValidAccessToken) {
            Text(text = stringResource(id = R.string.button_get_valid_access_token))
        }

        Spacer(Modifier.height(24.dp))
        Button(modifier = Modifier.align(Alignment.End), onClick = { onSignOut() }) {
            Text(text = stringResource(id = R.string.button_sign_out))
        }
    }
}