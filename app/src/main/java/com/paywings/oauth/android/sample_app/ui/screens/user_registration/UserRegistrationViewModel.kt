package com.paywings.oauth.android.sample_app.ui.screens.user_registration

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
import com.paywings.oauth.android.sample_app.ui.screens.main.MainNav
import com.paywings.oauth.android.sample_app.util.CCPCountryHelper
import com.paywings.oauth.android.sample_app.util.PhoneNumberUtilHelper
import com.paywings.oauth.android.sample_app.util.UserSession
import com.paywings.oauth.android.sample_app.util.asOneTimeEvent
import com.paywings.oauth.android.sdk.data.enums.OAuthErrorCode
import com.paywings.oauth.android.sdk.initializer.PayWingsOAuthClient
import com.paywings.oauth.android.sdk.service.callback.RegisterUserCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@HiltViewModel
class UserRegistrationViewModel @Inject constructor(
    private val routeNavigator: RouteNavigator,
    private val networkState: NetworkState,
    private val userSession: UserSession
): ViewModel(), RouteNavigator by routeNavigator {

    var uiState: UserRegistrationUiState by mutableStateOf(value = UserRegistrationUiState())

    fun setFirstName(firstName: String) {
        uiState = uiState.updateState(firstName = firstName)
    }

    fun setLastName(lastName: String) {
        uiState = uiState.updateState(lastName = lastName)
    }

    fun setEmail(email: String) {
        uiState = uiState.updateState(email = email)
    }

    fun registerUser() {
        uiState = uiState.updateState(isLoading = true)
        viewModelScope.launch {
            PayWingsOAuthClient.instance.registerUser(
                firstName = uiState.firstName,
                lastName = uiState.lastName,
                email = uiState.email,
                callback = registerUserCallback
            )
        }
    }

    private val registerUserCallback = object: RegisterUserCallback {
        override fun onError(error: OAuthErrorCode, errorMessage: String?) {
            uiState = when(error) {
                OAuthErrorCode.NO_INTERNET -> uiState.updateState(systemDialogUiState = SystemDialogUiState.ShowNoInternetConnection.asOneTimeEvent())
                OAuthErrorCode.USER_IS_SUSPENDED -> uiState.updateState(errorMessageResId = R.string.sign_in_request_otp_screen_error_phone_number_suspended)
                OAuthErrorCode.INVALID_EMAIL -> uiState.updateState(emailErrorMessage = R.string.user_registration_screen_error_invalid_email)
                else -> uiState.updateState(systemDialogUiState = SystemDialogUiState.ShowError(errorMessage = error.description).asOneTimeEvent())
            }
        }

        override fun onShowEmailConfirmationScreen(email: String, autoEmailSent: Boolean) {
            navigateToRoute(EmailVerificationRequiredNav.routeWithArguments(email = email, autoEmailSent = autoEmailSent))
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

    fun recheckInternetConnection() {
        when (networkState.isConnected) {
            true -> registerUser()
            false -> uiState =
                uiState.updateState(systemDialogUiState = SystemDialogUiState.ShowNoInternetConnection.asOneTimeEvent())
        }
    }
}