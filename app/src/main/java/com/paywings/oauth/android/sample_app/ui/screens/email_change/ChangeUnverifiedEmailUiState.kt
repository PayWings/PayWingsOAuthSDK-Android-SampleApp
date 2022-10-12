package com.paywings.oauth.android.sample_app.ui.screens.email_change

import androidx.annotation.StringRes
import com.paywings.oauth.android.sample_app.ui.screens.dialogs.system.SystemDialogUiState
import com.paywings.oauth.android.sample_app.util.OneTimeEvent

data class ChangeUnverifiedEmailUiState(
    val previousEmail: String = "",
    val email: String = "",
    @StringRes val emailErrorMessage: Int? = null,
    val systemDialogUiState: OneTimeEvent<SystemDialogUiState>? = null,
    val isLoading: Boolean = false,
    @StringRes val errorMessage: Int? = null
)

fun ChangeUnverifiedEmailUiState.updateState(
    previousEmail: String = this.previousEmail,
    email: String = this.email,
    emailErrorMessage: Int? = null,
    systemDialogUiState: OneTimeEvent<SystemDialogUiState>? = null,
    isLoading: Boolean = false,
    errorMessage: Int? = null
) : ChangeUnverifiedEmailUiState {
    return ChangeUnverifiedEmailUiState(
        previousEmail = previousEmail,
        email = email,
        emailErrorMessage = emailErrorMessage,
        systemDialogUiState = systemDialogUiState,
        isLoading = isLoading,
        errorMessage = errorMessage
    )
}

fun ChangeUnverifiedEmailUiState.resetState(): ChangeUnverifiedEmailUiState {
    return ChangeUnverifiedEmailUiState()
}
