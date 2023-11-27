package com.paywings.oauth.android.sample_app.ui.screens.signin

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hbb20.CCPCountry
import com.paywings.oauth.android.sample_app.R
import com.paywings.oauth.android.sample_app.ui.nav.NavRoute
import com.paywings.oauth.android.sample_app.ui.screens.components.PhoneNumberInput
import com.paywings.oauth.android.sample_app.ui.screens.components.ProcessingButton
import com.paywings.oauth.android.sample_app.ui.screens.components.ScreenTitle
import com.paywings.oauth.android.sample_app.ui.screens.components.ShowErrorText
import com.paywings.oauth.android.sample_app.ui.screens.dialogs.country.CountrySelectDialog
import com.paywings.oauth.android.sample_app.ui.screens.dialogs.system.ErrorDialog
import com.paywings.oauth.android.sample_app.ui.screens.dialogs.system.NoInternetConnectionDialog
import com.paywings.oauth.android.sample_app.ui.screens.dialogs.system.SystemDialogUiState
import com.paywings.oauth.android.sample_app.ui.screens.dialogs.system.TooManySMSRequestDialog
import com.paywings.oauth.android.sample_app.util.consume
import com.vanpra.composematerialdialogs.rememberMaterialDialogState

/**
 * Every screen has a route, so that we don't have to add the route setup of all screens in one file.
 *
 * Inherits NavRoute, to be able to navigate away from this screen. All navigation logic is in there.
 */
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
object SignInRequestOtpNav : NavRoute<SignInRequestOtpViewModel> {

    override val route = "sign_in_request_otp_screen"

    @Composable
    override fun viewModel(): SignInRequestOtpViewModel = hiltViewModel()

    @Composable
    override fun Content(
        viewModel: SignInRequestOtpViewModel,
        arguments: Bundle?,
        onCloseApp: () -> Unit
    ) = SignInRequestOtpScreen(viewModel = viewModel, onCloseApp = onCloseApp)
}

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun SignInRequestOtpScreen(viewModel: SignInRequestOtpViewModel, onCloseApp: () -> Unit) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val phoneNumberInputTextFieldFocusRequester = FocusRequester()
    var autoFocusPhoneNumberInputTextField by remember { mutableStateOf(true) }

    val countrySelectDialogState = rememberMaterialDialogState(initialValue = false)
    val tooManySMSRequestDialogState = rememberMaterialDialogState(initialValue = false)
    val noInternetConnectionDialogState = rememberMaterialDialogState(initialValue = false)
    val errorDialogState = rememberMaterialDialogState(initialValue = false)
    var errorMessage: String by remember { mutableStateOf("") }

    val uiState = viewModel.uiState

    uiState.systemDialogUiState?.consume {
        when (it) {
            is SystemDialogUiState.ShowTooManySMSRequest -> tooManySMSRequestDialogState.show()
            is SystemDialogUiState.ShowNoInternetConnection -> noInternetConnectionDialogState.show()
            is SystemDialogUiState.ShowError -> {
                errorMessage = it.errorMessage
                errorDialogState.show()
            }
        }
    }

    uiState.showCountrySelectDialog.consume {
        if (it) {
            countrySelectDialogState.show()
        }
    }

    SignInRequestOtpContent(
        country= uiState.selectedCountry,
        phoneNumber = uiState.phoneNumber,
        placeholderText = uiState.phoneNumberTemplate,
        phoneNumberLength = uiState.phoneNumberLength,
        inputEnabled = !uiState.isLoading,
        onPhoneNumberChange = { newPhoneNumber -> viewModel.setMobileNumber(newPhoneNumber = newPhoneNumber) },
        onDropDownShow = { viewModel.showCountrySelectDialog() },
        phoneNumberInputTextFieldFocusRequester = phoneNumberInputTextFieldFocusRequester,
        buttonIsEnabled = uiState.isPhoneNumberValid && !uiState.isLoading,
        buttonIsLoading = uiState.isLoading,
        errorResId = uiState.errorMessage,
        onButtonClick = { viewModel.requestOtpSend() }
    )

    CountrySelectDialog(
        dialogState = countrySelectDialogState,
        countries = uiState.queryFilteredCountries,
        searchCountryNameValue = uiState.filterCountrySearchString,
        onSearchValueChange = { viewModel.onSearchStringChanged(it) },
        onSelectedCountry = { selectedCountry ->
            viewModel.setSelectedCountry(country = selectedCountry)
            countrySelectDialogState.takeIf { it.showing }?.hide()
            autoFocusPhoneNumberInputTextField = true
        },
        onClose = {
            countrySelectDialogState.takeIf { it.showing }?.hide()
            autoFocusPhoneNumberInputTextField = true
        }
    )

    TooManySMSRequestDialog(
        dialogState = tooManySMSRequestDialogState,
        onClose = {
            tooManySMSRequestDialogState.takeIf { it.showing }?.hide()
        }
    )

    NoInternetConnectionDialog(
        dialogState = noInternetConnectionDialogState,
        cancelButtonNameResId = R.string.button_exit,
        onRecheckInternetConnection = {
            noInternetConnectionDialogState.takeIf { it.showing }?.hide()
            viewModel.recheckInternetConnection()
        },
        onCancel = {
            noInternetConnectionDialogState.takeIf { it.showing }?.hide()
            onCloseApp()
        }
    )

    ErrorDialog(
        dialogState = errorDialogState,
        detailedMessage = errorMessage,
        onCancel = {
            errorDialogState.takeIf { it.showing }?.hide()
            onCloseApp()
        }
    )

    BackHandler(enabled = true, onBack = { onCloseApp() })

    SideEffect {
        if (autoFocusPhoneNumberInputTextField) {
            phoneNumberInputTextFieldFocusRequester.requestFocus()
            keyboardController?.show()
            autoFocusPhoneNumberInputTextField = false
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun SignInRequestOtpContent(
    country: CCPCountry,
    phoneNumber: String,
    placeholderText: String,
    phoneNumberLength: Int,
    inputEnabled: Boolean,
    onPhoneNumberChange: (newPhoneNumber: String) -> Unit,
    onDropDownShow: () -> Unit,
    phoneNumberInputTextFieldFocusRequester: FocusRequester,
    buttonIsEnabled: Boolean,
    buttonIsLoading: Boolean,
    @StringRes errorResId: Int?,
    onButtonClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp, 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            ScreenTitle(
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                stringResId = R.string.sign_in_phone_number_input_screen_title
            )
            Spacer(Modifier.height(24.dp))
            PhoneNumberInput(
                country = country,
                phoneNumber = phoneNumber,
                placeholderText = placeholderText,
                phoneNumberLength = phoneNumberLength,
                inputEnabled = inputEnabled,
                onPhoneNumberChange = onPhoneNumberChange,
                onDropDownShow = onDropDownShow,
                phoneNumberInputTextFieldFocusRequester = phoneNumberInputTextFieldFocusRequester
            )
            Text(
                text = stringResource(id = R.string.sign_in_phone_number_input_screen_description),
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(start = 8.dp, end = 8.dp)
            )
            Spacer(Modifier.height(24.dp))
            ProcessingButton(
                textResId = R.string.button_send_confirmation_code,
                isLoading = buttonIsLoading,
                isEnabled = buttonIsEnabled,
                onClick = onButtonClick
            )
            ShowErrorText(errorResId = errorResId)
        }
    }

}