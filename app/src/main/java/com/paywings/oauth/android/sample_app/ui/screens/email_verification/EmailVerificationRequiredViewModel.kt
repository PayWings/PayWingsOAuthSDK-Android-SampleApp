package com.paywings.oauth.android.sample_app.ui.screens.email_verification

import androidx.annotation.DoNotInline
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
import com.paywings.oauth.android.sample_app.ui.screens.email_change.updateState
import com.paywings.oauth.android.sample_app.ui.screens.main.MainNav
import com.paywings.oauth.android.sample_app.util.Constants.DoNothing
import com.paywings.oauth.android.sample_app.util.UserSession
import com.paywings.oauth.android.sample_app.util.asOneTimeEvent
import com.paywings.oauth.android.sdk.data.enums.OAuthErrorCode
import com.paywings.oauth.android.sdk.initializer.PayWingsOAuthClient
import com.paywings.oauth.android.sdk.service.callback.CheckEmailVerifiedCallback
import com.paywings.oauth.android.sdk.service.callback.SendNewVerificationEmailCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@HiltViewModel
class EmailVerificationRequiredViewModel @Inject constructor(
    private val routeNavigator: RouteNavigator,
    private val networkState: NetworkState,
    private val userSession: UserSession
): ViewModel(), RouteNavigator by routeNavigator {

    var uiState: EmailConfirmationRequiredUiState by mutableStateOf(value = EmailConfirmationRequiredUiState())
    private var isCheckEmailVerifiedLastAction: Boolean? = null

    fun setEmail(email: String, autoEmailSent: Boolean) {
        uiState = uiState.updateState(email = email, emailVerificationRunning = autoEmailSent)
        if (autoEmailSent) {
            checkEmailVerified()
        }
    }

    fun cancelEmailVerification() {
        uiState = uiState.updateState(emailVerificationRunning = false)
    }

    private fun checkEmailVerified() {
        isCheckEmailVerifiedLastAction = true
        viewModelScope.launch() {
            PayWingsOAuthClient.instance.checkEmailVerified(callback = checkEmailVerifiedCallback)
        }
    }

    private val checkEmailVerifiedCallback = object: CheckEmailVerifiedCallback {
        override fun onEmailNotVerified() {
            if (uiState.emailVerificationRunning) {
                viewModelScope.launch {
                    delay(NetworkConstants.CHECK_EMAIL_VERIFIED_DELAY_IN_MILLISECONDS)
                    checkEmailVerified()
                }
            }
        }

        override fun onError(error: OAuthErrorCode, errorMessage: String?) {
            uiState = when(error) {
                OAuthErrorCode.NO_INTERNET -> uiState.updateState(systemDialogUiState = SystemDialogUiState.ShowNoInternetConnection.asOneTimeEvent())
                else -> uiState.updateState(systemDialogUiState = SystemDialogUiState.ShowError(errorMessage = error.description).asOneTimeEvent())
            }
        }

        override fun onSignInSuccessful(
            refreshToken: String,
            accessToken: String,
            accessTokenExpirationTime: Long
        ) {
            userSession.signInUser(refreshToken = refreshToken, accessToken = accessToken, accessTokenExpirationTime = accessTokenExpirationTime)
            navigateToRoute(MainNav.route)
        }

        override fun onUserSignInRequired() {
            navigateToRoute(OAUTH_ROUTE)
        }

    }

    fun sendNewVerificationEmail() {
        isCheckEmailVerifiedLastAction = false
        viewModelScope.launch() {
            PayWingsOAuthClient.instance.sendNewVerificationEmail(callback = sendNewVerificationEmailCallback)
        }
    }

    private val sendNewVerificationEmailCallback = object : SendNewVerificationEmailCallback {
        override fun onError(error: OAuthErrorCode, errorMessage: String?) {
            uiState = when(error) {
                OAuthErrorCode.NO_INTERNET -> uiState.updateState(systemDialogUiState = SystemDialogUiState.ShowNoInternetConnection.asOneTimeEvent())
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
            true -> when(isCheckEmailVerifiedLastAction) {
                true -> checkEmailVerified()
                false -> sendNewVerificationEmail()
                else -> DoNothing
            }
            false -> uiState =
                uiState.updateState(systemDialogUiState = SystemDialogUiState.ShowNoInternetConnection.asOneTimeEvent())
        }
    }
}