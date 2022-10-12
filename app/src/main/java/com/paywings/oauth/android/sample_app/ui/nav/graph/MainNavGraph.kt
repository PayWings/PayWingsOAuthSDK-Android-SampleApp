package com.paywings.oauth.android.sample_app.ui.nav.graph

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.paywings.oauth.android.sample_app.ui.screens.main.MainNav

const val MAIN_ROUTE = "main"

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalPermissionsApi
fun NavGraphBuilder.mainNavGraph(navHostController: NavHostController, onCloseApp: () -> Unit) {
    navigation(
        startDestination = MainNav.route,
        route = MAIN_ROUTE
    ) {
        MainNav.composable(builder = this, navHostController = navHostController, onCloseApp = onCloseApp)
    }
}