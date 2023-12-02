package com.paywings.oauth.android.sample_app.ui.screens.signin

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hbb20.CCPCountry
import com.paywings.oauth.android.sample_app.R
import com.paywings.oauth.android.sample_app.network.NetworkState
import com.paywings.oauth.android.sample_app.ui.nav.RouteNavigator
import com.paywings.oauth.android.sample_app.ui.screens.dialogs.system.SystemDialogUiState
import com.paywings.oauth.android.sample_app.util.CCPCountryHelper
import com.paywings.oauth.android.sample_app.util.PhoneNumberUtilHelper
import com.paywings.oauth.android.sample_app.util.asOneTimeEvent
import com.paywings.oauth.android.sdk.data.enums.OAuthErrorCode
import com.paywings.oauth.android.sdk.initializer.PayWingsOAuthClient
import com.paywings.oauth.android.sdk.service.callback.SignInWithPhoneNumberRequestOtpCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@HiltViewModel
class SignInRequestOtpViewModel @Inject constructor(
    private val routeNavigator: RouteNavigator,
    private val networkState: NetworkState,
    private val ccpCountryHelper: CCPCountryHelper,
    private val phoneNumberUtilHelper: PhoneNumberUtilHelper
): ViewModel(), RouteNavigator by routeNavigator {

    var uiState: SignInRequestOtpUiState by mutableStateOf(value = SignInRequestOtpUiState(selectedCountry = ccpCountryHelper.getAutoDetectCountry()))

    init {
        setSelectedCountry(uiState.selectedCountry)
    }

    private var phoneNumberCountryCode: String = ""
    private var phoneNumber: String = ""

    fun setMobileNumber(newPhoneNumber: String) {
        uiState = uiState.updateState(phoneNumber = newPhoneNumber, isPhoneNumberValid = phoneNumberUtilHelper.isPhoneNumberValid(phoneNumber = newPhoneNumber, country = uiState.selectedCountry))
    }

    fun setSelectedCountry(country: CCPCountry) {
        phoneNumberUtilHelper.getCountryPhoneNumberInfo(country).let {
            uiState = uiState.updateState(selectedCountry = country, phoneNumberTemplate = it?.phoneNumberTemplate?:"", phoneNumberLength = it?.phoneNumberLength?:0)
        }
    }

    fun showCountrySelectDialog() {
        uiState = uiState.updateState(filterCountrySearchString = "", queryFilteredCountries = ccpCountryHelper.getCountryList(), showCountrySelectDialog = true.asOneTimeEvent())
    }

    fun onSearchStringChanged(newSearchString: String) {
        uiState = uiState.updateState(
            filterCountrySearchString = newSearchString,
            queryFilteredCountries = newSearchString.takeIf { it.isNotBlank() }?.let {
                ccpCountryHelper.getCountryList().filter { country ->
                    country.name.startsWith(
                        prefix = newSearchString,
                        ignoreCase = true
                    )
                }
            } ?: ccpCountryHelper.getCountryList(),
            showCountrySelectDialog = true.asOneTimeEvent()
        )
    }

    fun requestOtpSend() {
        uiState = uiState.updateState(isLoading = true)
        phoneNumber = phoneNumberUtilHelper.normalizeDigitsOnly(uiState.phoneNumber)?:""
        phoneNumberCountryCode = uiState.selectedCountry.phoneCode
        viewModelScope.launch {
            PayWingsOAuthClient.instance.signInWithPhoneNumberRequestOtp(
                phoneNumberCountryCode = phoneNumberCountryCode,
                phoneNumber = phoneNumber,
                callback = signInWithPhoneNumberRequestOtp
            )
        }
    }

    private val signInWithPhoneNumberRequestOtp = object: SignInWithPhoneNumberRequestOtpCallback {
        override fun onError(error: OAuthErrorCode, errorMessage: String?) {
            uiState = when(error) {
                OAuthErrorCode.TOO_MANY_REQUESTS_FOR_SMS_SEND -> uiState.updateState(systemDialogUiState = SystemDialogUiState.ShowTooManySMSRequest.asOneTimeEvent())
                OAuthErrorCode.INTERNET_CONNECTION_ISSUE -> uiState.updateState(systemDialogUiState = SystemDialogUiState.ShowNoInternetConnection.asOneTimeEvent())
                OAuthErrorCode.INVALID_PHONE_NUMBER -> uiState.updateState(errorMessageResId = R.string.sign_in_request_otp_screen_error_invalid_phone_number)
                OAuthErrorCode.USER_IS_SUSPENDED -> uiState.updateState(errorMessageResId = R.string.sign_in_request_otp_screen_error_phone_number_suspended)
                else -> uiState.updateState(systemDialogUiState = SystemDialogUiState.ShowError(errorMessage = error.description).asOneTimeEvent())
            }
        }

        override fun onShowOtpInputScreen(otpLength: Int) {
            navigateToRoute(SignInOtpVerificationNav.routeWithArguments(otpLength = otpLength, phoneNumberCountryCode = phoneNumberCountryCode, phoneNumber = phoneNumber, phoneNumberFormatted = phoneNumberUtilHelper.convertToUserDisplayFormat(phoneNumber = uiState.phoneNumber, country = uiState.selectedCountry)?:""))
        }

        override fun onShowTimeBasedOtpVerificationInputScreen(accountName: String) {
            navigateToRoute(SignInTimeBasedOtpVerificationNav.routeWithArguments(accountName))
        }
    }

    fun recheckInternetConnection() {
        when (networkState.isConnected) {
            true -> requestOtpSend()
            false -> uiState =
                uiState.updateState(systemDialogUiState = SystemDialogUiState.ShowNoInternetConnection.asOneTimeEvent())
        }
    }

}