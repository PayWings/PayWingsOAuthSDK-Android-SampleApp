package com.paywings.oauth.android.sample_app.ui.screens.signin

import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
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
import com.paywings.oauth.android.sample_app.util.consume
import com.vanpra.composematerialdialogs.rememberMaterialDialogState


/**
 * Every screen has a route, so that we don't have to add the route setup of all screens in one file.
 *
 * Inherits NavRoute, to be able to navigate away from this screen. All navigation logic is in there.
 */
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
object SignInTimeBasedOtpVerificationNav : NavRoute<SignInTimeBasedOtpVerificationViewModel> {

    private const val ACCOUNT_NAME = "accountName"

    override val route = "sign_in_time_based_otp_verification_screen"

    fun routeWithArguments(accountName: String): String {
        return "$route/${Uri.encode(accountName)}"
    }

    @Composable
    override fun viewModel(): SignInTimeBasedOtpVerificationViewModel = hiltViewModel()

    override fun getArguments(): List<NamedNavArgument> {
        return listOf(
            navArgument(ACCOUNT_NAME) {
                type = NavType.StringType
            })
    }

    private fun parseAccountName(arguments: Bundle?): String? {
        return arguments?.getString(ACCOUNT_NAME)
    }

    @Composable
    override fun Content(
        viewModel: SignInTimeBasedOtpVerificationViewModel,
        arguments: Bundle?,
        onCloseApp: () -> Unit
    ) = SignInTimeBasedOtpVerificationScreen(viewModel = viewModel, accountName = parseAccountName(arguments), onCloseApp = onCloseApp)
}

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun SignInTimeBasedOtpVerificationScreen(viewModel: SignInTimeBasedOtpVerificationViewModel, accountName: String?, onCloseApp: () -> Unit) {


    val otpTextFieldFocusRequester = FocusRequester()
    var autoFocusOtpTextField by remember { mutableStateOf(true) }

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

    SignInTimeBasedOtpVerificationContent(
        accountName = accountName?:"",
        timeBasedOtpLength = uiState.timeBasedOtpLength,
        timeBasedOtp = uiState.timeBasedOtp,
        otpTextFieldFocusRequester = otpTextFieldFocusRequester,
        buttonConfirmIsEnabled = uiState.timeBasedOtp.length == uiState.timeBasedOtpLength && !uiState.isButtonVerifyTimeBasedOtpLoading,
        buttonConfirmIsLoading = uiState.isButtonVerifyTimeBasedOtpLoading,
        inputEnabled = !uiState.isButtonVerifyTimeBasedOtpLoading,
        confirmErrorResId = uiState.verifyTimeBasedOtpErrorMessage,
        showInvalidOtp = uiState.showInvalidTimeBasedOtp,
        onBack = { viewModel.navigateToRoute(SignInRequestOtpNav.route) },
        onOtpChange = { viewModel.setTimeBasedOtp(it) },
        onConfirmClick = { viewModel.verifyTimeBasedOtp() },
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

    BackHandler(enabled = true, onBack = { viewModel.navigateToRoute(SignInRequestOtpNav.route) })

    SideEffect {
        if (autoFocusOtpTextField) {
            otpTextFieldFocusRequester.requestFocus()
            autoFocusOtpTextField = false
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun SignInTimeBasedOtpVerificationContent(
    accountName: String,
    timeBasedOtpLength: Int,
    timeBasedOtp: String,
    otpTextFieldFocusRequester: FocusRequester,
    buttonConfirmIsLoading: Boolean,
    buttonConfirmIsEnabled: Boolean,
    inputEnabled: Boolean,
    showInvalidOtp: Boolean,
    @StringRes confirmErrorResId: Int?,
    onBack: () -> Unit,
    onConfirmClick: () -> Unit,
    onOtpChange: (newTimeBasedOtp: String) -> Unit
) {
    var cancelIconVisible by remember { mutableStateOf(false) }

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
                title = stringResource(
                    id = R.string.sign_in_time_based_otp_verification_screen_title, accountName
                ),
                onClose = {
                    onBack()
                }
            )
            Spacer(Modifier.height(24.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .focusRequester(otpTextFieldFocusRequester),
                value = timeBasedOtp,
                onValueChange = {
                    if (it.isDigitsOnly() && (timeBasedOtpLength == 0 || it.length <= timeBasedOtpLength)) {
                        onOtpChange(it)
                    }
                    cancelIconVisible = it.isNotEmpty()
                },
                label = { Text(stringResource(id = R.string.sign_in_phone_number_otp_verification_screen_label)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    autoCorrect = false
                ),
                maxLines = 1,
                singleLine = true,
                enabled = inputEnabled,
                isError = showInvalidOtp,
                trailingIcon = {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = cancelIconVisible,
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
                                onOtpChange("")
                                cancelIconVisible = false
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
                text = if (showInvalidOtp) stringResource(id = R.string.sign_in_phone_number_otp_verification_screen_incorrect_confirmation_code_error)  else  "",
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption
            )
            Spacer(Modifier.height(16.dp))
            ProcessingButton(
                textResId = R.string.button_confirm_code,
                isLoading = buttonConfirmIsLoading,
                isEnabled = buttonConfirmIsEnabled,
                onClick = onConfirmClick
            )
            ShowErrorText(errorResId = confirmErrorResId)
            Spacer(Modifier.height(24.dp))
        }
    }
}
