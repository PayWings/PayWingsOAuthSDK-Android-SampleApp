package com.paywings.oauth.android.sample_app.ui.screens.signin

import androidx.annotation.StringRes
import com.hbb20.CCPCountry
import com.paywings.oauth.android.sample_app.ui.screens.dialogs.system.SystemDialogUiState
import com.paywings.oauth.android.sample_app.util.OneTimeEvent
import com.paywings.oauth.android.sample_app.util.asOneTimeEvent

data class SignInRequestOtpUiState(
    val phoneNumber: String = "",
    val phoneNumberTemplate: String = "",
    val phoneNumberLength: Int = 0,
    val isPhoneNumberValid: Boolean = false,
    val selectedCountry: CCPCountry,
    val queryFilteredCountries: List<CCPCountry> = emptyList(),
    val filterCountrySearchString: String = "",
    val showCountrySelectDialog: OneTimeEvent<Boolean> = false.asOneTimeEvent(),
    val systemDialogUiState: OneTimeEvent<SystemDialogUiState>? = null,
    val isLoading: Boolean = false,
    @StringRes val errorMessage: Int? = null
)

fun SignInRequestOtpUiState.updateState(
    phoneNumber: String = this.phoneNumber,
    phoneNumberTemplate: String = this.phoneNumberTemplate,
    phoneNumberLength: Int = this.phoneNumberLength,
    isPhoneNumberValid: Boolean = this.isPhoneNumberValid,
    selectedCountry: CCPCountry = this.selectedCountry,
    queryFilteredCountries: List<CCPCountry> = emptyList(),
    filterCountrySearchString: String = this.filterCountrySearchString,
    showCountrySelectDialog: OneTimeEvent<Boolean> = this.showCountrySelectDialog,
    systemDialogUiState: OneTimeEvent<SystemDialogUiState>? = null,
    isLoading: Boolean = false,
    @StringRes errorMessageResId: Int? = null): SignInRequestOtpUiState {
    return SignInRequestOtpUiState(
        phoneNumber = phoneNumber,
        phoneNumberTemplate = phoneNumberTemplate,
        phoneNumberLength = phoneNumberLength,
        isPhoneNumberValid = isPhoneNumberValid,
        selectedCountry = selectedCountry,
        queryFilteredCountries = queryFilteredCountries,
        filterCountrySearchString = filterCountrySearchString,
        showCountrySelectDialog = showCountrySelectDialog,
        systemDialogUiState = systemDialogUiState,
        isLoading = isLoading,
        errorMessage = errorMessageResId)
}

fun SignInRequestOtpUiState.resetState(selectedCountry: CCPCountry): SignInRequestOtpUiState {
    return SignInRequestOtpUiState(selectedCountry = selectedCountry)
}
