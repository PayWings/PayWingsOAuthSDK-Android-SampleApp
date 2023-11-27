package com.paywings.oauth.android.sample_app.ui.screens.email_verification

import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.paywings.oauth.android.sample_app.R
import com.paywings.oauth.android.sample_app.ui.nav.NavRoute
import com.paywings.oauth.android.sample_app.ui.screens.components.ProcessingButton
import com.paywings.oauth.android.sample_app.ui.screens.components.ScreenTitle
import com.paywings.oauth.android.sample_app.ui.screens.dialogs.system.ErrorDialog
import com.paywings.oauth.android.sample_app.ui.screens.dialogs.system.NoInternetConnectionDialog
import com.paywings.oauth.android.sample_app.ui.screens.dialogs.system.SystemDialogUiState
import com.paywings.oauth.android.sample_app.ui.screens.email_change.ChangeUnverifiedEmailNav
import com.paywings.oauth.android.sample_app.ui.theme.contentEdgePadding
import com.paywings.oauth.android.sample_app.util.Constants.DoNothing
import com.paywings.oauth.android.sample_app.util.consume
import com.vanpra.composematerialdialogs.rememberMaterialDialogState

/**
 * Every screen has a route, so that we don't have to add the route setup of all screens in one file.
 *
 * Inherits NavRoute, to be able to navigate away from this screen. All navigation logic is in there.
 */
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
object EmailVerificationRequiredNav : NavRoute<EmailVerificationRequiredViewModel> {

    private const val EMAIL_DATA = "email"
    private const val AUTO_EMAIL_SENT = "autoEmailSent"

    override val route = "email_verification_required_screen"

    fun routeWithArguments(email: String, autoEmailSent: Boolean): String {
        return "${route}/${Uri.encode(email)}/${autoEmailSent}"
    }

    @Composable
    override fun viewModel(): EmailVerificationRequiredViewModel = hiltViewModel()


    override fun getArguments(): List<NamedNavArgument> {
        return listOf(
            navArgument(EMAIL_DATA) {
                type = NavType.StringType
            },
            navArgument(AUTO_EMAIL_SENT) {
                type = NavType.BoolType
            },
        )
    }

    private fun parseEmail(arguments: Bundle?): String? {
        return arguments?.getString(EMAIL_DATA)
    }

    private fun parseAutoEmailSent(arguments: Bundle?): Boolean {
        return arguments?.getBoolean(AUTO_EMAIL_SENT)?:false
    }

    @Composable
    override fun Content(
        viewModel: EmailVerificationRequiredViewModel,
        arguments: Bundle?,
        onCloseApp: () -> Unit
    ) = EmailVerificationRequiredScreen(viewModel = viewModel, email = parseEmail(arguments), autoEmailSent = parseAutoEmailSent(arguments), onCloseApp = onCloseApp)
}

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun EmailVerificationRequiredScreen(viewModel: EmailVerificationRequiredViewModel, email: String?, autoEmailSent: Boolean, onCloseApp: () -> Unit) {

    LaunchedEffect(Unit) {
        viewModel.setEmail(email = email?:"", autoEmailSent = autoEmailSent)
    }

    val uiState = viewModel.uiState

    val noInternetConnectionDialogState = rememberMaterialDialogState(initialValue = false)
    val errorDialogState = rememberMaterialDialogState(initialValue = false)
    var errorMessage: String by remember { mutableStateOf("") }

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

    when(uiState.emailVerificationRunning) {
        true -> EmailVerificationInProgressContent(
            email = uiState.email,
            onCancel = { viewModel.cancelEmailVerification() }
        )
        false -> EmailVerificationRequiredContent(
            email = uiState.email,
            isLoading = uiState.isLoading,
            onVerifyEmail = { viewModel.sendNewVerificationEmail() },
            onChangeEmail = { viewModel.navigateToRoute(ChangeUnverifiedEmailNav.routeWithArguments(uiState.email)) })
    }

    NoInternetConnectionDialog(
        dialogState = noInternetConnectionDialogState,
        cancelButtonNameResId = R.string.button_exit,
        onRecheckInternetConnection = {
            noInternetConnectionDialogState.takeIf { it.showing }?.hide()
            viewModel.recheckInternetConnection()
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

    BackHandler(enabled = true, onBack = { onCloseApp() })
}

@Composable
fun EmailVerificationRequiredContent(
    email: String,
    isLoading: Boolean,
    onVerifyEmail: () -> Unit,
    onChangeEmail: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp, 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(alignment = Alignment.TopCenter)
        ) {
            ScreenTitle(
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                title = stringResource(id = R.string.email_verification_required_screen_title)
            )
            Spacer(Modifier.height(24.dp))
            Text(text = stringResource(id = R.string.email_verification_required_screen_verification_required_subtitle))
            Spacer(Modifier.height(24.dp))
            Text(
                text = stringResource(
                    id = R.string.email_verification_required_screen_verification_required_description,
                )
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                value = email,
                onValueChange = { DoNothing },
                label = { Text(stringResource(id = R.string.email_verification_required_screen_verification_required_email)) },
                singleLine = true,
                enabled = false,
            )
            Spacer(Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Box(modifier = Modifier.fillMaxWidth(0.5f)) {
                    ProcessingButton(
                        modifier = Modifier.wrapContentWidth(),
                        textResId = R.string.button_change_email,
                        isLoading = false,
                        onClick = onChangeEmail
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                ProcessingButton(
                    modifier = Modifier.wrapContentWidth(),
                    textResId = R.string.button_verify_email,
                    isLoading = isLoading,
                    onClick = onVerifyEmail
                )
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}


@Composable
fun EmailVerificationInProgressContent(
    email: String,
    onCancel: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(horizontal = MaterialTheme.shapes.contentEdgePadding),
    ) {
        ScreenTitle(
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
            title = stringResource(id = R.string.email_verification_required_screen_title)
        )
        Spacer(Modifier.height(24.dp))
        CircularProgressIndicator(
            strokeWidth = 2.dp,
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
        )
        Text(modifier = Modifier.align(alignment = Alignment.CenterHorizontally), text = stringResource(id = R.string.email_verification_required_screen_verification_in_progress_spinner))
        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(
                id = R.string.email_verification_required_screen_verification_in_progress_description,
                email
            )
        )
        Spacer(Modifier.height(24.dp))
        Button(modifier = Modifier.fillMaxWidth(), onClick = { onCancel() }) {
            Text(text = stringResource(id = R.string.button_cancel))
        }
    }
}
