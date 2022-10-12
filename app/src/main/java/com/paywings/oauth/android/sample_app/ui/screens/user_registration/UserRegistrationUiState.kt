package com.paywings.oauth.android.sample_app.ui.screens.user_registration

import androidx.annotation.StringRes
import com.paywings.oauth.android.sample_app.ui.screens.dialogs.system.SystemDialogUiState
import com.paywings.oauth.android.sample_app.util.OneTimeEvent

data class UserRegistrationUiState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    @StringRes val emailErrorMessage: Int? = null,
    val systemDialogUiState: OneTimeEvent<SystemDialogUiState>? = null,
    val isLoading: Boolean = false,
    @StringRes val errorMessage: Int? = null
)

fun UserRegistrationUiState.updateState(
    firstName: String = this.firstName,
    lastName: String = this.lastName,
    email: String = this.email,
    emailErrorMessage: Int? = null,
    systemDialogUiState: OneTimeEvent<SystemDialogUiState>? = null,
    isLoading: Boolean = false,
    @StringRes errorMessageResId: Int? = null): UserRegistrationUiState {
    return UserRegistrationUiState(
        firstName = firstName,
        lastName = lastName,
        email = email,
        emailErrorMessage = emailErrorMessage,
        systemDialogUiState = systemDialogUiState,
        isLoading = isLoading,
        errorMessage = errorMessageResId)
}

fun UserRegistrationUiState.resetState(): UserRegistrationUiState {
    return UserRegistrationUiState()
}
