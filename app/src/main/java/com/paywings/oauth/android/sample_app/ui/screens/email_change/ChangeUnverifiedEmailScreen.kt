package com.paywings.oauth.android.sample_app.ui.screens.email_change

import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.paywings.oauth.android.sample_app.R
import com.paywings.oauth.android.sample_app.ui.nav.NavRoute
import com.paywings.oauth.android.sample_app.ui.screens.components.ProcessingButton
import com.paywings.oauth.android.sample_app.ui.screens.components.ScreenTitleWithBackButtonIcon
import com.paywings.oauth.android.sample_app.ui.screens.components.ShowErrorText
import com.paywings.oauth.android.sample_app.ui.screens.dialogs.system.ErrorDialog
import com.paywings.oauth.android.sample_app.ui.screens.dialogs.system.NoInternetConnectionDialog
import com.paywings.oauth.android.sample_app.ui.screens.dialogs.system.SystemDialogUiState
import com.paywings.oauth.android.sample_app.ui.screens.email_verification.EmailVerificationRequiredNav
import com.paywings.oauth.android.sample_app.ui.screens.signin.SignInOtpVerificationNav
import com.paywings.oauth.android.sample_app.util.consume
import com.vanpra.composematerialdialogs.rememberMaterialDialogState


/**
 * Every screen has a route, so that we don't have to add the route setup of all screens in one file.
 *
 * Inherits NavRoute, to be able to navigate away from this screen. All navigation logic is in there.
 */
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
object ChangeUnverifiedEmailNav : NavRoute<ChangeUnverifiedEmailViewModel> {

    private const val EMAIL_DATA = "email"

    override val route = "change_unverified_email_screen"

    fun routeWithArguments(email: String): String {
        return "${route}/${Uri.encode(email)}"
    }

    @Composable
    override fun viewModel(): ChangeUnverifiedEmailViewModel = hiltViewModel()

    override fun getArguments(): List<NamedNavArgument> {
        return listOf(
            navArgument(EMAIL_DATA) {
                type = NavType.StringType
            }
        )
    }

    private fun parseEmail(arguments: Bundle?): String? {
        return arguments?.getString(EMAIL_DATA)
    }

    @Composable
    override fun Content(
        viewModel: ChangeUnverifiedEmailViewModel,
        arguments: Bundle?,
        onCloseApp: () -> Unit
    ) = ChangeUnverifiedEmailScreen(viewModel = viewModel, previousEmail = parseEmail(arguments), onCloseApp = onCloseApp)
}

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun ChangeUnverifiedEmailScreen(viewModel: ChangeUnverifiedEmailViewModel, previousEmail: String?, onCloseApp: () -> Unit) {

    LaunchedEffect(Unit) {
        viewModel.setPreviousEmail(email = previousEmail?:"")
    }

    val emailTextFieldFocusRequester = FocusRequester()
    var autoFocusEmailTextField by remember { mutableStateOf(true) }


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

    ChangeUnverifiedEmailContent(
        email = uiState.email,
        emailTextFieldFocusRequester = emailTextFieldFocusRequester,
        inputEnabled = !uiState.isLoading,
        isLoading = uiState.isLoading,
        emailErrorMessage = uiState.emailErrorMessage,
        errorMessage = uiState.errorMessage,
        onEmailChange = { email -> viewModel.setNewEmail(email = email) },
        onEmailChangeConfirm = { viewModel.changeEmailConfirm() },
        onBack = { viewModel.navigateToRoute(EmailVerificationRequiredNav.routeWithArguments(email = uiState.previousEmail, autoEmailSent = false)) },
    )

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

    BackHandler(enabled = true, onBack = { viewModel.navigateToRoute(EmailVerificationRequiredNav.routeWithArguments(email = uiState.previousEmail, autoEmailSent = false)) })

    SideEffect {
        if (autoFocusEmailTextField) {
            emailTextFieldFocusRequester.requestFocus()
            autoFocusEmailTextField = false
        }
    }
}

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun ChangeUnverifiedEmailContent(
    email: String,
    emailTextFieldFocusRequester: FocusRequester,
    inputEnabled: Boolean,
    isLoading: Boolean,
    @StringRes emailErrorMessage: Int?,
    @StringRes errorMessage: Int?,
    onEmailChange: (email: String) ->  Unit,
    onEmailChangeConfirm: () -> Unit,
    onBack: () -> Unit,
    ) {

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
            ScreenTitleWithBackButtonIcon(
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                title = stringResource(id = R.string.change_unverified_email_screen_title),
                onClose = {
                    onBack()
                }
            )
            Spacer(Modifier.height(24.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .focusRequester(emailTextFieldFocusRequester),
                value = email,
                onValueChange = {
                    onEmailChange(it)
                },
                label = { Text(stringResource(id = R.string.change_unverified_email_screen_email)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    autoCorrect = false
                ),
                singleLine = true,
                enabled = inputEnabled,
                isError = emailErrorMessage != null,
                trailingIcon = {
                    AnimatedVisibility(
                        visible = email.isNotEmpty(),
                        enter = fadeIn(
                            // Overwrites the initial value of alpha to 0.4f for fade in, 0 by default
                            initialAlpha = 0.4f
                        ),
                        exit = fadeOut(
                            // Overwrites the default animation with tween
                            animationSpec = tween(durationMillis = 250)
                        )
                    ) {
                        IconButton(
                            onClick = {
                                onEmailChange("")
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Cancel,
                                contentDescription = null
                            )
                        }
                    }
                }
            )
            Text(
                text = emailErrorMessage.takeIf { it != null }?.let { stringResource(id = it) }
                    ?: "",
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption
            )
            Spacer(Modifier.height(16.dp))
            ProcessingButton(
                textResId = R.string.button_confirm_change,
                isLoading = isLoading,
                isEnabled = email.isNotBlank() && !isLoading,
                onClick = onEmailChangeConfirm
            )
            ShowErrorText(errorResId = errorMessage)
            Spacer(Modifier.height(8.dp))
        }
    }
}