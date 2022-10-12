package com.paywings.oauth.android.sample_app.ui.screens.email_change

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
import com.paywings.oauth.android.sample_app.ui.nav.graph.OAUTH_ROUTE
import com.paywings.oauth.android.sample_app.ui.screens.dialogs.system.SystemDialogUiState
import com.paywings.oauth.android.sample_app.ui.screens.email_verification.EmailVerificationRequiredNav
import com.paywings.oauth.android.sample_app.util.asOneTimeEvent
import com.paywings.oauth.android.sdk.data.enums.OAuthErrorCode
import com.paywings.oauth.android.sdk.initializer.PayWingsOAuthClient
import com.paywings.oauth.android.sdk.service.callback.ChangeUnverifiedEmailCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@HiltViewModel
class ChangeUnverifiedEmailViewModel @Inject constructor(
    private val routeNavigator: RouteNavigator,
    private val networkState: NetworkState
): ViewModel(), RouteNavigator by routeNavigator {

    var uiState: ChangeUnverifiedEmailUiState by mutableStateOf(value = ChangeUnverifiedEmailUiState())

    fun setPreviousEmail(email: String) {
        uiState = uiState.updateState(previousEmail = email)
    }

    fun setNewEmail(email: String) {
        uiState = uiState.updateState(email = email)
    }

    fun changeEmailConfirm() {
        viewModelScope.launch {
            PayWingsOAuthClient.instance.changeUnverifiedEmail(
                email = uiState.email,
                callback = changeUnverifiedEmailCallback
            )
        }
    }

    private val changeUnverifiedEmailCallback = object : ChangeUnverifiedEmailCallback {
        override fun onError(error: OAuthErrorCode, errorMessage: String?) {
            uiState = when(error) {
                OAuthErrorCode.NO_INTERNET -> uiState.updateState(systemDialogUiState = SystemDialogUiState.ShowNoInternetConnection.asOneTimeEvent())
                OAuthErrorCode.INVALID_EMAIL -> uiState.updateState(emailErrorMessage = R.string.user_registration_screen_error_invalid_email)
                else -> uiState.updateState(systemDialogUiState = SystemDialogUiState.ShowError(errorMessage = error.description).asOneTimeEvent())
            }
        }

        override fun onShowEmailConfirmationScreen(email: String, autoEmailSent: Boolean) {
            navigateToRoute(EmailVerificationRequiredNav.routeWithArguments(email = email, autoEmailSent = autoEmailSent))
        }

        override fun onUserSignInRequired() {
            navigateToRoute(OAUTH_ROUTE)
        }

    }

    fun recheckInternetConnection() {
        when (networkState.isConnected) {
            true -> changeEmailConfirm()
            false -> uiState =
                uiState.updateState(systemDialogUiState = SystemDialogUiState.ShowNoInternetConnection.asOneTimeEvent())
        }
    }
}