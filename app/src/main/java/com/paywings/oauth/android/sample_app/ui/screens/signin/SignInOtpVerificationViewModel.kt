package com.paywings.oauth.android.sample_app.ui.screens.signin

import android.os.CountDownTimer
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paywings.oauth.android.sample_app.R
import com.paywings.oauth.android.sample_app.data.remote.NetworkConstants
import com.paywings.oauth.android.sample_app.network.NetworkState
import com.paywings.oauth.android.sample_app.ui.nav.RouteNavigator
import com.paywings.oauth.android.sample_app.ui.nav.graph.OAUTH_ROUTE
import com.paywings.oauth.android.sample_app.ui.screens.dialogs.system.SystemDialogUiState
import com.paywings.oauth.android.sample_app.ui.screens.email_verification.EmailVerificationRequiredNav
import com.paywings.oauth.android.sample_app.ui.screens.main.MainNav
import com.paywings.oauth.android.sample_app.ui.screens.user_registration.UserRegistrationNav
import com.paywings.oauth.android.sample_app.util.Constants.DoNothing
import com.paywings.oauth.android.sample_app.util.asOneTimeEvent
import com.paywings.oauth.android.sdk.data.enums.OAuthErrorCode
import com.paywings.oauth.android.sdk.initializer.PayWingsOAuthClient
import com.paywings.oauth.android.sdk.service.callback.SignInWithPhoneNumberRequestOtpCallback
import com.paywings.oauth.android.sdk.service.callback.SignInWithPhoneNumberVerifyOtpCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@HiltViewModel
class SignInOtpVerificationViewModel @Inject constructor(
    private val routeNavigator: RouteNavigator,
    private val networkState: NetworkState,
): ViewModel(), RouteNavigator by routeNavigator {

    var uiState: SignInOtpVerificationUiState by mutableStateOf(value = SignInOtpVerificationUiState())
    var countDownTimerTime: Int by mutableStateOf(value = 0)

    private var phoneNumberCountryCode: String = ""
    private var phoneNumber: String = ""
    private var isVerifyOtpLastAction: Boolean? = null

    private val countDownTimer = object: CountDownTimer(
        NetworkConstants.COUNTDOWN_TIMER_FOR_RESEND_OTP_MAX_TIME_IN_SECONDS * 1000L,
        1000
    ) {
        override fun onTick(millisUntilFinished: Long) {
            countDownTimerTime = millisUntilFinished.toInt() / 1000
        }

        override fun onFinish() {
            countDownTimerTime = 0
        }
    }

    private fun startOtpResendTimer() {
        countDownTimer.start()
    }

    init {
        startOtpResendTimer()
    }

    fun setVerificationData(otpLength: Int, phoneNumberCountryCode: String?, phoneNumber: String?, phoneNumberFormatted: String?) {
        if (otpLength > 0 && !phoneNumberCountryCode.isNullOrBlank() && !phoneNumber.isNullOrBlank() && !phoneNumberFormatted.isNullOrBlank()) {
            this.phoneNumberCountryCode = phoneNumberCountryCode
            this.phoneNumber = phoneNumber
            uiState = uiState.updateState(otpLength = otpLength, otpPhoneNumberFormatted = phoneNumberFormatted)
        } else {
            navigateToRoute(OAUTH_ROUTE)
        }
    }

    fun setOtp(newOtp: String) {
        uiState = uiState.updateState(otp = newOtp)
    }

    fun verifyOtp() {
        isVerifyOtpLastAction = true
        uiState = uiState.updateState(isButtonVerifyOtpLoading = true)
        viewModelScope.launch {
            PayWingsOAuthClient.instance.signInWithPhoneNumberVerifyOtp(
                otp = uiState.otp,
                callback = signInWithPhoneNumberVerifyOtpCallback
            )
        }
    }

    private val signInWithPhoneNumberVerifyOtpCallback = object : SignInWithPhoneNumberVerifyOtpCallback {
        override fun onError(error: OAuthErrorCode, errorMessage: String?) {
            uiState = when(error) {
                OAuthErrorCode.INTERNET_CONNECTION_ISSUE -> uiState.updateState(systemDialogUiState = SystemDialogUiState.ShowNoInternetConnection.asOneTimeEvent())
                OAuthErrorCode.USER_IS_SUSPENDED -> uiState.updateState(verifyOtpErrorMessage = R.string.sign_in_request_otp_screen_error_invalid_phone_number)
                else -> uiState.updateState(systemDialogUiState = SystemDialogUiState.ShowError(errorMessage = error.description).asOneTimeEvent())
            }
        }

        override fun onShowEmailConfirmationScreen(email: String, autoEmailSent: Boolean) {
             navigateToRoute(EmailVerificationRequiredNav.routeWithArguments(email = email, autoEmailSent = autoEmailSent))
        }

        override fun onShowRegistrationScreen() {
            navigateToRoute(UserRegistrationNav.route)
        }

        override fun onShowTimeBasedOtpSetupScreen(accountName: String, secretKey: String) {
            navigateToRoute(SignInTimeBasedOtpSetupScreenNav.routeWithArguments(accountName = accountName, secretKey = secretKey))
        }

        override fun onShowTimeBasedOtpVerificationInputScreen(accountName: String) {
            navigateToRoute(SignInTimeBasedOtpVerificationNav.routeWithArguments(accountName))
        }

        override fun onSignInSuccessful() {
            navigateToRoute(MainNav.route)
        }

        override fun onUserSignInRequired() {
            requestNewOtp()
        }

        override fun onVerificationFailed() {
            uiState = uiState.updateState(showInvalidOtp = true)
        }
    }

    fun requestNewOtp() {
        isVerifyOtpLastAction = false
        uiState = uiState.updateState(isButtonRequestNewOtpLoading = true)
        viewModelScope.launch {
            PayWingsOAuthClient.instance.signInWithPhoneNumberRequestOtp(
                phoneNumberCountryCode = phoneNumberCountryCode,
                phoneNumber = phoneNumber,
                callback = signInWithPhoneNumberRequestOtp
            )
        }
    }

    private val signInWithPhoneNumberRequestOtp = object: SignInWithPhoneNumberRequestOtpCallback {
        override fun onError(error: OAuthErrorCode, errorMessage: String?) {
            uiState = when(error) {
                OAuthErrorCode.INTERNET_CONNECTION_ISSUE -> uiState.updateState(systemDialogUiState = SystemDialogUiState.ShowNoInternetConnection.asOneTimeEvent())
                OAuthErrorCode.USER_IS_SUSPENDED -> uiState.updateState(requestNewOtpErrorMessage = R.string.sign_in_request_otp_screen_error_phone_number_suspended)
                else -> uiState.updateState(systemDialogUiState = SystemDialogUiState.ShowError(errorMessage = error.description).asOneTimeEvent())
            }
        }

        override fun onShowOtpInputScreen(otpLength: Int) {
            startOtpResendTimer()
            uiState = uiState.updateState()
        }

        override fun onShowTimeBasedOtpVerificationInputScreen(accountName: String) {
            navigateToRoute(SignInTimeBasedOtpVerificationNav.routeWithArguments(accountName))
        }
    }

    fun recheckInternetConnection() {
        when (networkState.isConnected) {
            true -> when (isVerifyOtpLastAction) {
                true -> verifyOtp()
                false -> requestNewOtp()
                else -> DoNothing
            }
            false -> uiState =
                uiState.updateState(systemDialogUiState = SystemDialogUiState.ShowNoInternetConnection.asOneTimeEvent())
        }
    }
}