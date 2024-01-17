package com.paywings.oauth.android.sample_app.ui.screens.main

import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paywings.oauth.android.sample_app.ui.nav.RouteNavigator
import com.paywings.oauth.android.sample_app.ui.nav.graph.OAUTH_ROUTE
import com.paywings.oauth.android.sample_app.ui.screens.dialogs.system.SystemDialogUiState
import com.paywings.oauth.android.sample_app.util.asOneTimeEvent
import com.paywings.oauth.android.sample_app.util.toLocalDateTime
import com.paywings.oauth.android.sdk.data.enums.HttpRequestMethod
import com.paywings.oauth.android.sdk.data.enums.OAuthErrorCode
import com.paywings.oauth.android.sdk.initializer.PayWingsOAuthClient
import com.paywings.oauth.android.sdk.service.callback.GetNewAuthorizationDataCallback
import com.paywings.oauth.android.sdk.service.callback.GetUserDataCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@HiltViewModel
class MainViewModel @Inject constructor(
    private val routeNavigator: RouteNavigator
): ViewModel(), RouteNavigator by routeNavigator  {

    var uiState: MainUiState by mutableStateOf(value = MainUiState())

    fun signOutUser() {
        viewModelScope.launch {
            PayWingsOAuthClient.instance.signOutUser()
            navigateToRoute(OAUTH_ROUTE)
        }
    }

    fun initialization() {
        getUserData()
    }

    private fun getUserData() {
        viewModelScope.launch {
            PayWingsOAuthClient.instance.getUserData(
                callback = getUserDataCallback
            )
        }
    }

    private val getUserDataCallback = object : GetUserDataCallback {
        override fun onError(error: OAuthErrorCode, errorMessage: String?) {
            Log.d("KycSampleApp", "Error: ${error.id} with description: ${error.description}")
        }

        override fun onUserData(
            userId: String,
            firstName: String?,
            lastName: String?,
            email: String?,
            emailConfirmed: Boolean,
            phoneNumber: String?
        ) {
            uiState = uiState.updateState(userId = userId, firstName = firstName?:"", lastName = lastName?:"", email = email?:"", phoneNumber = phoneNumber?:"")
        }

        override fun onUserSignInRequired() {
            navigateToRoute(OAUTH_ROUTE)
        }
    }

    fun getNewAuthorizationData() {
        viewModelScope.launch {
            PayWingsOAuthClient.instance.getNewAuthorizationData(methodUrl = "https://testing.api.call", httpRequestMethod = HttpRequestMethod.POST, callback =  getNewAuthorizationDataCallback)
        }
    }

    private val getNewAuthorizationDataCallback = object : GetNewAuthorizationDataCallback {
        override fun onError(error: OAuthErrorCode, errorMessage: String?) {
            uiState = uiState.updateState(systemDialogUiState = SystemDialogUiState.ShowError(errorMessage = error.description).asOneTimeEvent())
        }

        override fun onNewAuthorizationData(
            dpop: String,
            accessToken: String,
            accessTokenExpirationTime: Long
        ) {
            uiState = uiState.updateState(accessTokenExpirationDateTime = accessTokenExpirationTime.toLocalDateTime())
            getUserData()
        }

        override fun onUserSignInRequired() {
            navigateToRoute(OAUTH_ROUTE)
        }
    }
}