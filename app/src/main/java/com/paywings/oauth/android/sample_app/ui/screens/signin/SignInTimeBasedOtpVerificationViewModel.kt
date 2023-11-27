package com.paywings.oauth.android.sample_app.ui.screens.signin

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paywings.oauth.android.sample_app.R
import com.paywings.oauth.android.sample_app.network.NetworkState
import com.paywings.oauth.android.sample_app.ui.nav.RouteNavigator
import com.paywings.oauth.android.sample_app.ui.screens.dialogs.system.SystemDialogUiState
import com.paywings.oauth.android.sample_app.ui.screens.email_verification.EmailVerificationRequiredNav
import com.paywings.oauth.android.sample_app.ui.screens.main.MainNav
import com.paywings.oauth.android.sample_app.ui.screens.user_registration.UserRegistrationNav
import com.paywings.oauth.android.sample_app.util.asOneTimeEvent
import com.paywings.oauth.android.sdk.data.enums.OAuthErrorCode
import com.paywings.oauth.android.sdk.initializer.PayWingsOAuthClient
import com.paywings.oauth.android.sdk.service.callback.SignInWithPhoneTimeBasedOTPVerificationCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@HiltViewModel
class SignInTimeBasedOtpVerificationViewModel @Inject constructor(
    private val routeNavigator: RouteNavigator,
    private val networkState: NetworkState
): ViewModel(), RouteNavigator by routeNavigator {

    var uiState: SignInTimeBasedOtpVerificationUiState by mutableStateOf(value = SignInTimeBasedOtpVerificationUiState())

    fun setTimeBasedOtp(newTimeBasedOtp: String) {
        uiState = uiState.updateState(timeBasedOtp = newTimeBasedOtp)
    }

    fun verifyTimeBasedOtp() {
        uiState = uiState.updateState(isButtonVerifyTimeBasedOtpLoading = true)
        viewModelScope.launch {
            PayWingsOAuthClient.instance.signInWithPhoneTimeBasedOTPVerification(
                timeBasedOtp = uiState.timeBasedOtp,
                callback = signInWithPhoneTimeBasedOTPVerificationCallback
            )
        }
    }

    private val signInWithPhoneTimeBasedOTPVerificationCallback = object :
        SignInWithPhoneTimeBasedOTPVerificationCallback {
        override fun onError(error: OAuthErrorCode, errorMessage: String?) {
            uiState = when(error) {
                OAuthErrorCode.NO_INTERNET -> uiState.updateState(systemDialogUiState = SystemDialogUiState.ShowNoInternetConnection.asOneTimeEvent())
                OAuthErrorCode.USER_IS_SUSPENDED -> uiState.updateState(verifyTimeBasedOtpErrorMessage = R.string.sign_in_request_otp_screen_error_invalid_phone_number)
                else -> uiState.updateState(systemDialogUiState = SystemDialogUiState.ShowError(errorMessage = error.description).asOneTimeEvent())
            }
        }

        override fun onShowEmailConfirmationScreen(email: String, autoEmailSent: Boolean) {
            navigateToRoute(EmailVerificationRequiredNav.routeWithArguments(email = email, autoEmailSent = autoEmailSent))
        }

        override fun onShowRegistrationScreen() {
            navigateToRoute(UserRegistrationNav.route)
        }

        override fun onSignInSuccessful() {
            navigateToRoute(MainNav.route)
        }

        override fun onUserSignInRequired() {
            navigateToRoute(SignInRequestOtpNav.route)
        }

        override fun onVerificationFailed() {
            uiState = uiState.updateState(showInvalidTimeBasedOtp = true)
        }
    }

    fun recheckInternetConnection() {
        when (networkState.isConnected) {
            true -> verifyTimeBasedOtp()
            false -> uiState =uiState.updateState(systemDialogUiState = SystemDialogUiState.ShowNoInternetConnection.asOneTimeEvent())
        }
    }
}