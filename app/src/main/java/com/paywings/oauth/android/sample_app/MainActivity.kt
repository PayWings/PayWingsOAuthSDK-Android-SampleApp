package com.paywings.oauth.android.sample_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.paywings.oauth.android.sample_app.network.NetworkState
import com.paywings.oauth.android.sample_app.ui.nav.graph.StartUpNavGraph
import com.paywings.oauth.android.sample_app.ui.theme.PayWingsOAuthAndroidSDKSampleAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalPermissionsApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var networkState: NetworkState


    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        installSplashScreen()
        super.onCreate(savedInstanceState)
        networkState.start()
        setActivityContent()
    }

    override fun onDestroy() {
        super.onDestroy()
        networkState.destroy()
    }

    private fun setActivityContent() {
        setContent {
            PayWingsOAuthAndroidSDKSampleAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()

                    StartUpNavGraph(
                        navController,
                        onCloseApp = { onCloseApp() }
                    )
                }
            }
        }
    }

    private fun onCloseApp() {
        finishAndRemoveTask()
    }

}