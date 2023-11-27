package com.paywings.oauth.android.sample_app.ui.screens.signin

import androidx.annotation.StringRes
import com.paywings.oauth.android.sample_app.ui.screens.dialogs.system.SystemDialogUiState
import com.paywings.oauth.android.sample_app.util.OneTimeEvent

data class SignInTimeBasedOtpVerificationUiState(
    val timeBasedOtp: String = "",
    val timeBasedOtpLength: Int = 6,
    val systemDialogUiState: OneTimeEvent<SystemDialogUiState>? = null,
    val isButtonVerifyTimeBasedOtpLoading: Boolean = false,
    val showInvalidTimeBasedOtp: Boolean = false,
    @StringRes val verifyTimeBasedOtpErrorMessage: Int? = null,
)

fun SignInTimeBasedOtpVerificationUiState.updateState(
    timeBasedOtp: String = this.timeBasedOtp,
    timeBasedOtpLength: Int = this.timeBasedOtpLength,
    systemDialogUiState: OneTimeEvent<SystemDialogUiState>? = null,
    isButtonVerifyTimeBasedOtpLoading: Boolean = false,
    showInvalidTimeBasedOtp: Boolean = false,
    @StringRes verifyTimeBasedOtpErrorMessage: Int? = null): SignInTimeBasedOtpVerificationUiState {
    return SignInTimeBasedOtpVerificationUiState(
        timeBasedOtp = timeBasedOtp,
        timeBasedOtpLength = timeBasedOtpLength,
        systemDialogUiState = systemDialogUiState,
        isButtonVerifyTimeBasedOtpLoading = isButtonVerifyTimeBasedOtpLoading,
        showInvalidTimeBasedOtp = showInvalidTimeBasedOtp,
        verifyTimeBasedOtpErrorMessage = verifyTimeBasedOtpErrorMessage)
}

fun SignInTimeBasedOtpVerificationUiState.resetState(): SignInTimeBasedOtpVerificationUiState {
    return SignInTimeBasedOtpVerificationUiState()
}