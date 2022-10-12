package com.paywings.oauth.android.sample_app.ui.screens.signin

import androidx.annotation.StringRes
import com.paywings.oauth.android.sample_app.ui.screens.dialogs.system.SystemDialogUiState
import com.paywings.oauth.android.sample_app.util.OneTimeEvent

data class SignInOtpVerificationUiState(
    val otp: String = "",
    val otpLength: Int = 0,
    val otpPhoneNumberFormatted: String = "",
    val systemDialogUiState: OneTimeEvent<SystemDialogUiState>? = null,
    val isButtonVerifyOtpLoading: Boolean = false,
    val isButtonRequestNewOtpLoading: Boolean = false,
    val showInvalidOtp: Boolean = false,
    @StringRes val verifyOtpErrorMessage: Int? = null,
    @StringRes val requestNewOtpErrorMessage: Int? = null
)

fun SignInOtpVerificationUiState.updateState(
    otp: String = this.otp,
    otpLength: Int = this.otpLength,
    otpPhoneNumberFormatted: String = this.otpPhoneNumberFormatted,
    systemDialogUiState: OneTimeEvent<SystemDialogUiState>? = null,
    isButtonVerifyOtpLoading: Boolean = false,
    isButtonRequestNewOtpLoading: Boolean = false,
    showInvalidOtp: Boolean = false,
    @StringRes verifyOtpErrorMessage: Int? = null,
    @StringRes requestNewOtpErrorMessage: Int? = null): SignInOtpVerificationUiState {
    return SignInOtpVerificationUiState(
        otp = otp,
        otpLength = otpLength,
        otpPhoneNumberFormatted = otpPhoneNumberFormatted,
        systemDialogUiState = systemDialogUiState,
        isButtonVerifyOtpLoading = isButtonVerifyOtpLoading,
        isButtonRequestNewOtpLoading = isButtonRequestNewOtpLoading,
        showInvalidOtp = showInvalidOtp,
        verifyOtpErrorMessage = verifyOtpErrorMessage,
        requestNewOtpErrorMessage = requestNewOtpErrorMessage)
}

fun SignInOtpVerificationUiState.resetState(): SignInOtpVerificationUiState {
    return SignInOtpVerificationUiState()
}