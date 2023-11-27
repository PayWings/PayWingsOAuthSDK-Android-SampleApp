package com.paywings.oauth.android.sample_app.ui.screens.initialization

import android.app.Application
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.lifecycle.AndroidViewModel
import com.paywings.oauth.android.sample_app.ui.nav.RouteNavigator
import com.paywings.oauth.android.sample_app.ui.nav.graph.MAIN_ROUTE
import com.paywings.oauth.android.sample_app.ui.nav.graph.OAUTH_ROUTE
import com.paywings.oauth.android.sample_app.ui.screens.dialogs.system.SystemDialogUiState
import com.paywings.oauth.android.sample_app.util.asOneTimeEvent
import com.paywings.oauth.android.sdk.data.enums.EnvironmentType
import com.paywings.oauth.android.sdk.data.enums.OAuthErrorCode
import com.paywings.oauth.android.sdk.initializer.OAuthInitializationCallback
import com.paywings.oauth.android.sdk.initializer.PayWingsOAuthClient
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@HiltViewModel
class InitializationViewModel @Inject constructor(
    application: Application,
    private val routeNavigator: RouteNavigator
) : AndroidViewModel(application), RouteNavigator by routeNavigator {

    private val context
        get() = getApplication<Application>()

    var uiState: InitializationUiState by mutableStateOf(value = InitializationUiState())

    var oauthInitializationRetryCount: Int = 0

    private val oauthInitializationCallback = object: OAuthInitializationCallback {
        override fun onFailure(error: OAuthErrorCode, errorMessage: String?) {
            if (oauthInitializationRetryCount < 2) {
                oauthInitializationRetryCount++
                oauthInitialization()
            } else {
                uiState = uiState.updateState(systemDialogUiState = SystemDialogUiState.ShowError(errorMessage = errorMessage?:"").asOneTimeEvent())
            }
        }

        override fun onSuccess() {
            when(PayWingsOAuthClient.instance.isUserSignIn()){
                true -> navigateToRoute(MAIN_ROUTE)
                false -> navigateToRoute(OAUTH_ROUTE)
            }
        }
    }

    init {
        oauthInitialization()
    }

    private fun oauthInitialization() {
        PayWingsOAuthClient.init(
            context = context,
            environmentType = EnvironmentType.TEST,
            apiKey = "fd724674-415d-42d7-b56c-fe3237c956d9",
            domain = "paywings.io",
            appPlatformID = "C9350E67-C251-4FCF-8B5F-01A865B36BAF",
            recaptchaKey = "6LfsCKIoAAAAACh_ycSZx6wgAngWBEi9NHrU541j",
            callback = oauthInitializationCallback
        )
    }
}

