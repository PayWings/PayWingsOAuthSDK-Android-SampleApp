package com.paywings.oauth.android.sample_app.util

import android.content.Context
import android.telephony.PhoneNumberUtils
import com.hbb20.CCPCountry
import dagger.hilt.android.qualifiers.ApplicationContext
import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import io.michaelrocks.libphonenumber.android.Phonenumber
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper class for libphonenumber-android library.
 */
@Singleton
class PhoneNumberUtilHelper @Inject constructor(@ApplicationContext val context: Context) {

    private val phoneNumberUtil = PhoneNumberUtil.createInstance(context)

    /**
     * Get info about country phone number (hint template and length).
     *
     * @param country for which we want phone number information.
     * @return country phone number info. (phone number template and length).
     */
    fun getCountryPhoneNumberInfo(country: CCPCountry): CountryPhoneNumberInfo? {
        phoneNumberUtil.getExampleNumberForType(country.nameCode.uppercase(Locale.ROOT), PhoneNumberUtil.PhoneNumberType.MOBILE)?.takeIf { it.hasNationalNumber() }?.let { phoneNumber ->
            PhoneNumberUtils.formatNumber(country.getCountryCodeWithPlus() + phoneNumber.nationalNumber,
                country.nameCode.uppercase(Locale.ROOT)
            )?.substring(country.getCountryCodeWithPlus().length)?.trim()?.let {
                return CountryPhoneNumberInfo(
                    phoneNumberTemplate = it,
                    phoneNumberLength = phoneNumber.nationalNumber.toString().length
                )
            }
        }

        return null
    }

    /**
     * Validates if entered phone number is pattern correct.
     *
     * @param phoneNumber for validation.
     * @param country for which phone number must be valid.
     * @return true - if phone number is valid; false - if not.
     */
    fun isPhoneNumberValid(phoneNumber: String?, country: CCPCountry?): Boolean {
        return try {
            convertToPhoneNumber(phoneNumber = phoneNumber, country = country)?.let {
                phoneNumberUtil.isValidNumber(it)
            } ?: false
        } catch (ex: NumberParseException) {
            false
        }
    }

    /**
     * Formats phone number to OAuth required format.
     *
     * @param phoneNumber for validation.
     * @param country for which phone number is.
     * @return formatted phone number string
     */
    fun convertToOAuthFormat(phoneNumber: String?, country: CCPCountry?): String? {
        return convertToPhoneNumber(phoneNumber = phoneNumber, country = country)?.let {
            phoneNumberUtil.format(it, PhoneNumberUtil.PhoneNumberFormat.E164)
        }
    }

    /**
     * Formats phone number for user display.
     *
     * @param phoneNumber for validation.
     * @param country for which phone number is.
     * @return formatted phone number string
     */
    fun convertToUserDisplayFormat(phoneNumber: String?, country: CCPCountry?): String? {
        return convertToPhoneNumber(phoneNumber = phoneNumber, country = country)?.let {
            phoneNumberUtil.format(it, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
        }
    }

    private fun convertToPhoneNumber(phoneNumber: String?, country: CCPCountry?): Phonenumber.PhoneNumber? {
        return phoneNumber?.takeIf { it.isNotBlank() }?.let { phoneNumberString ->
            PhoneNumberUtil.normalizeDigitsOnly(phoneNumberString)?.takeIf { it.isNotBlank() }?.let { normalizedPhoneNumber ->
                country?.let {
                    phoneNumberUtil.parse(country.getCountryCodeWithPlus() + normalizedPhoneNumber, country.nameCode)
                }
            }
        }
    }

}

data class CountryPhoneNumberInfo(
    val phoneNumberTemplate: String,
    val phoneNumberLength: Int
)