package com.paywings.oauth.android.sample_app.ui.screens.initialization

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.paywings.oauth.android.sample_app.R
import com.paywings.oauth.android.sample_app.ui.nav.NavRoute
import com.paywings.oauth.android.sample_app.ui.screens.dialogs.system.ErrorDialog
import com.paywings.oauth.android.sample_app.ui.screens.dialogs.system.SystemDialogUiState
import com.paywings.oauth.android.sample_app.util.consume
import com.vanpra.composematerialdialogs.rememberMaterialDialogState


/**
 * Every screen has a route, so that we don't have to add the route setup of all screens in one file.
 *
 * Inherits NavRoute, to be able to navigate away from this screen. All navigation logic is in there.
 */
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
object InitializationNav : NavRoute<InitializationViewModel> {

    override val route = "initialization_screen"

    @Composable
    override fun viewModel(): InitializationViewModel = hiltViewModel()

    @Composable
    override fun Content(
        viewModel: InitializationViewModel,
        arguments: Bundle?,
        onCloseApp: () -> Unit
    ) = InitializationScreen(viewModel = viewModel, onCloseApp = onCloseApp)
}

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun InitializationScreen(viewModel: InitializationViewModel, onCloseApp: () -> Unit) {

    val noInternetConnectionDialogState = rememberMaterialDialogState(initialValue = false)
    val errorDialogState = rememberMaterialDialogState(initialValue = false)
    var errorMessage: String by remember { mutableStateOf("") }

    val uiState = viewModel.uiState

    uiState.systemDialogUiState?.consume {
        when (it) {
            is SystemDialogUiState.ShowTooManySMSRequest -> Unit
            is SystemDialogUiState.ShowNoInternetConnection -> noInternetConnectionDialogState.show()
            is SystemDialogUiState.ShowError -> {
                errorMessage = it.errorMessage
                errorDialogState.show()
            }
        }
    }

    InitializationContent()

    ErrorDialog(
        dialogState = errorDialogState,
        detailedMessage = errorMessage,
        onCancel = {
            errorDialogState.takeIf { it.showing }?.hide()
            onCloseApp()
        }
    )

    BackHandler(enabled = true, onBack = { onCloseApp() })
}

@Composable
fun InitializationContent() {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (logoWithText, progressIndicatorWithDescription) = createRefs()

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "",
            modifier = Modifier
                .requiredSize(200.dp)
                .constrainAs(logoWithText) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .wrapContentSize()
                .constrainAs(progressIndicatorWithDescription) {
                    top.linkTo(logoWithText.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            CircularProgressIndicator(strokeWidth = 2.dp)
            Text(text = stringResource(R.string.initialization_screen_progress_message))
        }
    }
}