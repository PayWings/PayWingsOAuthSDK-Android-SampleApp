package com.paywings.oauth.android.sample_app.ui.screens.email_verification

import com.paywings.oauth.android.sample_app.ui.screens.dialogs.system.SystemDialogUiState
import com.paywings.oauth.android.sample_app.util.OneTimeEvent

data class EmailConfirmationRequiredUiState(
    val email: String = "",
    val emailVerificationRunning: Boolean = false,
    val systemDialogUiState: OneTimeEvent<SystemDialogUiState>? = null,
    val isLoading: Boolean = false
)


fun EmailConfirmationRequiredUiState.updateState(
    email: String = this.email,
    emailVerificationRunning: Boolean = this.emailVerificationRunning,
    systemDialogUiState: OneTimeEvent<SystemDialogUiState>? = null,
    isLoading: Boolean = false,
) : EmailConfirmationRequiredUiState {
    return EmailConfirmationRequiredUiState(
        email = email,
        emailVerificationRunning = emailVerificationRunning,
        systemDialogUiState = systemDialogUiState,
        isLoading = isLoading
    )
}

fun EmailConfirmationRequiredUiState.resetState(): EmailConfirmationRequiredUiState {
    return EmailConfirmationRequiredUiState()
}
