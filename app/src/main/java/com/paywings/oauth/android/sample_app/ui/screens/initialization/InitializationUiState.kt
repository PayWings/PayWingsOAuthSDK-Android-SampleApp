package com.paywings.oauth.android.sample_app.ui.screens.initialization

import androidx.annotation.StringRes
import com.paywings.oauth.android.sample_app.ui.screens.dialogs.system.SystemDialogUiState
import com.paywings.oauth.android.sample_app.util.OneTimeEvent

data class InitializationUiState(
    val systemDialogUiState: OneTimeEvent<SystemDialogUiState>? = null,
    @StringRes val errorMessage: Int? = null
)


fun InitializationUiState.updateState(
    systemDialogUiState: OneTimeEvent<SystemDialogUiState>? = null,
    errorMessage: Int? = null
) : InitializationUiState {
    return InitializationUiState(
        systemDialogUiState = systemDialogUiState,
        errorMessage = errorMessage
    )
}

fun InitializationUiState.resetState(): InitializationUiState {
    return InitializationUiState()
}
