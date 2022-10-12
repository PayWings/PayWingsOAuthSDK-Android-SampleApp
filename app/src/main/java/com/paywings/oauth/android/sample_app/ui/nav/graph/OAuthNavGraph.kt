package com.paywings.oauth.android.sample_app.ui.nav.graph

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.paywings.oauth.android.sample_app.ui.screens.email_change.ChangeUnverifiedEmailNav
import com.paywings.oauth.android.sample_app.ui.screens.email_verification.EmailVerificationRequiredNav
import com.paywings.oauth.android.sample_app.ui.screens.signin.SignInOtpVerificationNav
import com.paywings.oauth.android.sample_app.ui.screens.signin.SignInRequestOtpNav
import com.paywings.oauth.android.sample_app.ui.screens.user_registration.UserRegistrationNav

const val OAUTH_ROUTE = "oauth"

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalPermissionsApi
fun NavGraphBuilder.oauthNavGraph(navHostController: NavHostController, onCloseApp: () -> Unit) {
    navigation(
        startDestination = SignInRequestOtpNav.route,
        route = OAUTH_ROUTE
    ) {
        SignInRequestOtpNav.composable(builder = this, navHostController = navHostController, onCloseApp = onCloseApp)
        SignInOtpVerificationNav.composable(builder = this, navHostController = navHostController, onCloseApp = onCloseApp)
        UserRegistrationNav.composable(builder = this, navHostController = navHostController, onCloseApp = onCloseApp)
        EmailVerificationRequiredNav.composable(builder = this, navHostController = navHostController, onCloseApp = onCloseApp)
        ChangeUnverifiedEmailNav.composable(builder = this, navHostController = navHostController, onCloseApp = onCloseApp)
        mainNavGraph(navHostController = navHostController, onCloseApp = onCloseApp)
    }
}