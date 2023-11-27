package com.paywings.oauth.android.sample_app.ui.screens.signin

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.lifecycle.ViewModel
import com.paywings.oauth.android.sample_app.ui.nav.RouteNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@HiltViewModel
class SignInTimeBasedOtpSetupViewModel @Inject constructor(
    private val routeNavigator: RouteNavigator
): ViewModel(), RouteNavigator by routeNavigator {

}