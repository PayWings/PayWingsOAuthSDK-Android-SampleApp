package com.paywings.oauth.android.sample_app.ui.screens.main

import com.paywings.oauth.android.sample_app.ui.screens.dialogs.system.SystemDialogUiState
import com.paywings.oauth.android.sample_app.util.OneTimeEvent

data class MainUiState(
    val userId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val accessTokenExpirationDateTime: String = "",
    val systemDialogUiState: OneTimeEvent<SystemDialogUiState>? = null,
)

fun MainUiState.updateState(
    userId: String = this.userId,
    firstName: String = this.firstName,
    lastName: String = this.lastName,
    email: String = this.email,
    phoneNumber: String = this.phoneNumber,
    accessTokenExpirationDateTime: String = this.accessTokenExpirationDateTime,
    systemDialogUiState: OneTimeEvent<SystemDialogUiState>? = null,
) : MainUiState {
    return MainUiState(
        userId = userId,
        firstName = firstName,
        lastName = lastName,
        email = email,
        phoneNumber = phoneNumber,
        accessTokenExpirationDateTime = accessTokenExpirationDateTime,
        systemDialogUiState = systemDialogUiState
    )
}

fun MainUiState.resetState(): MainUiState {
    return MainUiState()
}
