package com.paywings.oauth.android.sample_app.ui.screens.signin

import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
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
object SignInOtpVerificationNav : NavRoute<SignInOtpVerificationViewModel> {

    private const val OTP_LENGTH_DATA = "otpLength"
    private const val PHONE_NUMBER_OAUTH_DATA = "phoneNumberOAuth"
    private const val PHONE_NUMBER_FORMATTED_DATA = "phoneNumberFormatted"


    override val route = "sign_in_otp_verification_screen"

    fun routeWithArguments(otpLength: Int, phoneNumberOAuth: String, phoneNumberFormatted: String): String {
        return "$route/${otpLength}/${Uri.encode(phoneNumberOAuth)}/${Uri.encode(phoneNumberFormatted)}"
    }

    @Composable
    override fun viewModel(): SignInOtpVerificationViewModel = hiltViewModel()

    override fun getArguments(): List<NamedNavArgument> {
        return listOf(
            navArgument(OTP_LENGTH_DATA) {
                type = NavType.IntType
            },
            navArgument(PHONE_NUMBER_OAUTH_DATA) {
                type = NavType.StringType
            },
            navArgument(PHONE_NUMBER_FORMATTED_DATA) {
                type = NavType.StringType
            })
    }

    private fun parseOtpLength(arguments: Bundle?): Int {
        return arguments?.getInt(OTP_LENGTH_DATA)?:0
    }

    private fun parsePhoneNumberOAuth(arguments: Bundle?): String? {
        return arguments?.getString(PHONE_NUMBER_OAUTH_DATA)
    }

    private fun parsePhoneNumberFormatted(arguments: Bundle?): String? {
        return arguments?.getString(PHONE_NUMBER_FORMATTED_DATA)
    }

    @Composable
    override fun Content(
        viewModel: SignInOtpVerificationViewModel,
        arguments: Bundle?,
        onCloseApp: () -> Unit
    ) = SignInOtpVerificationScreen(viewModel = viewModel, otpLength = parseOtpLength(arguments), phoneNumberOAuth = parsePhoneNumberOAuth(arguments), phoneNumberFormatted = parsePhoneNumberFormatted(arguments), onCloseApp = onCloseApp)
}



@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun SignInOtpVerificationScreen(viewModel: SignInOtpVerificationViewModel, otpLength: Int, phoneNumberOAuth: String?, phoneNumberFormatted: String?, onCloseApp: () -> Unit) {

    LaunchedEffect(Unit) {
        viewModel.setVerificationData(otpLength = otpLength, phoneNumberOAuth = phoneNumberOAuth, phoneNumberFormatted = phoneNumberFormatted)
    }

    val otpTextFieldFocusRequester = FocusRequester()
    var autoFocusOtpTextField by remember { mutableStateOf(true) }

    val noInternetConnectionDialogState = rememberMaterialDialogState(initialValue = false)
    val errorDialogState = rememberMaterialDialogState(initialValue = false)
    var errorMessage: String by remember { mutableStateOf("") }

    val uiState = viewModel.uiState

    uiState.systemDialogUiState?.consume {
        when (it) {
            is SystemDialogUiState.ShowNoInternetConnection -> noInternetConnectionDialogState.show()
            is SystemDialogUiState.ShowError -> {
                errorMessage = it.errorMessage
                errorDialogState.show()
            }
        }
    }

    SignInPhoneNumberOtpVerificationContent(
        otpLength = uiState.otpLength,
        otpFormattedPhoneNumber = uiState.otpPhoneNumberFormatted,
        otp = uiState.otp,
        otpTextFieldFocusRequester = otpTextFieldFocusRequester,
        buttonConfirmIsEnabled = uiState.otp.length == uiState.otpLength && !uiState.isButtonVerifyOtpLoading && !uiState.isButtonRequestNewOtpLoading,
        buttonResendIsEnabled = viewModel.countDownTimerTime == 0 && !uiState.isButtonVerifyOtpLoading && !uiState.isButtonRequestNewOtpLoading,
        buttonConfirmIsLoading = uiState.isButtonVerifyOtpLoading,
        buttonResendIsLoading = uiState.isButtonRequestNewOtpLoading,
        inputEnabled = !uiState.isButtonVerifyOtpLoading,
        countDownTime = viewModel.countDownTimerTime,
        confirmErrorResId = uiState.verifyOtpErrorMessage,
        resendErrorResId = uiState.requestNewOtpErrorMessage,
        showInvalidOtp = uiState.showInvalidOtp,
        onBack = { viewModel.navigateToRoute(SignInRequestOtpNav.route) },
        onOtpChange = { viewModel.setOtp(it) },
        onConfirmClick = { viewModel.verifyOtp() },
        onResendClick = { viewModel.requestNewOtp() }
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
fun SignInPhoneNumberOtpVerificationContent(
    otpLength: Int,
    otpFormattedPhoneNumber: String,
    otp: String,
    otpTextFieldFocusRequester: FocusRequester,
    buttonConfirmIsLoading: Boolean,
    buttonResendIsLoading: Boolean,
    buttonConfirmIsEnabled: Boolean,
    buttonResendIsEnabled: Boolean,
    inputEnabled: Boolean,
    countDownTime: Int,
    showInvalidOtp: Boolean,
    @StringRes confirmErrorResId: Int?,
    @StringRes resendErrorResId: Int?,
    onBack: () -> Unit,
    onConfirmClick: () -> Unit,
    onOtpChange: (newOtp: String) -> Unit,
    onResendClick: () -> Unit
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
                    id = R.string.sign_in_phone_number_otp_verification_screen_title,
                    (otpLength.takeIf { it > 0 } ?: "")
                ),
                onClose = {
                    onBack()
                }
            )
            Text(
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                style = MaterialTheme.typography.body1,
                text = otpFormattedPhoneNumber
            )
            Spacer(Modifier.height(24.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .focusRequester(otpTextFieldFocusRequester),
                value = otp,
                onValueChange = {
                    if (it.isDigitsOnly() && (otpLength == 0 || it.length <= otpLength)) {
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
            ButtonResendCode(
                textResId = R.string.button_resend_code,
                isEnabled = buttonResendIsEnabled,
                isLoading = buttonResendIsLoading,
                countDownTime = countDownTime,
                onButtonClick = onResendClick,
                Modifier.align(alignment = Alignment.CenterHorizontally)
            )
            ShowErrorText(errorResId = resendErrorResId)
        }
    }
}

@Composable
fun ButtonResendCode(
    @StringRes textResId: Int,
    isEnabled: Boolean,
    isLoading: Boolean,
    countDownTime: Int,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        modifier = modifier.padding(start = 24.dp),
        onClick = { if (!isLoading) onButtonClick() },
        enabled = isEnabled && countDownTime == 0
    ) {
        Column {
            when (countDownTime > 0) {
                true -> {
                    Box(modifier = Modifier.align(alignment = Alignment.End)) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(24.dp)
                                .align(alignment = Alignment.Center),
                            strokeWidth = 1.dp
                        )
                        Text(
                            text = countDownTime.toString(),
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.primary,
                            modifier = Modifier.align(alignment = Alignment.Center)
                        )
                    }
                }
                false -> {
                    Spacer(Modifier.height(24.dp))
                }
            }
            when (isLoading) {
                true -> CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colors.primary,
                    strokeWidth = 2.dp
                )
                false -> Text(
                    text = stringResource(id = textResId),
                    modifier = Modifier.padding(end = 24.dp)
                )
            }
        }
    }
}