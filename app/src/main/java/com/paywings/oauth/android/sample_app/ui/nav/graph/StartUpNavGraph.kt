package com.paywings.oauth.android.sample_app.ui.nav.graph

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.paywings.oauth.android.sample_app.ui.screens.initialization.InitializationNav

const val START_UP_ROUTE = "startup"

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalPermissionsApi
@Composable
fun StartUpNavGraph(navHostController: NavHostController, onCloseApp: () -> Unit) {
    NavHost(
        navController = navHostController,
        startDestination = InitializationNav.route,
        route = START_UP_ROUTE
    ) {
        InitializationNav.composable(builder = this, navHostController = navHostController, onCloseApp = onCloseApp)
        oauthNavGraph(navHostController = navHostController, onCloseApp = onCloseApp)
    }
}