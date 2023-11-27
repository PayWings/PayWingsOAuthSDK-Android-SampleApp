package com.paywings.oauth.android.sample_app.ui.screens.signin

import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.paywings.oauth.android.sample_app.R
import com.paywings.oauth.android.sample_app.ui.nav.NavRoute
import com.paywings.oauth.android.sample_app.ui.screens.components.ScreenTitle
import com.paywings.oauth.android.sample_app.ui.theme.contentEdgePadding


/**
 * Every screen has a route, so that we don't have to add the route setup of all screens in one file.
 *
 * Inherits NavRoute, to be able to navigate away from this screen. All navigation logic is in there.
 */
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
object SignInTimeBasedOtpSetupScreenNav : NavRoute<SignInTimeBasedOtpSetupViewModel> {

    private const val ACCOUNT_NAME = "accountName"
    private const val SECRET_KEY = "secretKey"

    override val route = "sign_in_time_based_otp_setup_screen"

    fun routeWithArguments(accountName: String, secretKey: String): String {
        return "$route/${Uri.encode(accountName)}/${Uri.encode(secretKey)}"
    }
    @Composable
    override fun viewModel(): SignInTimeBasedOtpSetupViewModel = hiltViewModel()

    override fun getArguments(): List<NamedNavArgument> {
        return listOf(
            navArgument(ACCOUNT_NAME) {
                type = NavType.StringType
            },
            navArgument(SECRET_KEY) {
                type = NavType.StringType
            })
    }

    private fun parseAccountName(arguments: Bundle?): String? {
        return arguments?.getString(ACCOUNT_NAME)
    }

    private fun parseSecretKey(arguments: Bundle?): String? {
        return arguments?.getString(SECRET_KEY)
    }

    @Composable
    override fun Content(
        viewModel: SignInTimeBasedOtpSetupViewModel,
        arguments: Bundle?,
        onCloseApp: () -> Unit
    ) = SignInTimeBasedOtpSetupScreen(viewModel = viewModel, accountName = parseAccountName(arguments)?:"", secretKey = parseSecretKey(arguments)?:"", onCloseApp = onCloseApp)
}


@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun SignInTimeBasedOtpSetupScreen(viewModel: SignInTimeBasedOtpSetupViewModel, accountName: String, secretKey: String, onCloseApp: () -> Unit) {

    SignInTimeBasedOtpSetupContent(
        accountName = accountName,
        secretKey = secretKey,
        onContinue = { viewModel.navigateToRoute(SignInTimeBasedOtpVerificationNav.routeWithArguments(accountName)) },
        onCancel = { viewModel.navigateToRoute(SignInRequestOtpNav.route) }
    )

    BackHandler(enabled = true, onBack = { viewModel.navigateToRoute(SignInRequestOtpNav.route) })
}

@Composable
fun SignInTimeBasedOtpSetupContent(
    accountName: String,
    secretKey: String,
    onContinue: () -> Unit,
    onCancel: () -> Unit) {

    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    Column(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(horizontal = MaterialTheme.shapes.contentEdgePadding),
    ) {
        ScreenTitle(
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
            title = stringResource(id = R.string.sign_in_time_based_otp_setup_screen_title)
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(id = R.string.sign_in_time_based_otp_setup_screen_subtitle)
        )
        Spacer(Modifier.height(24.dp))
        Row {
            Text(
                text = stringResource(
                    id = R.string.sign_in_time_based_otp_setup_screen_account_name,
                    accountName
                )
            )
            IconButton(onClick = { clipboardManager.setText(AnnotatedString(accountName)) }) {
                Icon(imageVector = Icons.Outlined.ContentCopy, contentDescription = Icons.Outlined.ContentCopy.name)
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = stringResource(
            id = R.string.sign_in_time_based_otp_setup_screen_secret_key,
                secretKey
            )
        )
        IconButton(onClick = { clipboardManager.setText(AnnotatedString(secretKey)) }) {
            Icon(imageVector = Icons.Outlined.ContentCopy, contentDescription = Icons.Outlined.ContentCopy.name)
        }
        Spacer(Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween) {
            Box(modifier = Modifier.fillMaxWidth(0.5f)) {
                Button(modifier = Modifier.fillMaxWidth(), onClick = { onContinue() }) {
                    Text(text = stringResource(id = R.string.button_verify_setup))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(modifier = Modifier.fillMaxWidth(), onClick = { onCancel() }) {
                Text(text = stringResource(id = R.string.button_cancel))
            }

        }
        Spacer(Modifier.height(8.dp))
    }
}