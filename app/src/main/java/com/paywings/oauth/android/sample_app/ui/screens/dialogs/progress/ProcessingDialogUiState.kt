package com.paywings.oauth.android.sample_app.ui.screens.dialogs.progress

sealed class ProcessingDialogUiState {
    object ShowProcessing: ProcessingDialogUiState()
    object HideProcessing: ProcessingDialogUiState()
}